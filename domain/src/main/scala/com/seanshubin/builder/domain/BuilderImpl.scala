package com.seanshubin.builder.domain

import java.nio.file.Path

import scala.concurrent.ExecutionContext

class BuilderImpl(processLauncher: ProcessLauncher,
                  baseDirectory: Path,
                  processCompleted: ProcessOutput => Unit,
                  loggerFactory: LoggerFactory)
                 (implicit val executionContext: ExecutionContext) extends Builder {
  override def build(name: String): Unit = {
    val logger = loggerFactory.createProjectAction(name, "build")
    val command = Seq("mvn", "clean", "verify")
    val directory = baseDirectory.resolve(name)
    val environment = Map[String, String]()
    val input = ProcessInput(command, directory, environment)
    val futureProcessOutput = processLauncher.launch(input, logger)
    for {
      processOutput <- futureProcessOutput
    } {
      processCompleted(processOutput)
    }
  }
}
