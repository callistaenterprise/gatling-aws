package se.callistaenterprise.gatling.aws.lambda.process

import se.callistaenterprise.gatling.aws.lambda.action.LambdaActionBuilder
import se.callistaenterprise.gatling.aws.lambda.LambdaCheck
import se.callistaenterprise.gatling.aws.lambda.check.LambdaCheckSupport
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.session.Expression

case class LambdaProcessBuilder(functionName: Expression[String], payload: Option[Expression[String]] = None, checks: List[LambdaCheck] = Nil) extends LambdaCheckSupport {
  /**
   * Set payload.
   */
  def payload(payload: Expression[String]) = copy(payload = Some(payload))

    /**
   * Add a check that will be perfomed on the response payload before giving Gatling on OK/KO response
   */
  def check(lambdaChecks: LambdaCheck*) = copy(checks = checks ::: lambdaChecks.toList)

  def build(): ActionBuilder = LambdaActionBuilder(functionName, payload, checks)
}