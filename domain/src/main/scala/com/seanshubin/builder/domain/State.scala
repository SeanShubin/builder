package com.seanshubin.builder.domain

import akka.typed.ActorRef
import com.seanshubin.builder.domain.Event.{Initialize, ProjectsFoundInGithub, ProjectsFoundLocally}

import scala.concurrent.ExecutionContext.Implicits.global

sealed trait State {
  def handle(event: Event, dispatcher: Dispatcher, actorRef: ActorRef[Event]): State
}

object State {
  case object Initial extends State{
    override def handle(event: Event, dispatcher: Dispatcher, actorRef: ActorRef[Event]): State = {
      val handler = new DispatchResultHandler(actorRef)
      event match {
        case Initialize =>
          dispatcher.findRemoteProjects().onComplete(handler.foundRemoteProjects)
          dispatcher.findLocalProjects().onComplete(handler.foundLocalProjects)
          State.Searching
      }
    }
  }

  case object Searching extends State {
    override def handle(event: Event, dispatcher: Dispatcher, actorRef: ActorRef[Event]): State = {
      event match {
        case ProjectsFoundLocally(names) =>
          SearchingForRemoteProjects(names)
        case ProjectsFoundInGithub(names) =>
          SearchingForLocalProjects(names)
      }
    }
  }

  case class SearchingForRemoteProjects(localProjectNames:Seq[String]) extends State{
    override def handle(event: Event, dispatcher: Dispatcher, actorRef: ActorRef[Event]): State = {
      event match {
        case ProjectsFoundInGithub(remoteProjectNames) =>
          val stateMap = ProjectLogic.createStateMap(localProjectNames, remoteProjectNames)
          Processing(stateMap)
      }
    }
  }

  case class SearchingForLocalProjects(remoteProjectNames:Seq[String]) extends State{
    override def handle(event: Event, dispatcher: Dispatcher, actorRef: ActorRef[Event]): State = {
      event match {
        case ProjectsFoundLocally(localProjectNames) =>
          val stateMap = ProjectLogic.createStateMap(localProjectNames, remoteProjectNames)
          Processing(stateMap)
      }
    }
  }

  case class Processing(stateMap:Map[String, ProjectState])extends State{
    override def handle(event: Event, dispatcher: Dispatcher, actorRef: ActorRef[Event]): State = ???
  }

}
