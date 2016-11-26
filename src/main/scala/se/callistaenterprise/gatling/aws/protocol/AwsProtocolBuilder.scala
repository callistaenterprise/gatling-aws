package se.callistaenterprise.gatling.aws.protocol

import com.amazonaws.regions.Region

object AwsProtocolBuilderBase {
  def accessKey(accessKey: String) = AwsProtocolBuilderSecretKeyStep(accessKey)
}

case class AwsProtocolBuilderSecretKeyStep(accessKey: String) {
  def secretKey(secretKey: String) = AwsProtocolBuilderRegionStep(accessKey, secretKey)
}

case class AwsProtocolBuilderRegionStep(accessKey: String, secretKey: String) {
  def region(region: Region) = AwsProtocolBuilder(accessKey, secretKey, region)
}

case class AwsProtocolBuilder(accessKey: String, secretKey: String, region: Region) {

  def build = AwsProtocol(
        awsAccessKeyId = accessKey,
        awsSecretAccessKey = secretKey,
        awsRegion = region
  )
}
