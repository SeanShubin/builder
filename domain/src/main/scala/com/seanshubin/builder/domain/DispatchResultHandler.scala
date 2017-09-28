package com.seanshubin.builder.domain

import akka.typed.ActorRef
import com.seanshubin.builder.domain.Event._

import scala.util.{Success, Try}

class DispatchResultHandler(actorRef: ActorRef[Event]) {
  def foundLocalProjects(result: Try[Seq[String]]): Unit = {
    result match {
      case Success(names) => actorRef ! ProjectsFoundLocally(names)
    }
  }

  def foundRemoteProjects(result: Try[Seq[String]]): Unit = {
    result match {
      case Success(names) => actorRef ! ProjectsFoundInGithub(names)
    }
  }

  def finishedCloning(result: Try[CommandResult]): Unit = {
    result match {
      case Success(cloneResult) =>
        if (cloneResult.isSuccess) {
          actorRef ! BuildProject(cloneResult.project, previousAttemptCount = 0)
        } else if (cloneResult.shouldRetry) {
          actorRef ! CloneProject(cloneResult.project, cloneResult.previousAttemptCount + 1)
        } else {
          actorRef ! FailedToClone(cloneResult.project)
        }
    }
  }

  def finishedBuilding(result: Try[CommandResult]): Unit = {
    result match {
      case Success(buildResult) =>
        if (buildResult.isSuccess) {
          actorRef ! ProjectBuilt(buildResult.project)
        } else if (buildResult.shouldRetry) {
          actorRef ! BuildProject(buildResult.project, buildResult.previousAttemptCount + 1)
        } else {
          actorRef ! FailedToBuild(buildResult.project)
        }
    }
  }

  def missingFromGithub(name: String): Unit = {
    actorRef ! MissingFromGithub(name)
  }
}
