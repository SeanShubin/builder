package com.seanshubin.builder.core

trait Notifications {
  def highlightErrors(reports: Seq[Report])

  def effectiveConfiguration(configuration: Configuration)

  def configurationError(lines: Seq[String])

  def summarize(reports: Seq[Report])

  def projects(projects: Seq[Project])

  def execOutput(line: String)

  def totalMillisecondsElapsed(milliseconds: Long)
}
