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
import scala.concurrent.duration._
import akka.util.Timeout

class FeedRequester {

  def cleanContent(texts: Seq[String]): Try[Seq[String]] = {
    val word = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]".r
    Try(texts.map(content => word.replaceAllIn(content, " ")))
  }

  def parserRequest(url: String): Seq[String] = {
    val xmlContent = XML.loadString(url)
    val texts = (xmlContent \\ "item").map {item =>
      (item \ "title").text + " " + (item \ "description").text
    }
    cleanContent(texts) match {
      case Success(x) => x
      case Failure(e) => List()
    }

  }
}

object Feed {
  def apply(): Behavior[FeedCommand] = Behaviors.setup(context => new Feed(context))
  sealed trait FeedCommand
  final case class ParseRequest(
    id: String,
    name: String,
    url: String,
    feed: String,
    replyTo : ActorRef[FeedResponse]
  ) extends FeedCommand

  sealed trait FeedResponse
  final case class FeedMessage(msg: Seq[String]) extends FeedResponse

}

class Feed(context: ActorContext[Feed.FeedCommand])
    extends AbstractBehavior[Feed.FeedCommand](context) {
  context.log.info("Feed Started")
  import Feed._

  override def onMessage(msg: FeedCommand): Behavior[FeedCommand] = {
    msg match {
      case ParseRequest(id,name,url,feed,replyTo) => {
        val frequest = new FeedRequester
        val answer = frequest.parserRequest(feed)
        replyTo ! FeedMessage(answer)
        Behaviors.same
      }
    }
  }
}
