package edu.famaf.paradigmas

import scalaj.http.{Http, HttpResponse}
import scala.xml.XML
import scala.io.Source
import scala.util.{Try,Success,Failure}

import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.jackson.JsonMethods._

import akka.actor.typed.ActorSystem
import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.PostStop
import akka.actor.typed.Signal
import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.scaladsl.AbstractBehavior
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.LoggerOps

class Url {

  def getRequest(url: String): Try[String] = {
    val CONN_TIMEOUT = 2000
    val READ_TIMEOUT = 5000
    Try(Http(url).timeout(connTimeoutMs = CONN_TIMEOUT, readTimeoutMs = READ_TIMEOUT).asString.body)
  }
}


object Site {
  def apply(): Behavior[SiteCommand] = Behaviors.setup(context => new Site(context))
  sealed trait SiteCommand
  final case class Httpget(
    id: String,
    name: String,
    url: String,
  ) extends SiteCommand

}

class Site(context: ActorContext[Site.SiteCommand])
    extends AbstractBehavior[Site.SiteCommand](context) {
  context.log.info("Site Started")
  import Site._
  var word = "%s".r

  override def onMessage(msg: SiteCommand): Behavior[SiteCommand] = {
    msg match {
      case Httpget(id,name,url) => {
        val parsed = context.spawn(Feed(),s"obtain_text:${id}")
        val request = new Url
        request.getRequest(url) match {
          case Success(x) => parsed ! Feed.ParseRequest(id,name,url,x)
          case Failure(e) => ""
        }       
        Behaviors.same
      }
    }
  }
}

