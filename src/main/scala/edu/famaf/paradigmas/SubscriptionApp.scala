package edu.famaf.paradigmas

import akka.actor.typed.ActorSystem
import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}

object SubscriptionApp extends App {
  implicit val formats = DefaultFormats

  val logger: Logger = LoggerFactory.getLogger("edu.famaf.paradigmas.SubscriptionApp")
  val subscriptionsFilePath: String = "./subscriptions.json"

  case class Feed(id: String, name: String, url: String)

  private def readSubscriptions(filename: String): List[Feed] = {
    List(
      Feed("chicago_tribune_business", "Chicago Tribune: Business", "https://www.chicagotribune.com/arcio/rss/category/business/"),
      Feed("nytimes_business", "New York Times: Business", "https://rss.nytimes.com/services/xml/rss/nyt/Business.xml")
    )
  }

  val system = ActorSystem[Supervisor.SupervisorCommand](Supervisor(), "subscription-app")
  system ! Supervisor.LoadSubscriptions(readSubscriptions(subscriptionsFilePath))
}
