package com.seanshubin.builder.domain

import java.nio.file.Path

import scala.concurrent.Future

class ProjectCommandRunnerImpl(baseDirectory: Path,
                               processLauncher: ProcessLauncher) extends ProjectCommandRunner {
  override def exec(name: String, command: String*): Future[ProcessOutput] = {
    val directory = baseDirectory.resolve(name)
    val environment = Map[String, String]()
    val input = ProcessInput(command, directory, environment)
    val futureProcessOutput = processLauncher.launch(input)
    futureProcessOutput
  }
}
