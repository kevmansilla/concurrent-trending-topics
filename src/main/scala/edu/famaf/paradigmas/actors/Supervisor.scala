package edu.famaf.paradigmas

import akka.actor.typed.ActorSystem
import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.PostStop
import akka.actor.typed.Signal
import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.scaladsl.AbstractBehavior
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.LoggerOps
import java.io.{File, PrintWriter}

object Supervisor {
  def apply(): Behavior[SupervisorCommand] = Behaviors.setup(context => new Supervisor(context))

  sealed trait SupervisorCommand
  final case class Subs (
    id: String,
    name: String, 
    url: String
  ) extends SupervisorCommand

  final case class answer (
    id: String,
    name: String,
    feed: Seq[String]
  ) extends SupervisorCommand

  final case class Stop() extends SupervisorCommand
  final case class SendFeed(feed: String) extends SupervisorCommand
}

class Supervisor(context: ActorContext[Supervisor.SupervisorCommand])
    extends AbstractBehavior[Supervisor.SupervisorCommand](context) {
  context.log.info("Supervisor Started")

  import Supervisor._

  override def onMessage(msg: SupervisorCommand): Behavior[SupervisorCommand] = {
    msg match {
      case Subs(id,name,url) => {
        val new_subscription = context.spawn(Site(), s"New_Sub_${id}")
        new_subscription ! Site.Httpget(id,name,url)
        Behaviors.same
      }
      case answer(id,name,feed) => {
        val storage = context.spawn(Storage(), s"New_File_${id}")
        storage ! Storage.store(id,name,feed)
        Behaviors.same
      }
      case Stop() => {
        Behaviors.stopped
      }
    }
  }

  override def onSignal: PartialFunction[Signal, Behavior[SupervisorCommand]] = {
    case PostStop =>
      context.log.info("Supervisor Stopped")
      this
  }
}