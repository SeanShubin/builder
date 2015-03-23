package com.seanshubin.builder.core

import java.nio.file.Path

case class ExecutionResult(command: String, exitCode: Int, directory: Path, outputLines: Seq[String], errorLines: Seq[String]) {
  def throwIfError(): Unit = {
    if (hasError) {
      throw new RuntimeException(s"Error executing command $command, exit code was $exitCode")
    }
  }

  def hasError: Boolean = exitCode != 0
}
