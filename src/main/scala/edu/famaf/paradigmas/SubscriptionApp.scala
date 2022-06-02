package edu.famaf.paradigmas

import akka.actor.typed.ActorSystem
import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import scala.io._

object SubscriptionApp extends App {
  implicit val formats = DefaultFormats

  val logger: Logger = LoggerFactory.getLogger("edu.famaf.paradigmas.SubscriptionApp")
  val subscriptionsFilePath: String = "./subscriptions.json"

  case class Subscription(
    id: String,
    name: String,
    url: String
  )

  private def readSubscriptions(filename: String): List[Subscription] = {
    println("reading subscriptions")
    val JsonContent = Source.fromFile(filename)
    (parse(JsonContent.mkString)).extract[List[Subscription]]
  }

  val system = ActorSystem[Supervisor.SupervisorCommand](Supervisor(), "subscription-app")
  val readSub = readSubscriptions(subscriptionsFilePath)
  readSub.foreach { s =>
    system ! Supervisor.Subs(s.id, s.name, s.url)
  }
  val SLEEP_TIME = 10000
  Thread.sleep(SLEEP_TIME)
  system ! Supervisor.Stop()
}
