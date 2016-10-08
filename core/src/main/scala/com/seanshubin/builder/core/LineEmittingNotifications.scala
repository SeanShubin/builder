package com.seanshubin.builder.core

import com.seanshubin.devon.parserules.DevonMarshaller

class LineEmittingNotifications(devonMarshaller: DevonMarshaller, emit: String => Unit) extends Notifications {
  override def effectiveConfiguration(configuration: Configuration): Unit = {
    val pretty = devonMarshaller.valueToPretty(configuration)
    emit("Effective configuration:")
    pretty.foreach(emit)
  }

  override def configurationError(lines: Seq[String]): Unit = {
    lines.foreach(emit)
  }

  override def summarize(reports: Seq[Report]): Unit = {
    reports.flatMap(summarizeReport).foreach(emit)
  }

  override def highlightErrors(reports: Seq[Report]): Unit = {
    val reportsWithErrors = reports.filter(_.hasError)
    if (reportsWithErrors.size == 0) {
      emit("No errors!")
    } else {
      emit(s"${reportsWithErrors.size} errors!")
      reportsWithErrors.flatMap(summarizeReport).foreach(emit)
    }
  }

  private def summarizeReport(report: Report): Seq[String] = {
    report match {
      case x: ExecutionReport => summarizeExecutionReport(x)
      case x: IgnoredReport => summarizeIgnoredReport(x)
      case x: ExceptionReport => summarizeExceptionReport(x)
    }
  }

  private def summarizeExecutionReport(report: ExecutionReport): Seq[String] = {
    report.executionResults.flatMap(summarizeExecutionResult)
  }

  private def summarizeExecutionResult(result: ExecutionResult): Seq[String] = {
    val resultCommand = result.command.mkString(" ")
    if (result.exitCode == 0) Seq(s"(success) ${result.directory} $resultCommand")
    else Seq(s"(failure) ${result.directory} $resultCommand")
  }

  private def summarizeIgnoredReport(report: IgnoredReport): Seq[String] = {
    Seq(s"(ignored) ${report.projectName}")
  }

  private def summarizeExceptionReport(report: ExceptionReport): Seq[String] = {
    Seq(s"(exception) ${report.projectName}")
  }

  override def projects(projects: Seq[Project]): Unit = {
    for {
      project <- projects
      name = project.name
    } {
      if (!project.isIgnored) {
        if (project.hasPendingEdits) emit(s"$name has pending edits")
        if (project.isConfigured) {
          if (!project.isInGithub) emit(s"$name is missing from github")
          if (!project.isLocal) emit(s"$name is missing locally")
        } else {
          if (project.isInGithub) emit(s"$name is in github but not configured")
          if (project.isLocal) emit(s"$name exists locally but is not configured")
        }
      }
    }
  }

  override def execOutput(line: String): Unit = emit(line)

  override def totalMillisecondsElapsed(milliseconds: Long): Unit = {
    val timeTakenString = DurationFormat.MillisecondsFormat.format(milliseconds)
    emit(s"Time taken: $timeTakenString")
  }
}
