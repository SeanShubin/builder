package com.seanshubin.builder.domain

import java.nio.file.Path

import scala.concurrent.{ExecutionContext, Future, Promise}

class DispatcherImpl(githubProjectFinder: ProjectFinder,
                     localProjectFinder: ProjectFinder,
                     systemSpecific: SystemSpecific,
                     baseDirectory: Path,
                     processLauncher: ProcessLauncher,
                     loggerFactory: ProcessLoggerFactory,
                     statusUpdateFunction: StatusOfProjects => Unit,
                     statusSummaryFunction: StatusOfProjects => Unit,
                     unsupportedEventFromStateFunction: (String, String) => Unit,
                     donePromise: Promise[Unit])
                    (implicit executionContext: ExecutionContext) extends Dispatcher {
  override def findLocalProjects(): Future[Seq[String]] = {
    localProjectFinder.findProjects()
  }

  override def findRemoteProjects(): Future[Seq[String]] = {
    githubProjectFinder.findProjects()
  }

  override def cloneProject(name: String): Future[CommandResult] = {
    val command = Seq("git", "clone", s"https://github.com/SeanShubin/$name.git")
    val logger = loggerFactory.createProjectCommand("clone", name)
    val environment = Map[String, String]()
    val input = ProcessInput(command, baseDirectory, environment)
    val futureProcessOutput = processLauncher.launch(input, logger)
    for {
      processOutput <- futureProcessOutput
    } yield {
      CommandResult("clone", name, processOutput)
    }
  }

  override def buildProject(name: String): Future[CommandResult] = {
    val mvnCleanVerify = Seq("mvn", "clean", "verify")
    val settingsAnnotation = systemSpecific.mavenSettings match {
      case Some(mavenSettings) => Seq("--settings", mavenSettings)
      case None => Seq()
    }
    val mavenCommand = mvnCleanVerify ++ settingsAnnotation
    val logger = loggerFactory.createProjectCommand("build", name)
    val directory = baseDirectory.resolve(name)
    val environment = Map[String, String]()
    val input = ProcessInput(mavenCommand, directory, environment)
    val futureProcessOutput = processLauncher.launch(input, logger)
    for {
      processOutput <- futureProcessOutput
    } yield {
      CommandResult("build", name, processOutput)
    }
  }

  override def checkForPendingEdits(name: String): Future[PendingEditResult] = {
    val command = Seq("git", "status", "-s")
    val directory = baseDirectory.resolve(name)
    val environment = Map[String, String]()
    val input = ProcessInput(command, directory, environment)
    val logger = loggerFactory.createProjectCommand("status", name)
    val futureProcessOutput = processLauncher.launch(input, logger)
    for {
      processOutput <- futureProcessOutput
    } yield {
      if (processOutput.exitCode == 0) {
        if (processOutput.outputLines.isEmpty) {
          PendingEditResult.NoPendingEdits(name)
        } else {
          PendingEditResult.HasPendingEdits(name)
        }
      } else {
        PendingEditResult.UnableToDeterminePendingEdits(name)
      }
    }
  }

  override def done(): Unit = {
    donePromise.success(())
  }

  override def statusUpdate(statusOfProjects: StatusOfProjects): Unit = statusUpdateFunction(statusOfProjects)

  override def statusSummary(statusOfProjects: StatusOfProjects): Unit = statusSummaryFunction(statusOfProjects)

  override def unsupportedEventFromState(eventName: String, stateName: String): Unit = unsupportedEventFromStateFunction(eventName, stateName)
}
