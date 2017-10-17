package com.seanshubin.builder.domain

import java.nio.file.Path

import com.seanshubin.uptodate.logic.SummaryReport

import scala.concurrent.{ExecutionContext, Future, Promise}

class DispatcherImpl(githubProjectFinder: ProjectFinder,
                     localProjectFinder: ProjectFinder,
                     systemSpecific: SystemSpecific,
                     baseDirectory: Path,
                     processLauncher: ProcessLauncher,
                     loggerFactory: ProcessLoggerFactory,
                     dependencyUpgrader: DependencyUpgrader,
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

  override def clone(projectName: String): Future[ProcessOutput] = {
    val command = Seq("git", "clone", s"https://github.com/SeanShubin/$projectName.git")
    val logDirectoryName = commandToLogDirectoryName(command)
    val logger = loggerFactory.createProjectCommand(projectName, logDirectoryName)
    val environment = Map[String, String]()
    val input = ProcessInput(command, baseDirectory, environment)
    processLauncher.launch(input, logger)
  }

  override def build(projectName: String): Future[ProcessOutput] = {
    val mvnCleanVerify = Seq("mvn", "clean", "verify")
    val settingsAnnotation = systemSpecific.mavenSettings match {
      case Some(mavenSettings) => Seq("--settings", mavenSettings)
      case None => Seq()
    }
    val mavenCommand = mvnCleanVerify ++ settingsAnnotation
    execProjectCommand(projectName, mavenCommand: _*)
  }

  override def checkForPendingEdits(projectName: String): Future[ProcessOutput] = {
    execProjectCommand(projectName, "git", "status", "-s")
  }

  override def fetch(projectName: String): Future[ProcessOutput] = {
    execProjectCommand(projectName, "git", "fetch")
  }

  override def merge(projectName: String): Future[ProcessOutput] = {
    execProjectCommand(projectName, "git", "merge")
  }

  override def push(projectName: String): Future[ProcessOutput] = {
    execProjectCommand(projectName, "git", "push")
  }

  override def done(): Unit = {
    donePromise.success(())
  }

  override def statusUpdate(statusOfProjects: StatusOfProjects): Unit = statusUpdateFunction(statusOfProjects)

  override def statusSummary(statusOfProjects: StatusOfProjects): Unit = statusSummaryFunction(statusOfProjects)

  override def unsupportedEventFromState(eventName: String, stateName: String): Unit = unsupportedEventFromStateFunction(eventName, stateName)

  override def upgradeDependencies(projectName: String): Future[SummaryReport] = {
    Future {
      dependencyUpgrader.upgradeDependencies(projectName)
    }
  }

  override def addCommitPushUpdates(projectName: String): Future[ProcessOutput] = {
    val stream = Stream(
      execProjectCommand(projectName, "git", "add", "--all"),
      execProjectCommand(projectName, "git", "commit", "-m", "Automatically upgraded dependencies to latest"),
      execProjectCommand(projectName, "git", "push")
    )
    execChainedCommands(stream)
  }

  private def execChainedCommands(stream: Stream[Future[ProcessOutput]]): Future[ProcessOutput] = {
    ???
  }

  private def execProjectCommand(projectName: String, command: String*): Future[ProcessOutput] = {
    val directory = baseDirectory.resolve(projectName)
    val environment = Map[String, String]()
    val input = ProcessInput(command, directory, environment)
    val logDirectoryName = commandToLogDirectoryName(command)
    val logger = loggerFactory.createProjectCommand(projectName, logDirectoryName)
    processLauncher.launch(input, logger)
  }

  private def commandToLogDirectoryName(command: Seq[String]): String = {
    val alpha = ('a' to 'z') ++ ('A' to 'Z')
    val allAlpha: String => Boolean = s => s.forall(alpha.contains)
    val relevantCommandParts = command.filter(allAlpha)
    relevantCommandParts.mkString("-")
  }
}
