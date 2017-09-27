package com.seanshubin.builder.domain

import scala.concurrent.Future

trait Dispatcher {
  def findLocalProjects(): Future[Seq[String]]

  def findRemoteProjects(): Future[Seq[String]]

  def cloneProject(name: String, previousAttemptCount: Int): Future[CommandResult]

  def buildProject(name: String, previousAttemptCount: Int): Future[CommandResult]
}
