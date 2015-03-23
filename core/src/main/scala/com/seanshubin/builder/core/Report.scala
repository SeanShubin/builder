package com.seanshubin.builder.core

sealed trait Report {
  def hasError: Boolean
}

case class ExecutionReport(projectName: String, command: CommandEnum, executionResults: Seq[ExecutionResult]) extends Report {
  override def hasError = executionResults.exists(_.hasError)
}

case class IgnoredReport(projectName: String) extends Report {
  override def hasError: Boolean = false
}
