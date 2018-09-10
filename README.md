# gatling-aws
Gatling custom protocol for AWS Lambda. Use an [`AWSCredentialsProvider`](https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/index.html?com/amazonaws/auth/InstanceProfileCredentialsProvider.html)
to provide AWS credentials to the protocol

```java
package my.gatling.lambda

import io.gatling.core.Predef._
import se.callistaenterprise.gatling.aws.Predef._
import com.amazonaws.auth.InstanceProfileCredentialsProvider

import scala.concurrent.duration._

import com.amazonaws.regions.{Region, Regions}

class LambdaGatlingTest extends Simulation {

  // when running in an EC2 instance with an attached profile we can use the InstanceProfileCredentialsProvider
  // for all other cases pass in an implementation of the AWSCredentialsProvider object:
  // https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/index.html?com/amazonaws/auth/InstanceProfileCredentialsProvider.html
  val awsConfig = Aws
    .credentialsProvider(new InstanceProfileCredentialsProvider(true))
    .region(Region.getRegion(Regions.US_WEST_2))


  val lambdaScenario = scenario("Call lambda")
    .exec(
      lambda("MyLambdaFunction").payload("""{"some":"json"}""")
        .check(jsonPath("$[?(@.id != '')]"))
    )


  setUp(lambdaScenario.inject(
        constantUsersPerSec(50) during (60 seconds)))
          .throttle(reachRps(20) in (10 seconds), holdFor(50 seconds))
          .protocols(awsConfig)
```