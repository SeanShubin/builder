package com.seanshubin.builder.domain

import scala.concurrent.{ExecutionContext, Future, Promise}

class DispatcherImpl(githubProjectFinder: ProjectFinder,
                     localProjectFinder: ProjectFinder,
                     projectCommandRunner: ProjectCommandRunner,
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
    projectCommandRunner.exec("clone", name, previousAttemptCount, "git", "clone", s"https://github.com/SeanShubin/$name.git")
  }

  override def buildProject(name: String, previousAttemptCount: Int): Future[CommandResult] = {
    projectCommandRunner.exec("build", name, previousAttemptCount, "mvn", "clean", "verify", "--settings", "/Users/sshubin/.m2/sean-settings.xml")
  }

  override def done(): Unit = {
    donePromise.success(())
  }

  override def statusUpdate(statusOfProjects: StatusOfProjects): Unit = statusUpdateFunction(statusOfProjects)

  override def statusSummary(statusOfProjects: StatusOfProjects): Unit = statusSummaryFunction(statusOfProjects)

  override def unsupportedEventFromState(eventName: String, stateName: String): Unit = unsupportedEventFromStateFunction(eventName, stateName)
}
