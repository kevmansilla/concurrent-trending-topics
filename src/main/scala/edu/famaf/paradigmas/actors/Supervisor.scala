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
}

class Supervisor(context: ActorContext[Supervisor.SupervisorCommand])
    extends AbstractBehavior[Supervisor.SupervisorCommand](context) {
  context.log.info("Supervisor Started")

  import Supervisor._

  override def onMessage(msg: SupervisorCommand): Behavior[SupervisorCommand] = {
    msg match {
      case Subs(id,name,url) => {
        val new_subscription = ActorSystem[Site.SiteCommand](Site(), "site")
        new_subscription ! Site.Httpget(id,name,url)
        new_subscription ! Site.Stop()
        Behaviors.same
      }
      case answer(id,name,feed) => {
        val storage = ActorSystem[Storage.StorageCommand](Storage(), "storage")
        storage ! Storage.store(id,name,feed)
        storage ! Storage.Stop()
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