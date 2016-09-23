package com.seanshubin.builder.domain

import java.nio.charset.StandardCharsets
import java.nio.file.Path

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{ExecutionContext, Future}

object ProcessRunner {
  implicit val implicitExecutionContext = ExecutionContext.global

  def run(command: Seq[String], directory: Path): Future[ProcessResult] = {
    val outputBuffer = new ArrayBuffer[String]
    val errorBuffer = new ArrayBuffer[String]
    val process = new LinesBasedProcess(
      command,
      directory,
      Map(),
      line => outputBuffer.append(line),
      line => errorBuffer.append(line),
      redirectErrorToStandard = false,
      StandardCharsets.UTF_8,
      ExecutionContext.global)
    val futureExitCode = process.waitForExitCode()
    for {
      exitCode <- futureExitCode
    } yield {
      ProcessResult(command, directory, exitCode, Seq(), outputBuffer, errorBuffer)
    }
  }
}
