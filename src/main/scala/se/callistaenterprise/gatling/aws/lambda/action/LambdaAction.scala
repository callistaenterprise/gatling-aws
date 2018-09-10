package se.callistaenterprise.gatling.aws.lambda.action

import se.callistaenterprise.gatling.aws.protocol.AwsProtocol
import se.callistaenterprise.gatling.aws.lambda.LambdaCheck

import io.gatling.commons.validation._
import io.gatling.core.action._
import io.gatling.core.check.Check
import io.gatling.core.session.{ Session, Expression }
import io.gatling.core.stats.message.ResponseTimings
import io.gatling.core.stats.StatsEngine
import io.gatling.commons.stats.Status
import io.gatling.core.util.NameGen
import akka.actor.{ ActorSystem, Props }

import com.amazonaws.services.lambda.AWSLambdaClient
import com.amazonaws.services.lambda.model.InvokeRequest
import com.amazonaws.services.lambda.model.InvokeResult

import java.nio.ByteBuffer


object LambdaAction extends NameGen {

  def apply(functionName: Expression[String], payload: Option[Expression[String]], checks: List[LambdaCheck], protocol: AwsProtocol, system: ActorSystem, statsEngine: StatsEngine, next: Action) = {
    val actor = system.actorOf(LambdaActionActor.props(functionName, payload, checks, protocol, statsEngine, next))
    new ExitableActorDelegatingAction(genName("Lambda"), statsEngine, next, actor)
  }
}

object LambdaActionActor {
  def props(functionName: Expression[String], payload: Option[Expression[String]], checks: List[LambdaCheck], protocol: AwsProtocol, statsEngine: StatsEngine, next: Action): Props =
    Props(new LambdaActionActor(functionName, payload, checks, protocol, statsEngine, next))
}

class LambdaActionActor(
    functionName: Expression[String],
    payload: Option[Expression[String]],
    checks: List[LambdaCheck],
    protocol: AwsProtocol,
    val statsEngine: StatsEngine,
    val next: Action
) extends ActionActor {

  override def execute(session: Session) = {
    val awsClient = new AWSLambdaClient(protocol.credentialsProvider)
    awsClient.setRegion(protocol.awsRegion)
    val request = new InvokeRequest
    functionName(session).flatMap { resolvedFunctionName =>
      request.setFunctionName(resolvedFunctionName).success
    }
    if (payload.isDefined) {
      payload.get(session).flatMap { resolvePayload =>
        request.setPayload(resolvePayload).success
      }
    }

    var optionalResult : Option[InvokeResult] = None
    var optionalThrowable : Option[Throwable] = None
    
    val startTime = now()
    try {
      optionalResult = Some(awsClient.invoke(request))
    } catch {
      case t: Throwable => optionalThrowable = Some(t)
    }
    val endTime = now()  
    val timings = ResponseTimings(startTime, endTime)
    
    if (optionalThrowable.isEmpty) {
      val result = optionalResult.get
      if (result.getStatusCode >= 200 && result.getStatusCode <= 299) {
        val resultPayload = bytesToString(result.getPayload)
        val (newSession, error) = Check.check(resultPayload, session, checks)
        error match {
          case None                        => {
            statsEngine.logResponse(session, request.getFunctionName(), timings, Status("OK"), None, None)
            next ! newSession(session)
          }
          case Some(Failure(errorMessage)) => {
            statsEngine.logResponse(session, request.getFunctionName(), timings, Status("KO"), None, Some(errorMessage))
            next ! newSession(session).markAsFailed
          }
        }
      } else {
        statsEngine.logResponse(session, request.getFunctionName(), timings, Status("KO"), None, Some(s"Status code ${result.getStatusCode}"))
        next ! session.markAsFailed
      }
    } else {
      val throwable = optionalThrowable.get
      statsEngine.logResponse(session, request.getFunctionName(), timings, Status("KO"), None, Some(throwable.getMessage))
        next ! session.markAsFailed
    }
  }

  @inline
  def bytesToString(buffer: ByteBuffer): String = {
    val bytes = buffer.array()
    return new String(bytes, "UTF-8")
  }

  @inline
  private def now() = System.currentTimeMillis()

}
