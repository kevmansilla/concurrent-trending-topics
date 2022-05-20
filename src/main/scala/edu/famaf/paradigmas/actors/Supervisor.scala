package edu.famaf.paradigmas

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
  final case class LoadSubscriptions(subscriptions: List[SubscriptionApp.Feed])
    extends SupervisorCommand
}

class Supervisor(context: ActorContext[Supervisor.SupervisorCommand])
    extends AbstractBehavior[Supervisor.SupervisorCommand](context) {
  context.log.info("Supervisor Started")

  import Supervisor._

  override def onMessage(msg: SupervisorCommand): Behavior[SupervisorCommand] = {
    msg match {
      case LoadSubscriptions(subscriptions) => {
        // TODO: Fix this, move to the corresponding module
        subscriptions.foreach { feed =>
          // TODO: Parse the XML with the feed content
          val feedTitle = s"News from: ${feed.name}"
          val feedContent = "Content of the feed"

          val fileWriter = new PrintWriter(new File(s"./output/${feed.id}.txt"))
          fileWriter.write(s"${feedTitle}\n\n${feedContent}")
          fileWriter.close()
        }

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