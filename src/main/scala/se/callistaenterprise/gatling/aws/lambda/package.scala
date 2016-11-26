package se.callistaenterprise.gatling.aws

import io.gatling.core.check.{ Check, Preparer, Extender }
import io.gatling.commons.validation.Success

package object lambda {

  /**
   * Type for Lambda checks
   */
  type LambdaCheck = Check[String]
  
  val LambdaStringExtender: Extender[LambdaCheck, String] = 
     (check: LambdaCheck) => check
  
  val LambdaStringPreparer: Preparer[String, String] = 
     (result: String) => Success(result)
  
}
