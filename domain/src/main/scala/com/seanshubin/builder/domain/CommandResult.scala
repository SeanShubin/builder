package com.seanshubin.builder.domain

case class CommandResult(command: String, project: String, processOutput: ProcessOutput, previousAttemptCount: Int) {
  def isSuccess: Boolean = ???

  def shouldRetry: Boolean = ???
}
