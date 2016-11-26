package se.callistaenterprise.gatling.aws.lambda.check

import scala.collection.mutable

import io.gatling.commons.validation._
import io.gatling.core.check.CheckResult
import io.gatling.core.session.Session
import se.callistaenterprise.gatling.aws.lambda._

case class LambdaCustomCheck(func: String => Boolean, failureMessage: String = "Lambda check failed") extends LambdaCheck {
  override def check(response: String, session: Session)(implicit cache: mutable.Map[Any, Any]): Validation[CheckResult] = {
    func(response) match {
      case true => CheckResult.NoopCheckResultSuccess
      case _    => Failure(failureMessage)
    }
  }
}
