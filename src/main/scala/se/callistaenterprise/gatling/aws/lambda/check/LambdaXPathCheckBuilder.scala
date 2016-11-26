package se.callistaenterprise.gatling.aws.lambda.check

import java.io.StringReader

import io.gatling.commons.validation._
import io.gatling.core.check._
import io.gatling.core.check.extractor.xpath._

import org.xml.sax.InputSource

import se.callistaenterprise.gatling.aws.lambda._

object LambdaXPathCheckBuilder extends XPathCheckBuilder[LambdaCheck, String] {

  private val ErrorMapper: String => String = "Could not parse response into a DOM Document: " + _

  def preparer[T](f: InputSource => T)(payload: String): Validation[Option[T]] =
    safely(ErrorMapper) {
      Some(f(new InputSource(new StringReader(payload)))).success
    }

  val CheckBuilder: Extender[LambdaCheck, String] = (wrapped: LambdaCheck) => wrapped
}
