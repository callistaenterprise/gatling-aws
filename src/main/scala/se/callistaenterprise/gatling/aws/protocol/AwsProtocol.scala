package se.callistaenterprise.gatling.aws.protocol

import io.gatling.core.CoreComponents
import io.gatling.core.config.{ GatlingConfiguration, Credentials }
import io.gatling.core.protocol.{ ProtocolKey, Protocol }

import com.amazonaws.regions.Region

import akka.actor.ActorSystem

object AwsProtocol {

  val AwsProtocolKey = new ProtocolKey {

    type Protocol = AwsProtocol
    type Components = AwsComponents

    def protocolClass: Class[io.gatling.core.protocol.Protocol] = classOf[AwsProtocol].asInstanceOf[Class[io.gatling.core.protocol.Protocol]]

    def defaultProtocolValue(configuration: GatlingConfiguration): AwsProtocol = throw new IllegalStateException("Can't provide a default value for AwsProtocol")

    def newComponents(system: ActorSystem, coreComponents: CoreComponents): AwsProtocol => AwsComponents = {
      awsProtocol => AwsComponents(awsProtocol)
    }
  }
}

case class AwsProtocol(
    awsAccessKeyId: String,
    awsSecretAccessKey: String,
    awsRegion: Region
) extends Protocol {

  type Components = AwsComponents
}
