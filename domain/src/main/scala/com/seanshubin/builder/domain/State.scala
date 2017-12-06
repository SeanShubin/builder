package com.seanshubin.builder.domain

import com.seanshubin.builder.domain.Event._
import com.seanshubin.builder.domain.ProjectState.{InGithubNotLocal, InLocalAndGithub, InLocalNotGithub}

sealed trait State {
  final def handle(event: Event): (State, Seq[Effect]) = {
    event match {
      case Start => start()
      case ProjectsFoundInGithub(projectNames) => projectsFoundInGithub(projectNames)
      case MissingFromGithub(projectName) => missingFromGithub(projectName)
      case ProjectsFoundLocally(projectNames) => projectsFoundLocally(projectNames)
      case ErrorFindingProjectsInGithub(ex) => errorFindingProjectsInGithub(ex)
      case ErrorFindingProjectsLocally(ex) => errorFindingProjectsLocally(ex)
      case FailedToClone(projectName, reason) => failedToClone(projectName, reason)
      case FailedToBuild(projectName, reason) => failedToBuild(projectName, reason)
      case ProjectBuilt(projectName) => projectBuilt(projectName)
      case ProjectCloned(projectName) => projectCloned(projectName)
      case ProjectFetched(projectName) => projectFetched(projectName)
      case FailedToFetch(projectName, reason) => failedToFetch(projectName, reason)
      case ProjectMerged(projectName) => projectMerged(projectName)
      case FailedToMerge(projectName, reason) => failedToMerge(projectName, reason)
      case ProjectPushed(projectName) => projectPushed(projectName)
      case FailedToPush(projectName, reason) => failedToPush(projectName, reason)
      case UnableToProcessProjectInThisState(project, stateName) => unableToProcessProjectInThisState(project, stateName)
      case NoPendingEdits(projectName) => noPendingEdits(projectName)
      case HasPendingEdits(projectName) => hasPendingEdits(projectName)
      case FailedToGetPendingEdits(projectName, reason) => failedToGetPendingEdits(projectName, reason)
      case UpdatesWereApplied(projectName) => updatesWereApplied(projectName)
      case NoUpdatesWereNeeded(projectName) => noUpdatesWereNeeded(projectName)
      case FailedToUpgradeDependencies(projectName, reason) => failedToUpgradeDependencies(projectName, reason)
      case UpdatesPushed(projectName) => updatesPushed(projectName)
      case UpdatesCommitted(projectName) => updatesCommitted(projectName)
      case UpdatesAdded(projectName) => updatesAdded(projectName)
      case FailedToAddUpdates(projectName, reason) => failedToAddUpdates(projectName, reason)
      case FailedToCommitUpdates(projectName, reason) => failedToCommitUpdates(projectName, reason)
      case FailedToPushUpdates(projectNames, reason) => failedToPushUpdates(projectNames, reason)
      case Ignored(projectNames) => ignored(projectNames)
    }
  }

  def start(): (State, Seq[Effect]) = {
    unsupported("start")
  }

  def projectsFoundInGithub(remoteProjectNames: Seq[String]): (State, Seq[Effect]) = {
    unsupported(s"projectsFoundInGithub($remoteProjectNames)")
  }

  def missingFromGithub(projectName: String): (State, Seq[Effect]) = {
    unsupported(s"missingFromGithub($projectName)")
  }

  def projectsFoundLocally(localProjectNames: Seq[String]): (State, Seq[Effect]) = {
    unsupported(s"projectsFoundLocally($localProjectNames)")
  }

  def errorFindingProjectsInGithub(ex: Throwable): (State, Seq[Effect]) = {
    unsupported(s"errorFindingProjectsInGithub($ex)")
  }

  def errorFindingProjectsLocally(ex: Throwable): (State, Seq[Effect]) = {
    unsupported(s"errorFindingProjectsLocally($ex)")
  }

  def failedToClone(projectName: String, failReason: FailReason): (State, Seq[Effect]) = {
    unsupported(s"failedToClone($projectName, $failReason)")
  }

  def failedToBuild(projectName: String, failReason: FailReason): (State, Seq[Effect]) = {
    unsupported(s"failedToBuild($projectName, $failReason)")
  }

  def projectBuilt(projectName: String): (State, Seq[Effect]) = {
    unsupported(s"projectBuilt($projectName)")
  }

  def projectCloned(projectName: String): (State, Seq[Effect]) = {
    unsupported(s"projectCloned($projectName)")
  }

  def projectFetched(projectName: String): (State, Seq[Effect]) = {
    unsupported(s"projectFetched($projectName)")
  }

  def failedToFetch(projectName: String, failReason: FailReason): (State, Seq[Effect]) = {
    unsupported(s"failedToFetch($projectName, $failReason)")
  }

  def projectMerged(projectName: String): (State, Seq[Effect]) = {
    unsupported(s"projectMerged($projectName)")
  }

  def failedToMerge(projectName: String, failReason: FailReason): (State, Seq[Effect]) = {
    unsupported(s"failedToMerge($projectName, $failReason)")
  }

  def projectPushed(projectName: String): (State, Seq[Effect]) = {
    unsupported(s"projectPushed($projectName)")
  }

  def failedToPush(projectName: String, failReason: FailReason): (State, Seq[Effect]) = {
    unsupported(s"failedToPush($projectName, $failReason)")
  }

  def unableToProcessProjectInThisState(project: String, stateName: String): (State, Seq[Effect]) = {
    unsupported(s"unableToProcessProjectInThisState($project, $stateName)")
  }

  def noPendingEdits(projectName: String): (State, Seq[Effect]) = {
    unsupported(s"noPendingEdits($projectName)")
  }

  def hasPendingEdits(projectName: String): (State, Seq[Effect]) = {
    unsupported(s"hasPendingEdits($projectName)")
  }

  def failedToGetPendingEdits(projectName: String, failReason: FailReason): (State, Seq[Effect]) = {
    unsupported(s"failedToGetPendingEdits($projectName, $failReason)")
  }

  def updatesWereApplied(projectName: String): (State, Seq[Effect]) = {
    unsupported(s"updatesWereApplied($projectName)")
  }

  def noUpdatesWereNeeded(projectName: String): (State, Seq[Effect]) = {
    unsupported(s"noUpdatesWereNeeded($projectName)")
  }

  def failedToUpgradeDependencies(projectName: String, failReason: FailReason): (State, Seq[Effect]) = {
    unsupported(s"failedToUpgradeDependencies($projectName, $failReason)")
  }

  def updatesPushed(projectName: String): (State, Seq[Effect]) = {
    unsupported(s"updatesPushed($projectName)")
  }

  def updatesCommitted(projectName: String): (State, Seq[Effect]) = {
    unsupported(s"updatesCommitted($projectName)")
  }

  def updatesAdded(projectName: String): (State, Seq[Effect]) = {
    unsupported(s"updatesAdded($projectName)")
  }

  def failedToAddUpdates(projectName: String, failReason: FailReason): (State, Seq[Effect]) = {
    unsupported(s"failedToAddUpdates($projectName, $failReason)")
  }

  def failedToCommitUpdates(projectName: String, failReason: FailReason): (State, Seq[Effect]) = {
    unsupported(s"failedToCommitUpdates($projectName, $failReason)")
  }

  def failedToPushUpdates(projectNames: String, failReason: FailReason): (State, Seq[Effect]) = {
    unsupported(s"failedToPushUpdates($projectNames, $failReason)")
  }

  def ignored(projectNames: String): (State, Seq[Effect]) = {
    unsupported(s"ignored($projectNames)")
  }

  def unsupported(message: String): Nothing = {
    throw new RuntimeException(s"unsupported event: $message")
  }
}

object State {

  case object Initial extends State {
    override def start(): (State, Seq[Effect]) = {
      val effects = Seq(
        Effect.FindLocalProjects,
        Effect.FindRemoteProjects
      )
      (State.Searching, effects)
    }
  }

  case object Searching extends State {
    override def projectsFoundLocally(projectNames: Seq[String]): (State, Seq[Effect]) = {
      (SearchingForRemoteProjects(projectNames), Seq())
    }

    override def projectsFoundInGithub(projectNames: Seq[String]): (State, Seq[Effect]) = {
      (SearchingForLocalProjects(projectNames), Seq())
    }
  }

  case class SearchingForRemoteProjects(localProjectNames: Seq[String]) extends State {
    override def projectsFoundInGithub(remoteProjectNames: Seq[String]): (State, Seq[Effect]) = {
      beginProcessing(localProjectNames, remoteProjectNames)
    }
  }

  case class SearchingForLocalProjects(remoteProjectNames: Seq[String]) extends State {
    override def projectsFoundLocally(localProjectNames: Seq[String]): (State, Seq[Effect]) = {
      beginProcessing(localProjectNames, remoteProjectNames)
    }
  }

  case class Processing(statusOfProjects: StatusOfProjects) extends State {
    override def ignored(projectName: String): (State, Seq[Effect]) = {
      update(projectName, ProjectState.Ignored)
    }

    override def failedToMerge(projectName: String, failReason: FailReason): (State, Seq[Effect]) = {
      update(projectName, ProjectState.FailedToMerge, Effect.LogFailure("merge", projectName, failReason))
    }

    override def missingFromGithub(projectName: String): (State, Seq[Effect]) = {
      update(projectName, ProjectState.InLocalNotGithub)
    }

    override def failedToUpgradeDependencies(projectName: String, failReason: FailReason): (State, Seq[Effect]) = {
      update(projectName, ProjectState.FailedToUpdateDependencies, Effect.LogFailure("upgrade", projectName, failReason))
    }

    override def projectCloned(projectName: String): (State, Seq[Effect]) = {
      update(projectName, ProjectState.UpgradingAfterClone, Effect.UpgradeDependencies(projectName))
    }

    override def failedToBuild(projectName: String, failReason: FailReason): (State, Seq[Effect]) = {
      update(projectName, ProjectState.FailedToBuild, Effect.LogFailure("build", projectName, failReason))
    }

    override def failedToClone(projectName: String, failReason: FailReason): (State, Seq[Effect]) = {
      update(projectName, ProjectState.FailedToClone, Effect.LogFailure("clone", projectName, failReason))
    }

    override def hasPendingEdits(projectName: String): (State, Seq[Effect]) = {
      update(projectName, ProjectState.HasPendingEdits)
    }

    override def noUpdatesWereNeeded(projectName: String): (State, Seq[Effect]) = {
      update(projectName, ProjectState.Building, Effect.Build(projectName))
    }

    override def updatesPushed(projectName: String): (State, Seq[Effect]) = {
      update(projectName, ProjectState.BuildingAfterUpgrade, Effect.Build(projectName))
    }

    override def updatesCommitted(projectName: String): (State, Seq[Effect]) = {
      update(projectName, ProjectState.PushingAfterUpgrade, Effect.PushAfterUpdate(projectName))
    }

    override def updatesAdded(projectName: String): (State, Seq[Effect]) = {
      update(projectName, ProjectState.Committing, Effect.CommitAfterUpdate(projectName))
    }

    override def updatesWereApplied(projectName: String): (State, Seq[Effect]) = {
      update(projectName, ProjectState.Adding, Effect.AddAfterUpdate(projectName))
    }

    override def projectPushed(projectName: String): (State, Seq[Effect]) = {
      update(projectName, ProjectState.Upgrading, Effect.UpgradeDependencies(projectName))
    }

    override def projectMerged(projectName: String): (State, Seq[Effect]) = {
      update(projectName, ProjectState.PushingAfterMerge, Effect.Push(projectName))
    }

    override def noPendingEdits(projectName: String): (State, Seq[Effect]) = {
      update(projectName, ProjectState.Fetching, Effect.Fetch(projectName))
    }

    override def projectFetched(projectName: String): (State, Seq[Effect]) = {
      update(projectName, ProjectState.Merging, Effect.Merge(projectName))
    }

    override def projectBuilt(projectName: String): (State, Seq[Effect]) = {
      if (statusOfProjects.map(projectName) == ProjectState.BuildingAfterUpgrade) {
        update(projectName, ProjectState.UpgradeAndBuildSuccess)
      } else {
        update(projectName, ProjectState.BuildSuccess)
      }
    }

    def update(project: String, newProjectState: ProjectState, newEffects: Effect*): (State, Seq[Effect]) = {
      val newStatus = statusOfProjects.update(project, newProjectState)
      val statusUpdateEffect = Effect.StatusUpdate(newStatus)
      val doneEffects = if (newStatus.isDone) {
        Seq(
          Effect.Summary(newStatus),
          Effect.Done)
      } else {
        Seq()
      }
      val effects = Seq(statusUpdateEffect) ++ newEffects ++ doneEffects
      (Processing(newStatus), effects)
    }
  }

  def beginProcessing(localProjectNames: Seq[String], remoteProjectNames: Seq[String]): (State, Seq[Effect]) = {
    val statusOfProjects = StatusOfProjects.create(localProjectNames, remoteProjectNames)
    val processProjectFunction = (processProject(_: String, _: ProjectState)).tupled
    val effects: Seq[Effect] = statusOfProjects.map.toSeq.map(processProjectFunction)
    (Processing(statusOfProjects), effects)
  }

  def processProject(projectName: String, state: ProjectState): Effect = {
    state match {
      case InGithubNotLocal => Effect.Clone(projectName)
      case InLocalNotGithub => Effect.MissingFromGithub(projectName)
      case InLocalAndGithub => Effect.CheckForPendingEdits(projectName)
      case unexpected => Effect.UnableToProcessProjectInThisState(projectName, unexpected)
    }
  }
}
