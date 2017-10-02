package com.seanshubin.builder.domain

import akka.typed.ActorRef
import com.seanshubin.builder.domain.Event._

import scala.util.{Failure, Success, Try}

class DispatchResultHandler(actorRef: ActorRef[Event]) {
  def foundLocalProjects(result: Try[Seq[String]]): Unit = {
    result match {
      case Success(names) => actorRef ! ProjectsFoundLocally(names)
      case Failure(exception) => actorRef ! ErrorFindingProjectsLocally(exception)
    }
  }

  def foundRemoteProjects(result: Try[Seq[String]]): Unit = {
    result match {
      case Success(names) => actorRef ! ProjectsFoundInGithub(names)
      case Failure(exception) => actorRef ! ErrorFindingProjectsLocally(exception)
    }
  }

  def finishedCloning(result: Try[CommandResult]): Unit = {
    result match {
      case Success(cloneResult) =>
        if (cloneResult.isSuccess) {
          actorRef ! BuildProject(cloneResult.project)
        } else {
          actorRef ! FailedToCloneBasedOnExitCode(cloneResult.project)
        }
      case Failure(exception) => FailedToCloneBasedOnException(exception)
    }
  }

  def finishedBuilding(result: Try[CommandResult]): Unit = {
    result match {
      case Success(buildResult) =>
        if (buildResult.isSuccess) {
          actorRef ! ProjectBuilt(buildResult.project)
        } else {
          actorRef ! FailedToBuildBasedOnExitCode(buildResult.project)
        }

    }
  }

  def missingFromGithub(name: String): Unit = {
    actorRef ! MissingFromGithub(name)
  }
}
