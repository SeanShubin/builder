package com.seanshubin.builder.domain

import java.nio.file.Path

import scala.concurrent.ExecutionContext

class ClonerImpl(processLauncher: ProcessLauncher,
                 baseDirectory: Path,
                 processCompleted: ProcessOutput => Unit,
                 loggerFactory: LoggerFactory)
                (implicit val executionContext: ExecutionContext) extends Cloner {
  override def clone(name: String): Unit = {
    val logger = loggerFactory.createProjectAction(name, "clone")
    val command = Seq("git", "clone", s"https://github.com/SeanShubin/$name.git")
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
