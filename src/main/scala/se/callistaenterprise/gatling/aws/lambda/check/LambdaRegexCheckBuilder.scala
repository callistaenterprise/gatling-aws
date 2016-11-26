
package se.callistaenterprise.gatling.aws.lambda.check

import se.callistaenterprise.gatling.aws.lambda._

import io.gatling.core.check.DefaultMultipleFindCheckBuilder
import io.gatling.core.check.extractor.regex._
import io.gatling.core.session.{ Expression, RichExpression }

import com.amazonaws.services.lambda.model.InvokeResult

trait LambdaRegexOfType { self: LambdaRegexCheckBuilder[String] =>

  def ofType[X: GroupExtractor](implicit extractorFactory: RegexExtractorFactory) = new LambdaRegexCheckBuilder[X](expression)
}

object LambdaRegexCheckBuilder {

  def regex(expression: Expression[String])(implicit extractorFactory: RegexExtractorFactory) =
    new LambdaRegexCheckBuilder[String](expression) with LambdaRegexOfType
}

class LambdaRegexCheckBuilder[X: GroupExtractor](private[check] val expression: Expression[String])(implicit extractorFactory: RegexExtractorFactory)
    extends DefaultMultipleFindCheckBuilder[LambdaCheck, String, CharSequence, X](LambdaStringExtender, LambdaStringPreparer) {
  import extractorFactory._

  def findExtractor(occurrence: Int) = expression.map(newSingleExtractor[X](_, occurrence))
  def findAllExtractor = expression.map(newMultipleExtractor[X])
  def countExtractor = expression.map(newCountExtractor)
}
