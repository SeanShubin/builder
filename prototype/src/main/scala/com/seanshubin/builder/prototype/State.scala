package com.seanshubin.builder.prototype

import akka.typed.ActorRef
import com.seanshubin.builder.prototype.Event.{Found, Processed, Ready}

import scala.concurrent.ExecutionContext

sealed trait State {
  def update(name: String, status: Int): State = ???

  def results: Map[String, Int] = ???

  def handle(event: Event, dispatcher: Dispatcher, actorRef: ActorRef[Event])(implicit executionContext: ExecutionContext): State = {
    val stateName: String = getClass.getSimpleName
    throw new UnsupportedOperationException(s"State $stateName does not support event $event")
  }
}

object State {

  case object Initial extends State {
    override def handle(event: Event, dispatcher: Dispatcher, actorRef: ActorRef[Event])(implicit executionContext: ExecutionContext): State = {
      val handler = new DispatchResultHandler(actorRef)
      event match {
        case Ready =>
          dispatcher.doSearch().onComplete(handler.searchResult)
          State.Searching
        case _ => super.handle(event, dispatcher, actorRef)
      }
    }
  }

  case object Searching extends State {
    override def handle(event: Event, dispatcher: Dispatcher, actorRef: ActorRef[Event])(implicit executionContext: ExecutionContext): State = {
      val handler = new DispatchResultHandler(actorRef)
      event match {
        case Found(names) => names.foreach(dispatcher.process(_).onComplete(handler.processResult))
          val statusOfProjects = StatusOfProjects.fromNames(names)
          State.Processing(statusOfProjects)
        case _ => super.handle(event, dispatcher, actorRef)
      }
    }
  }

  case class Processing(status: StatusOfProjects) extends State {
    override def handle(event: Event, dispatcher: Dispatcher, actorRef: ActorRef[Event])(implicit executionContext: ExecutionContext): State = {
      event match {
        case Processed(name, code) =>
          val newStatus = status.completeProject(name, code)
          if (newStatus.isDone) {
            dispatcher.setDone()
          }
          Processing(newStatus)
        case _ => super.handle(event, dispatcher, actorRef)
      }
    }
  }

  case object Error extends State

}
