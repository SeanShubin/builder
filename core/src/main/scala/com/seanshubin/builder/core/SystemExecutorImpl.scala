package com.seanshubin.builder.core

import java.nio.file.Path

import scala.collection.JavaConversions
import scala.sys.process._

class SystemExecutorImpl(processLoggerFactory: ProcessLoggerFactory) extends SystemExecutor {
  override def executeSynchronous(command: String, directory: Path): ExecutionResult = {
    val env: Seq[(String, String)] = JavaConversions.mapAsScalaMap(System.getenv()).toSeq
    val processBuilder: ProcessBuilder = Process(command, directory.toFile, env: _*)
    val processLogger = processLoggerFactory.newProcessLogger(command, directory, env)
    val process: Process = processBuilder.run(processLogger)
    val exitCode = process.exitValue()
    val outputLines: Seq[String] = processLogger.outputLines
    val errorLines: Seq[String] = processLogger.errorLines
    ExecutionResult(command, exitCode, directory, outputLines, errorLines)
  }
}
