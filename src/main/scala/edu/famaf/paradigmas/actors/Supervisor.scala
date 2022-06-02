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
import scala.concurrent.duration._
import akka.util.Timeout
import java.io.{File, PrintWriter}
import scala.util.{Try,Success,Failure}

object Supervisor {
  def apply(): Behavior[SupervisorCommand] = Behaviors.setup(context => new Supervisor(context))

  sealed trait SupervisorCommand
  final case class Subs (
    id: String,
    name: String, 
    url: String
  ) extends SupervisorCommand

  final case class Stop() extends SupervisorCommand
  final case class SiteResponse(id: String,name: String,msg: Seq[String]) extends SupervisorCommand
  final case class SiteFailed(msg: String) extends SupervisorCommand
}

class Supervisor(context: ActorContext[Supervisor.SupervisorCommand])
    extends AbstractBehavior[Supervisor.SupervisorCommand](context) {
  context.log.info("Supervisor Started")

  implicit val timeout: Timeout = 3.seconds
  import Supervisor._

  override def onMessage(msg: SupervisorCommand): Behavior[SupervisorCommand] = {
    msg match {
      case SiteResponse(id,name,feed) => {
        val store = context.spawn(Storage(), s"New_File_${id}.txt")
        store ! Storage.store(id,name,feed)
        Behaviors.same
      }
      case SiteFailed(msg) => {
        context.log.error(msg)
        Behaviors.same
      }
      case Subs(id,name,url) => {
        val site = context.spawn(Site(), s"New_Site_${id}")
        context.ask(site, Site.Httpget(id,name,url,_)) {
          case Success(Site.SiteMessage(text)) => SiteResponse(id,name,text)
          case Failure(e) => SiteFailed(e.getMessage)
        }
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