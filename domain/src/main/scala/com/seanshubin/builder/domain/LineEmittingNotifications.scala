package com.seanshubin.builder.domain

import java.time.{Duration, Instant}

import akka.typed.Signal
import com.seanshubin.devon.domain.DevonMarshallerWiring

class LineEmittingNotifications(emit: String => Unit, statefulLogger: StatefulLogger) extends Notifications {
  override def projectsFoundInGithub(names: Seq[String]): Unit = {
    names.map(x => s"project found in github: $x").foreach(emit)
  }

  override def projectsFoundLocally(names: Seq[String]): Unit = {
    names.map(x => s"project found locally: $x").foreach(emit)
  }

  override def errorFindingProjectsInGithub(): Unit = ???

  override def errorFindingProjectsLocally(ex: Throwable): Unit = ???

  override def unhandledException(ex: Throwable): Unit = {
    val s = ExceptionUtil.toStringWithStackTrace(ex)
    emit(s)
  }

  override def event(event: Event): Unit = {
    emit(s"event: $event")
  }

  override def signal(signal: Signal): Unit = {
    emit(s"signal: $signal")
  }

  override def processLaunched(processInput: ProcessInput): Unit = {
    emit("launching process:")
    DevonMarshallerWiring.Default.valueToPretty(processInput).foreach(emit)
  }

  override def statusUpdate(statusOfProjects: StatusOfProjects): Unit = {
    val reportOfUpdates = StatusReport.fromStatusOfProjects(statusOfProjects)
    val reportOfSummary = statusOfProjects.byStateName
    val updateLines = DevonMarshallerWiring.Default.valueToPretty(reportOfUpdates)
    val summaryLines = DevonMarshallerWiring.Default.valueToPretty(reportOfSummary)
    updateLines.foreach(emit)
    statefulLogger.emitLines(updateLines ++ summaryLines)
  }

  override def statusSummary(statusOfProjects: StatusOfProjects): Unit = {
    val summaryLines = DevonMarshallerWiring.Default.valueToPretty(statusOfProjects.byStateName)
    summaryLines.foreach(emit)
  }

  override def startAndEndTime(start: Instant, end: Instant): Unit = {
    val duration = Duration.between(start, end).toMillis
    val durationString = DurationFormat.MillisecondsFormat.format(duration)
    emit(durationString)
  }


  override def unsupportedEventFromState(eventName: String, stateName: String): Unit = {
    emit(s"Unable to apply event $eventName to state $stateName")
  }

  case class StatusReport(finished: Int, total: Int, remaining: Seq[String])

  object StatusReport {
    def fromStatusOfProjects(statusOfProjects: StatusOfProjects): StatusReport = {
      StatusReport(
        statusOfProjects.finished,
        statusOfProjects.total,
        statusOfProjects.remaining)
    }
  }

}
