package com.seanshubin.builder.domain

import scala.concurrent.{ExecutionContext, Future}

class DispatcherImpl(githubProjectFinder: ProjectFinder,
                     localProjectFinder: ProjectFinder,
                     projectCommandRunner: ProjectCommandRunner)
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
    projectCommandRunner.exec("build", name, previousAttemptCount, "mvn", "clean", "verify")
  }
}
