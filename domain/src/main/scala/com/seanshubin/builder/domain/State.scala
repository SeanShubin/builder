package com.seanshubin.builder.domain

import com.seanshubin.builder.domain.Event._
import com.seanshubin.builder.domain.ProjectState.{InGithubNotLocal, InLocalAndGithub, InLocalNotGithub}

sealed trait State {
  final def handle(event: Event): (State, Seq[Effect]) = {
    handlePartially().orElse(handleDefault())(event)
  }

  def handleDefault(): PartialFunction[Event, (State, Seq[Effect])] = {
    case event =>
      val eventName = ClassUtil.getSimpleClassName(event)
      val stateName = ClassUtil.getSimpleClassName(this)
      val effects = Seq(
        Effect.UnsupportedEventFromState(eventName, stateName),
        Effect.Done)
      (this, effects)
  }

  def handlePartially(): PartialFunction[Event, (State, Seq[Effect])]
}

object State {

  case object Initial extends State {
    override def handlePartially(): PartialFunction[Event, (State, Seq[Effect])] = {
      case Start =>
        val effects = Seq(
          Effect.FindLocalProjects,
          Effect.FindRemoteProjects
        )
        (State.Searching, effects)
    }
  }

  case object Searching extends State {
    override def handlePartially(): PartialFunction[Event, (State, Seq[Effect])] = {
      case ProjectsFoundLocally(names) =>
        (SearchingForRemoteProjects(names), Seq())
      case ProjectsFoundInGithub(names) =>
        (SearchingForLocalProjects(names), Seq())
    }
  }

  case class SearchingForRemoteProjects(localProjectNames: Seq[String]) extends State {
    override def handlePartially(): PartialFunction[Event, (State, Seq[Effect])] = {
      case ProjectsFoundInGithub(remoteProjectNames) =>
        beginProcessing(localProjectNames, remoteProjectNames)
    }
  }

  case class SearchingForLocalProjects(remoteProjectNames: Seq[String]) extends State {
    override def handlePartially(): PartialFunction[Event, (State, Seq[Effect])] = {
      case ProjectsFoundLocally(localProjectNames) =>
        beginProcessing(localProjectNames, remoteProjectNames)
    }
  }

  case class Processing(statusOfProjects: StatusOfProjects) extends State {
    override def handlePartially(): PartialFunction[Event, (State, Seq[Effect])] = {
      case NoPendingEdits(projectName) => update(projectName, ProjectState.Fetching, Effect.Fetch(projectName))
      case ProjectFetched(projectName) => update(projectName, ProjectState.Merging, Effect.Merge(projectName))
      case ProjectMerged(projectName) => update(projectName, ProjectState.PushingAfterMerge, Effect.Push(projectName))
      case ProjectPushed(projectName) => update(projectName, ProjectState.Upgrading, Effect.UpgradeDependencies(projectName))
      case UpdatesWereApplied(projectName) => update(projectName, ProjectState.Adding, Effect.AddAfterUpdate(projectName))
      case UpdatesAdded(projectName) => update(projectName, ProjectState.Committing, Effect.CommitAfterUpdate(projectName))
      case UpdatesCommitted(projectName) => update(projectName, ProjectState.PushingAfterUpgrade, Effect.PushAfterUpdate(projectName))
      case UpdatesPushed(projectName) => update(projectName, ProjectState.BuildingAfterUpgrade, Effect.Build(projectName))
      case NoUpdatesWereNeeded(projectName) => update(projectName, ProjectState.Building, Effect.Build(projectName))
      case HasPendingEdits(projectName) => update(projectName, ProjectState.HasPendingEdits)
      case FailedToClone(projectName, failReason) => update(projectName, ProjectState.FailedToClone, Effect.LogFailure("clone", projectName, failReason))
      case FailedToBuild(projectName, failReason) => update(projectName, ProjectState.FailedToBuild, Effect.LogFailure("build", projectName, failReason))
      case ProjectBuilt(projectName) => update(projectName, ProjectState.BuildSuccess)
      case ProjectCloned(projectName) => update(projectName, ProjectState.BuildingAfterClone, Effect.Build(projectName))
      case FailedToUpgradeDependencies(projectName, failReason) => update(projectName, ProjectState.FailedToUpdateDependencies, Effect.LogFailure("upgrade", projectName, failReason))
      case MissingFromGithub(projectName) => update(projectName, ProjectState.InLocalNotGithub)
      case FailedToMerge(projectName, failReason) => update(projectName, ProjectState.FailedToMerge, Effect.LogFailure("merge", projectName, failReason))
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

  def processProject(name: String, state: ProjectState): Effect = {
    state match {
      case InGithubNotLocal => Effect.Clone(name)
      case InLocalNotGithub => Effect.MissingFromGithub(name)
      case InLocalAndGithub => Effect.CheckForPendingEdits(name)
      case unexpected => Effect.UnableToProcessProjectInThisState(name, unexpected)
    }
  }
}
