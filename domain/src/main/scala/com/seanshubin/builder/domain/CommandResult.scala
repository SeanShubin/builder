package com.seanshubin.builder.domain

case class CommandResult(command: String, project: String, processOutput: ProcessOutput) {
  def isSuccess: Boolean = {
    processOutput.exitCode match {
      case 0 => true
      case 1 => false
      case _ => ???
    }
  }
}
