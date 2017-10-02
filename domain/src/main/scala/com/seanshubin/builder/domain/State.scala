package com.seanshubin.builder.domain

import akka.typed.ActorRef
import com.seanshubin.builder.domain.Event._
import com.seanshubin.builder.domain.ProjectState.{InGithubNotLocal, InLocalAndGithub, InLocalNotGithub}

import scala.concurrent.ExecutionContext.Implicits.global

sealed trait State {
  final def handle(event: Event, dispatcher: Dispatcher, actorRef: ActorRef[Event]): State = {
    handlePartially(dispatcher, actorRef).orElse(handleDefault(dispatcher, actorRef))(event)
  }

  def handleDefault(dispatcher: Dispatcher, actorRef: ActorRef[Event]): PartialFunction[Event, State] = {
    case event =>
      dispatcher.unsupportedEventFromState(event.toString, this.toString)
      dispatcher.done()
      this
  }

  def handlePartially(dispatcher: Dispatcher, actorRef: ActorRef[Event]): PartialFunction[Event, State]
}

object State {

  case object Initial extends State {
    override def handlePartially(dispatcher: Dispatcher, actorRef: ActorRef[Event]): PartialFunction[Event, State] = {
      case Initialize =>
        val handler = new DispatchResultHandler(actorRef)
        dispatcher.findRemoteProjects().onComplete(handler.foundRemoteProjects)
        dispatcher.findLocalProjects().onComplete(handler.foundLocalProjects)
        State.Searching
    }
  }

  case object Searching extends State {
    override def handlePartially(dispatcher: Dispatcher, actorRef: ActorRef[Event]): PartialFunction[Event, State] = {
      case ProjectsFoundLocally(names) =>
        SearchingForRemoteProjects(names)
      case ProjectsFoundInGithub(names) =>
        SearchingForLocalProjects(names)
    }
  }

  case class SearchingForRemoteProjects(localProjectNames: Seq[String]) extends State {
    override def handlePartially(dispatcher: Dispatcher, actorRef: ActorRef[Event]): PartialFunction[Event, State] = {
      case ProjectsFoundInGithub(remoteProjectNames) =>
        beginProcessing(localProjectNames, remoteProjectNames, dispatcher, actorRef)
    }
  }

  case class SearchingForLocalProjects(remoteProjectNames: Seq[String]) extends State {
    override def handlePartially(dispatcher: Dispatcher, actorRef: ActorRef[Event]): PartialFunction[Event, State] = {
      case ProjectsFoundLocally(localProjectNames) =>
        beginProcessing(localProjectNames, remoteProjectNames, dispatcher, actorRef)
    }
  }

  case class Processing(statusOfProjects: StatusOfProjects) extends State {
    override def handlePartially(dispatcher: Dispatcher, actorRef: ActorRef[Event]): PartialFunction[Event, State] = {
      case FailedToCloneBasedOnExitCode(project) => update(dispatcher, project, ProjectState.FailedToClone)
      case FailedToBuildBasedOnExitCode(project) => update(dispatcher, project, ProjectState.FailedToBuild)
      case ProjectBuilt(project) => update(dispatcher, project, ProjectState.BuildSuccess)
    }

    def update(dispatcher: Dispatcher, project: String, newProjectState: ProjectState): State = {
      val newStatus = statusOfProjects.update(project, newProjectState)
      dispatcher.statusUpdate(newStatus)
      if (newStatus.isDone) {
        dispatcher.statusSummary(newStatus)
        dispatcher.done()
      }
      Processing(newStatus)
    }
  }

  def beginProcessing(localProjectNames: Seq[String], remoteProjectNames: Seq[String], dispatcher: Dispatcher, actorRef: ActorRef[Event]): State = {
    val statusOfProjects = StatusOfProjects.create(localProjectNames, remoteProjectNames)
    val processProjectFunction = (processProject(_: String, _: ProjectState, dispatcher, actorRef)).tupled
    statusOfProjects.map.par.foreach(processProjectFunction)
    Processing(statusOfProjects)
  }

  def processProject(name: String, state: ProjectState, dispatcher: Dispatcher, actorRef: ActorRef[Event]): Unit = {
    val handler = new DispatchResultHandler(actorRef)
    state match {
      case InGithubNotLocal => dispatcher.cloneProject(name).onComplete(handler.finishedCloning)
      case InLocalNotGithub => handler.missingFromGithub(name)
      case InLocalAndGithub => dispatcher.buildProject(name).onComplete(handler.finishedBuilding)
      case unexpected => handler.unableToProcessProjectInThisState(name, unexpected)
    }
  }
}
