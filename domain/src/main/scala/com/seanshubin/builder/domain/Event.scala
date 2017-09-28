package com.seanshubin.builder.domain

sealed trait Event

object Event {

  case object Initialize extends Event

  case class ProjectsFoundInGithub(names: Seq[String]) extends Event

  case class MissingFromGithub(name: String) extends Event

  case class ProjectsFoundLocally(names: Seq[String]) extends Event

  case class ErrorFindingProjectsInGithub(ex: Throwable) extends Event

  case class ErrorFindingProjectsLocally(ex: Throwable) extends Event

  case class Cloned(name: String) extends Event

  case class ErrorCloning(name: String, exception: Throwable) extends Event

  case class Built(name: String) extends Event

  case class ErrorBuilding(name: String, exception: Throwable) extends Event

  case class CloneProject(name: String, previousAttemptCount: Int) extends Event

  case class BuildProject(name: String, previousAttemptCount: Int) extends Event

  case class FailedToClone(name: String) extends Event

  case class ProjectFinished(name: String) extends Event

  case object WantStatusUpdate extends Event
}
