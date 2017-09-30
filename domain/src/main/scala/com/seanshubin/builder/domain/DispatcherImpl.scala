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

  override def cloneProject(name: String, previousAttemptCount: Int): Future[CommandResult] = {
    val command = Seq("git", "clone", s"https://github.com/SeanShubin/$name.git")
    val logger = loggerFactory.createCommandProjectRetry("clone", name, previousAttemptCount)
    val environment = Map[String, String]()
    val input = ProcessInput(command, baseDirectory, environment)
    val futureProcessOutput = processLauncher.launch(input, logger)
    for {
      processOutput <- futureProcessOutput
    } yield {
      CommandResult("clone", name, processOutput, previousAttemptCount)
    }
  }

  override def buildProject(name: String, previousAttemptCount: Int): Future[CommandResult] = {
    val mvnCleanVerify = Seq("mvn", "clean", "verify")
    val settingsAnnotation = systemSpecific.mavenSettings match {
      case Some(mavenSettings) => Seq("--settings", mavenSettings)
      case None => Seq()
    }
    val mavenCommand = mvnCleanVerify ++ settingsAnnotation
    val logger = loggerFactory.createCommandProjectRetry("build", name, previousAttemptCount)
    val directory = baseDirectory.resolve(name)
    val environment = Map[String, String]()
    val input = ProcessInput(mavenCommand, directory, environment)
    val futureProcessOutput = processLauncher.launch(input, logger)
    for {
      processOutput <- futureProcessOutput
    } yield {
      CommandResult("build", name, processOutput, previousAttemptCount)
    }
  }

  override def done(): Unit = {
    donePromise.success(())
  }

  override def statusUpdate(statusOfProjects: StatusOfProjects): Unit = statusUpdateFunction(statusOfProjects)

  override def statusSummary(statusOfProjects: StatusOfProjects): Unit = statusSummaryFunction(statusOfProjects)

  override def unsupportedEventFromState(eventName: String, stateName: String): Unit = unsupportedEventFromStateFunction(eventName, stateName)
}
