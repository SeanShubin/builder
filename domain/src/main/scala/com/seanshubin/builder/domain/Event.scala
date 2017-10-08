package com.seanshubin.builder.domain

sealed trait Event

object Event {

  case object Initialize extends Event

  case class ProjectsFoundInGithub(names: Seq[String]) extends Event

  case class MissingFromGithub(name: String) extends Event

  case class ProjectsFoundLocally(names: Seq[String]) extends Event

  case class ErrorFindingProjectsInGithub(ex: Throwable) extends Event

  case class ErrorFindingProjectsLocally(ex: Throwable) extends Event

  case class FailedToClone(name: String, reason: FailReason) extends Event

  case class FailedToBuild(name: String, reason: FailReason) extends Event

  case class ProjectBuilt(name: String) extends Event

  case class ProjectCloned(name: String) extends Event

  case class UnableToProcessProjectInThisState(project: String, stateName: String) extends Event

  case class NoPendingEdits(projectName: String) extends Event

  case class HasPendingEdits(projectName: String) extends Event

  case class FailedToGetPendingEdits(projectName: String, reason: FailReason) extends Event

}
