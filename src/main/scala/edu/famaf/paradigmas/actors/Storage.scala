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
  final case class Stop() extends StorageCommand
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
        val fileWriter = new PrintWriter(new File(s"./output/${id}.txt"))
        fileWriter.write(s"${feedTitle}\n\n${feedContent}")
        fileWriter.close()
        Behaviors.same
      }
      case Stop() => {
        Behaviors.stopped
      }
    }
  }

  override def onSignal: PartialFunction[Signal, Behavior[StorageCommand]] = {
    case PostStop =>
      context.log.info("Storage Stopped")
      this
  }
}