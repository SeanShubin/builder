package com.seanshubin.builder.domain

import java.time.{Duration, Instant}

import akka.typed.Signal
import com.seanshubin.devon.domain.DevonMarshallerWiring
import com.seanshubin.up_to_date.logic.DurationFormat

class LineEmittingNotifications(emit: String => Unit) extends Notifications {
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
    val report = StatusReport.fromStatusOfProjects(statusOfProjects)
    DevonMarshallerWiring.Default.valueToPretty(report).foreach(emit)
  }

  override def startAndEndTime(start: Instant, end: Instant): Unit = {
    val duration = Duration.between(start, end).toMillis
    val durationString = DurationFormat.MillisecondsFormat.format(duration)
    emit(durationString)
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
