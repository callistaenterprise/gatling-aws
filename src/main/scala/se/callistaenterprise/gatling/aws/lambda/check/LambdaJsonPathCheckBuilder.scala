package se.callistaenterprise.gatling.aws.lambda.check

import se.callistaenterprise.gatling.aws.lambda._

import io.gatling.core.check.{ DefaultMultipleFindCheckBuilder, Preparer }
import io.gatling.core.check.extractor.jsonpath._
import io.gatling.core.json.JsonParsers
import io.gatling.core.session.{ Expression, RichExpression }

trait LambdaJsonPathOfType {
  self: LambdaJsonPathCheckBuilder[String] =>

  def ofType[X: JsonFilter](implicit extractorFactory: JsonPathExtractorFactory) = new LambdaJsonPathCheckBuilder[X](path, jsonParsers)
}

object LambdaJsonPathCheckBuilder {

  val CharsParsingThreshold = 200 * 1000
  
  def preparer(jsonParsers: JsonParsers): Preparer[String, Any] =
    response => {
      if (response.length() > CharsParsingThreshold || jsonParsers.preferJackson)
        jsonParsers.safeParseJackson(response)
      else
        jsonParsers.safeParseBoon(response)
    }

  def jsonPath(path: Expression[String])(implicit extractorFactory: JsonPathExtractorFactory, jsonParsers: JsonParsers) =
    new LambdaJsonPathCheckBuilder[String](path, jsonParsers) with LambdaJsonPathOfType
}

class LambdaJsonPathCheckBuilder[X: JsonFilter](
  private[check] val path:        Expression[String],
  private[check] val jsonParsers: JsonParsers
)(implicit extractorFactory: JsonPathExtractorFactory)
    extends DefaultMultipleFindCheckBuilder[LambdaCheck, String, Any, X](
      LambdaStringExtender,
      LambdaJsonPathCheckBuilder.preparer(jsonParsers)
    ) {

  import extractorFactory._

  def findExtractor(occurrence: Int) = path.map(newSingleExtractor[X](_, occurrence))
  def findAllExtractor = path.map(newMultipleExtractor[X])
  def countExtractor = path.map(newCountExtractor)
}
