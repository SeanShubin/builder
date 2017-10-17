package com.seanshubin.builder.domain

sealed trait Event

object Event {

  case object Start extends Event

  case class ProjectsFoundInGithub(names: Seq[String]) extends Event

  case class MissingFromGithub(name: String) extends Event

  case class ProjectsFoundLocally(names: Seq[String]) extends Event

  case class ErrorFindingProjectsInGithub(ex: Throwable) extends Event

  case class ErrorFindingProjectsLocally(ex: Throwable) extends Event

  case class FailedToClone(name: String, reason: FailReason) extends Event

  case class FailedToBuild(name: String, reason: FailReason) extends Event

  case class ProjectBuilt(name: String) extends Event

  case class ProjectCloned(name: String) extends Event

  case class ProjectFetched(name: String) extends Event

  case class FailedToFetch(name: String, reason: FailReason) extends Event

  case class ProjectMerged(name: String) extends Event

  case class FailedToMerge(name: String, reason: FailReason) extends Event

  case class ProjectPushed(name: String) extends Event

  case class FailedToPush(name: String, reason: FailReason) extends Event

  case class UnableToProcessProjectInThisState(project: String, stateName: String) extends Event

  case class NoPendingEdits(projectName: String) extends Event

  case class HasPendingEdits(projectName: String) extends Event

  case class FailedToGetPendingEdits(projectName: String, reason: FailReason) extends Event

  case class UpdatesWereApplied(projectName: String) extends Event

  case class NoUpdatesWereNeeded(projectName: String) extends Event

  case class FailedToUpgradeDependencies(projectName: String) extends Event

  case class UpdatesPushed(projectName: String) extends Event

  case class UpdatesCommitted(projectName: String) extends Event

  case class UpdatesAdded(projectName: String) extends Event

  case class FailedToAddUpdates(projectName: String, reason: FailReason) extends Event

  case class FailedToCommitUpdates(projectName: String, reason: FailReason) extends Event

  case class FailedToPushUpdates(projectNames: String, reason: FailReason) extends Event

}
