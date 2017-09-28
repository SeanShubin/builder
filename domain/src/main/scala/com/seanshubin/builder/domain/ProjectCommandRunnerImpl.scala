package com.seanshubin.builder.domain

import java.nio.file.Path

import scala.concurrent.{ExecutionContext, Future}

class ProjectCommandRunnerImpl(baseDirectory: Path,
                               processLauncher: ProcessLauncher,
                               loggerFactory: LoggerFactory)
                              (implicit executionContext: ExecutionContext) extends ProjectCommandRunner {
  override def exec(commandName: String, projectName: String, previousAttemptCount: Int, command: String*): Future[CommandResult] = {
    val logger = loggerFactory.createCommandProjectRetry(commandName, projectName, previousAttemptCount)
    val directory = baseDirectory.resolve(projectName)
    val environment = Map[String, String]()
    val input = ProcessInput(command, directory, environment)
    val futureProcessOutput = processLauncher.launch(input, logger)
    for {
      processOutput <- futureProcessOutput
    } yield {
      CommandResult(commandName, projectName, processOutput, previousAttemptCount)
    }
  }
}
