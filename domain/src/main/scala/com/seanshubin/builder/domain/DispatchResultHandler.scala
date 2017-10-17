package com.seanshubin.builder.domain

import akka.typed.ActorRef
import com.seanshubin.builder.domain.Event._
import com.seanshubin.uptodate.logic.SummaryReport

import scala.util.{Failure, Success, Try}

class DispatchResultHandler(actorRef: ActorRef[Event]) {
  def foundLocalProjects(result: Try[Seq[String]]): Unit = {
    result match {
      case Success(names) => actorRef ! ProjectsFoundLocally(names)
      case Failure(exception) => actorRef ! ErrorFindingProjectsLocally(exception)
    }
  }

  def foundRemoteProjects(result: Try[Seq[String]]): Unit = {
    result match {
      case Success(names) => actorRef ! ProjectsFoundInGithub(names)
      case Failure(exception) => actorRef ! ErrorFindingProjectsLocally(exception)
    }
  }

  def finishedClone(projectName: String): Try[ProcessOutput] => Unit = {
    case Success(processOutput) =>
      if (processOutput.exitCode == 0) {
        actorRef ! ProjectCloned(projectName)
      } else {
        actorRef ! FailedToClone(projectName, FailReason.ExitCode(processOutput.exitCode))
      }
    case Failure(exception) =>
      actorRef ! FailedToClone(projectName, FailReason.ExceptionThrown(exception))
  }

  def finishedBuild(projectName: String): Try[ProcessOutput] => Unit = {
    case Success(processOutput) =>
      if (processOutput.exitCode == 0) {
        actorRef ! ProjectBuilt(projectName)
      } else {
        actorRef ! FailedToBuild(projectName, FailReason.ExitCode(processOutput.exitCode))
      }
    case Failure(exception) =>
      actorRef ! FailedToBuild(projectName, FailReason.ExceptionThrown(exception))
  }

  def finishedFetch(projectName: String): Try[ProcessOutput] => Unit = {
    case Success(processOutput) =>
      if (processOutput.exitCode == 0) {
        actorRef ! ProjectFetched(projectName)
      } else {
        actorRef ! FailedToFetch(projectName, FailReason.ExitCode(processOutput.exitCode))
      }
    case Failure(exception) =>
      actorRef ! FailedToFetch(projectName, FailReason.ExceptionThrown(exception))
  }

  def finishedMerge(projectName: String): Try[ProcessOutput] => Unit = {
    case Success(processOutput) =>
      if (processOutput.exitCode == 0) {
        actorRef ! ProjectMerged(projectName)
      } else {
        actorRef ! FailedToMerge(projectName, FailReason.ExitCode(processOutput.exitCode))
      }
    case Failure(exception) =>
      actorRef ! FailedToMerge(projectName, FailReason.ExceptionThrown(exception))
  }

  def finishedPush(projectName: String): Try[ProcessOutput] => Unit = {
    case Success(processOutput) =>
      if (processOutput.exitCode == 0) {
        actorRef ! ProjectPushed(projectName)
      } else {
        actorRef ! FailedToPush(projectName, FailReason.ExitCode(processOutput.exitCode))
      }
    case Failure(exception) =>
      actorRef ! FailedToPush(projectName, FailReason.ExceptionThrown(exception))
  }

  def finishedCheckingForPendingEdits(projectName: String): Try[ProcessOutput] => Unit = {
    case Success(processOutput) =>
      if (processOutput.exitCode == 0) {
        if (processOutput.outputLines.isEmpty) {
          actorRef ! Event.NoPendingEdits(projectName)
        } else {
          actorRef ! Event.HasPendingEdits(projectName)
        }
      } else {
        actorRef ! Event.FailedToGetPendingEdits(projectName, FailReason.ExitCode(processOutput.exitCode))
      }
    case Failure(exception) =>
      actorRef ! Event.FailedToGetPendingEdits(projectName, FailReason.ExceptionThrown(exception))
  }

  def missingFromGithub(name: String): Unit = {
    actorRef ! MissingFromGithub(name)
  }

  def unableToProcessProjectInThisState(project: String, state: ProjectState): Unit = {
    actorRef ! UnableToProcessProjectInThisState(project, ClassUtil.getSimpleClassName(state))
  }

  def finishedUpgradingDependencies(projectName: String): Try[SummaryReport] => Unit = {
    case Success(summaryReport) =>
      if (summaryReport.updatesWereApplied) {
        actorRef ! Event.UpdatesWereApplied(projectName)
      } else {
        actorRef ! Event.NoUpdatesWereNeeded(projectName)
      }
    case Failure(_) =>
      actorRef ! Event.FailedToUpgradeDependencies(projectName)
  }
}
