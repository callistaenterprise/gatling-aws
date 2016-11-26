package se.callistaenterprise.gatling.aws.protocol

import io.gatling.core.protocol.ProtocolComponents
import io.gatling.core.session.Session

import akka.actor.ActorRef

case class AwsComponents(awsProtocol: AwsProtocol) extends ProtocolComponents {

  def onStart: Option[Session => Session] = None
  def onExit: Option[Session => Unit] = None
}
