package com.seanshubin.builder.domain

import akka.typed.ActorRef
import com.seanshubin.builder.domain.Event.{Initialize, ProjectsFoundInGithub, ProjectsFoundLocally}
import com.seanshubin.builder.domain.ProjectState.{InGithubNotLocal, InLocalAndGithub, InLocalNotGithub}

import scala.concurrent.ExecutionContext.Implicits.global

sealed trait State {
  def handle(event: Event, dispatcher: Dispatcher, actorRef: ActorRef[Event]): State
}

object State {

  case object Initial extends State {
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

  case class SearchingForRemoteProjects(localProjectNames: Seq[String]) extends State {
    override def handle(event: Event, dispatcher: Dispatcher, actorRef: ActorRef[Event]): State = {
      event match {
        case ProjectsFoundInGithub(remoteProjectNames) =>
          beginProcessing(localProjectNames, remoteProjectNames, dispatcher, actorRef)
      }
    }
  }

  case class SearchingForLocalProjects(remoteProjectNames: Seq[String]) extends State {
    override def handle(event: Event, dispatcher: Dispatcher, actorRef: ActorRef[Event]): State = {
      event match {
        case ProjectsFoundLocally(localProjectNames) =>
          beginProcessing(localProjectNames, remoteProjectNames, dispatcher, actorRef)
      }
    }
  }

  case class Processing(stateMap: Map[String, ProjectState]) extends State {
    override def handle(event: Event, dispatcher: Dispatcher, actorRef: ActorRef[Event]): State = ???
  }

  def beginProcessing(localProjectNames: Seq[String], remoteProjectNames: Seq[String], dispatcher: Dispatcher, actorRef: ActorRef[Event]): State = {
    val stateMap = ProjectLogic.createStateMap(localProjectNames, remoteProjectNames)
    val processProjectFunction = (processProject(_: String, _: ProjectState, dispatcher, actorRef)).tupled
    stateMap.foreach(processProjectFunction)
    Processing(stateMap)
  }

  def processProject(name: String, state: ProjectState, dispatcher: Dispatcher, actorRef: ActorRef[Event]): Unit = {
    val handler = new DispatchResultHandler(actorRef)
    state match {
      case InGithubNotLocal => dispatcher.cloneProject(name, 0).onComplete(handler.finishedCloning)
      case InLocalNotGithub => handler.missingFromGithub(name)
      case InLocalAndGithub => dispatcher.buildProject(name, 0).onComplete(handler.finishedBuilding)
    }
  }
}
