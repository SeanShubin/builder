package com.seanshubin.builder.domain

import com.seanshubin.uptodate.logic.SummaryReport

import scala.concurrent.Future

trait Dispatcher {
  def findLocalProjects(): Future[Seq[String]]

  def findRemoteProjects(): Future[Seq[String]]

  def clone(projectName: String): Future[ProcessOutput]

  def build(projectName: String): Future[ProcessOutput]

  def fetch(projectName: String): Future[ProcessOutput]

  def merge(projectName: String): Future[ProcessOutput]

  def add(projectName: String): Future[ProcessOutput]

  def commit(projectName: String): Future[ProcessOutput]

  def push(projectName: String): Future[ProcessOutput]

  def checkForPendingEdits(projectName: String): Future[ProcessOutput]

  def statusUpdate(statusOfProjects: StatusOfProjects): Unit

  def statusSummary(statusOfProjects: StatusOfProjects): Unit

  def unsupportedEventFromState(eventName: String, stateName: String): Unit

  def done(): Unit

  def upgradeDependencies(projectName: String): Future[SummaryReport]

  def logFailure(action: String, projectName: String, failReason: FailReason): Unit
}
