package se.callistaenterprise.gatling.aws.lambda.check

import io.gatling.core.session.Expression
import io.gatling.core.check.extractor.regex._
import io.gatling.core.check.extractor.jsonpath.JsonPathExtractorFactory
import io.gatling.core.check.extractor.xpath.{ JdkXPathExtractorFactory, SaxonXPathExtractorFactory }
import io.gatling.core.json.JsonParsers

trait LambdaCheckSupport {

  def regex(expression: Expression[String])(implicit extractorFactory: RegexExtractorFactory) =
    LambdaRegexCheckBuilder.regex(expression)

  def xpath(expression: Expression[String], namespaces: List[(String, String)] = Nil)(implicit extractorFactory: SaxonXPathExtractorFactory, jdkXPathExtractorFactory: JdkXPathExtractorFactory) =
    LambdaXPathCheckBuilder.xpath(expression, namespaces)

  def jsonPath(path: Expression[String])(implicit extractorFactory: JsonPathExtractorFactory, jsonParsers: JsonParsers) =
    LambdaJsonPathCheckBuilder.jsonPath(path)

  def customCheck = LambdaCustomCheck
  
}
