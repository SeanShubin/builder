package com.seanshubin.builder.core

import java.nio.file.Path

case class ExecutionResult(command: Seq[String], exitCode: Int, directory: Path, outputLines: Seq[String], errorLines: Seq[String]) {
  def throwIfError(): Unit = {
    if (hasError) {
      val commandMessage = command.mkString(" ")
      throw new RuntimeException(s"Error executing command $commandMessage, exit code was $exitCode")
    }
  }

  def hasError: Boolean = exitCode != 0
}
