package com.seanshubin.builder.domain

import scala.concurrent.Future

trait Dispatcher {
  def findLocalProjects(): Future[Seq[String]]

  def findRemoteProjects(): Future[Seq[String]]

  def cloneProject(name: String): Future[ProcessOutput]

  def buildProject(name: String): Future[ProcessOutput]

  def checkForPendingEdits(name: String): Future[ProcessOutput]

  def statusUpdate(statusOfProjects: StatusOfProjects): Unit

  def statusSummary(statusOfProjects: StatusOfProjects): Unit

  def unsupportedEventFromState(eventName: String, stateName: String): Unit

  def done(): Unit
}
