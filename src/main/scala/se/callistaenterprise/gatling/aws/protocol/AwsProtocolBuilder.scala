package se.callistaenterprise.gatling.aws.protocol

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.regions.Region

object AwsProtocolBuilderBase {
  def credentialsProvider(provider: AWSCredentialsProvider) = AwsProtocolBuilderRegionStep(provider)
}

case class AwsProtocolBuilderRegionStep(provider: AWSCredentialsProvider) {
  def region(region: Region) = AwsProtocolBuilder(provider, region)
}

case class AwsProtocolBuilder(provider: AWSCredentialsProvider, region: Region) {

  def build = AwsProtocol(
        credentialsProvider = provider,
        awsRegion = region
  )
}
