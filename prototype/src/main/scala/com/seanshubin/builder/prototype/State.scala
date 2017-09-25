package com.seanshubin.builder.prototype

import akka.typed.ActorRef
import com.seanshubin.builder.prototype.Event.{Found, Processed, Ready}

import scala.concurrent.ExecutionContext

sealed trait State {
  def isDone: Boolean = false

  def update(name: String, status: Int): State = ???

  def results: Map[String, Int] = ???

  def handle(event: Event, dispatcher: Dispatcher, actorRef: ActorRef[Event])(implicit executionContext: ExecutionContext): State = {
    throw new UnsupportedOperationException(this.getClass.getName)
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
      }
    }
  }

  case class Processing(status: StatusOfProjects) extends State {
    override def handle(event: Event, dispatcher: Dispatcher, actorRef: ActorRef[Event])(implicit executionContext: ExecutionContext): State = {
      event match {
        case Processed(name, code) =>
          val newStatus = status.completeProject(name, code)
          Processing(newStatus)
      }
    }

    override def isDone: Boolean = {
      def isNameDone(name: String, result: Option[Int]): Boolean = {
        result.isDefined
      }

      status.isDone
    }
  }

}
