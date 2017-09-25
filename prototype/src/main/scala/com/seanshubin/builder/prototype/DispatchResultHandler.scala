package com.seanshubin.builder.prototype

import akka.typed.ActorRef
import com.seanshubin.builder.prototype.Event.{Found, Processed}

import scala.util.{Success, Try}

class DispatchResultHandler(actorRef: ActorRef[Event]) {
  def searchResult(result: Try[Seq[String]]): Unit = {
    result match {
      case Success(names) => actorRef ! Found(names)
    }
  }

  def processResult(result: Try[ProcessResult]): Unit = {
    result match {
      case Success(ProcessResult(name, code)) => actorRef ! Processed(name, code)
    }
  }
}
