package com.seanshubin.builder.core

import java.io.{PrintWriter, StringWriter}

import com.seanshubin.devon.core.devon.DevonMarshaller

class LineEmittingNotifications(devonMarshaller: DevonMarshaller, emit: String => Unit) extends Notifications {
  override def effectiveConfiguration(configuration: Configuration): Unit = {
    val pretty = devonMarshaller.valueToPretty(configuration)
    emit("Effective configuration:")
    pretty.foreach(emit)
  }

  override def configurationError(lines: Seq[String]): Unit = {
    lines.foreach(emit)
  }

  private def exceptionLines(ex: Throwable): Seq[String] = {
    val stringWriter = new StringWriter()
    val printWriter = new PrintWriter(stringWriter)
    ex.printStackTrace(printWriter)
    val s = stringWriter.toString
    val lines = s.split( """\r\n|\r|\n""").toSeq
    lines
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
    }
  }

  private def summarizeExecutionReport(report: ExecutionReport): Seq[String] = {
    report.executionResults.flatMap(summarizeExecutionResult)
  }

  private def summarizeExecutionResult(result: ExecutionResult): Seq[String] = {
    if (result.exitCode == 0) Seq(s"(success) ${result.directory} ${result.command}")
    else Seq(s"(failure) ${result.directory} ${result.command}")
  }

  private def summarizeIgnoredReport(report: IgnoredReport): Seq[String] = {
    Seq(s"(ignored) ${report.projectName}")
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

  private def indent(s: String) = s"  $s"
}
