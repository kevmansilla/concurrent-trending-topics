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


object Storage {
  def apply(): Behavior[StorageCommand] = Behaviors.setup(context => new Storage(context))

  sealed trait StorageCommand
  final case class store (
  	id: String,
    name: String,
    feed: Seq[String]
  ) extends StorageCommand
}

class Storage(context: ActorContext[Storage.StorageCommand])
    extends AbstractBehavior[Storage.StorageCommand](context) {
  context.log.info("Storage Started")

  import Storage._

  override def onMessage(msg: StorageCommand): Behavior[StorageCommand] = {
    msg match {
      case store(id,name,feed) => {
      	val feedTitle = s"News from: ${name}"
        val feedContent = feed
        context.log.info(s"${feedTitle}\n\n${feedContent}")
        Behaviors.same
      }
    }
  }
}