package com.seanshubin.builder.prototype

import akka.typed.ActorRef

import scala.concurrent.ExecutionContext

sealed trait Effect {
  def apply(dispatcher: Dispatcher, actorRef: ActorRef[Event])(implicit executionContext: ExecutionContext): Unit
}

object Effect {

  case object Search extends Effect {
    override def apply(dispatcher: Dispatcher, actorRef: ActorRef[Event])(implicit executionContext: ExecutionContext): Unit = {
      val handler = new DispatchResultHandler(actorRef)
      dispatcher.doSearch().onComplete(handler.searchResult)
    }
  }

  case class Process(name: String) extends Effect {
    override def apply(dispatcher: Dispatcher, actorRef: ActorRef[Event])(implicit executionContext: ExecutionContext): Unit = {
      val handler = new DispatchResultHandler(actorRef)
      dispatcher.process(name).onComplete(handler.processResult)
    }
  }

  case object Done extends Effect {
    override def apply(dispatcher: Dispatcher, actorRef: ActorRef[Event])(implicit executionContext: ExecutionContext): Unit = {
      dispatcher.setDone()
    }
  }

}
