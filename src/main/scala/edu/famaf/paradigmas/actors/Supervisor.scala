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
import scala.collection.mutable.{Map=>Dict}

object Supervisor {
  def apply(): Behavior[SupervisorCommand] = Behaviors.setup(context => new Supervisor(context))

  sealed trait SupervisorCommand
  final case class getAllFeeds() extends SupervisorCommand
  final case class Subs (subs: List[SubscriptionApp.Subscription]) 
    extends SupervisorCommand
  final case class Stop() extends SupervisorCommand
  final case class SiteResponse(id: String, name: String, feed: Seq[String]) extends SupervisorCommand
  final case class SiteFailed(msg: String) extends SupervisorCommand
}

class Supervisor(context: ActorContext[Supervisor.SupervisorCommand])
    extends AbstractBehavior[Supervisor.SupervisorCommand](context) {
  context.log.info("Supervisor Started")

  implicit val timeout: Timeout = 3.seconds
  import Supervisor._

  var sites: List[ActorRef[Site.SiteCommand]] = List()
  var feeds: Dict[List[String],Seq[String]] = Dict()
  var failed: List[String] = List()

  override def onMessage(msg: SupervisorCommand): Behavior[SupervisorCommand] = {
    msg match {
      case SiteResponse(id,name,feed) => {
        val params: List[String] = List(id,name)
        feeds.put(params,feed)
        context.log.info(s"Content from feed ${id}}")
        if(sites.length == feeds.size+failed.length){
          val store = context.spawn(Storage(), s"Store_files")
          store ! Storage.Store(feeds)
        }
        Behaviors.same
      }
      case SiteFailed(msg) => {
        failed = msg :: failed
        context.log.error(msg)
        if(sites.length == feeds.size+failed.length){
          val store = context.spawn(Storage(), s"Store_files")
          store ! Storage.Store(feeds)
        }
        Behaviors.same
      }
      case Subs(subs) => {
        subs.foreach{
          s: SubscriptionApp.Subscription => {
            val site = context.spawn(Site(), s"New_Site_${s.id}")
            sites = site :: sites
            context.ask(site, Site.Httpget(s.id,s.name,s.url,_)) {
              case Success(Site.SiteMessage(text)) => {
                SiteResponse(s.id,s.name,text)
              }
              case Failure(e) => {
                SiteFailed(e.getMessage)
              }
            }
          }
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
