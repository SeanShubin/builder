package com.seanshubin.builder.domain

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class DispatcherImpl(githubProjectFinder: ProjectFinder,
                     localProjectFinder: ProjectFinder,
                     projectCommandRunner: ProjectCommandRunner,
                     eventListener: Event => Unit)(implicit executionContext: ExecutionContext) extends Dispatcher {
  override def findLocalProjects(): Unit = {
    val futureResult = localProjectFinder.findProjects()
    futureResult.onComplete {
      case Success(value) =>
        val event = Event.ProjectsFoundLocally(value)
        eventListener(event)
      case Failure(exception) =>
        val event = Event.ErrorFindingProjectsLocally(exception)
        eventListener(event)
    }
  }

  override def findRemoteProjects(): Unit = {
    val futureResult = localProjectFinder.findProjects()
    futureResult.onComplete {
      case Success(value) =>
        val event = Event.ProjectsFoundInGithub(value)
        eventListener(event)
      case Failure(exception) =>
        val event = Event.ErrorFindingProjectsInGithub(exception)
        eventListener(event)
    }
  }

  override def cloneProject(name: String): Unit = {
    val futureResult = projectCommandRunner.exec("git", "clone", s"https://github.com/SeanShubin/$name.git")
    futureResult.onComplete {
      case Success(value) =>
        val event = Event.Cloned(name)
        eventListener(event)
      case Failure(exception) =>
        val event = Event.ErrorCloning(name, exception)
        eventListener(event)
    }
  }

  override def buildProject(name: String): Unit = {
    val futureResult = projectCommandRunner.exec("mvn", "clean", "verify")
    futureResult.onComplete {
      case Success(value) =>
        val event = Event.Built(name)
        eventListener(event)
      case Failure(exception) =>
        val event = Event.ErrorBuilding(name, exception)
        eventListener(event)
    }
  }
}
