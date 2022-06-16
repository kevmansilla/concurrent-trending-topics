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
import scala.collection.mutable.{Map=>Dict}

object Storage {
  def apply(): Behavior[StorageCommand] = Behaviors.setup(context =>
    new Storage(context))

  sealed trait StorageCommand
  final case class Store(answer: Dict[List[String], Seq[String]])
    extends StorageCommand
}

class Storage(context: ActorContext[Storage.StorageCommand])
    extends AbstractBehavior[Storage.StorageCommand](context) {
  context.log.info("Storage Started")

  import Storage._

  override def onMessage(msg: StorageCommand): Behavior[StorageCommand] = {
    msg match {
      case Store(answer) => {
        answer.keys.foreach{
          feed: List[String] => {
            val feedTitle = s"News from: ${feed(1)}"
            val feedContent = answer(feed)
            val fileWriter =
              new PrintWriter(new File(s"./output/${feed(0)}.txt"))
            fileWriter.write(s"${feedTitle}\n\n${feedContent}")
            fileWriter.close()
          }
        }
        Behaviors.same
      }
    }
  }
}
