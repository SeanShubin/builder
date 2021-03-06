package com.seanshubin.builder.domain

import java.time.Instant

import akka.typed.Signal

trait Notifications {
  def projectsFoundInGithub(names: Seq[String]): Unit

  def projectsFoundLocally(names: Seq[String]): Unit

  def errorFindingProjectsInGithub(): Unit

  def errorFindingProjectsLocally(ex: Throwable): Unit

  def unhandledException(ex: Throwable): Unit

  def event(event: Event): Unit

  def signal(signal: Signal): Unit

  def processLaunched(processInput: ProcessInput): Unit

  def statusUpdate(statusOfProjects: StatusOfProjects): Unit

  def statusSummary(statusOfProjects: StatusOfProjects): Unit

  def startAndEndTime(start: Instant, end: Instant): Unit

  def unsupportedEventFromState(eventName: String, stateName: String): Unit
}
