package com.seanshubin.builder.domain

import java.nio.file.Path
import java.time.Instant

class ProcessLoggerFactoryImpl(files: FilesContract,
                               baseDirectory: Path,
                               startTime: Instant,
                               rootLogger: String => Unit) extends ProcessLoggerFactory {
  val directory = baseDirectory.resolve(startTime.toString)

  override def createAction(action: String): ProcessLogger = {
    val path = directory.resolve("action").resolve(action)
    new ProcessLoggerImpl(files, path, rootLogger)
  }

  override def createProjectCommand(project: String, command: String): ProcessLogger = {
    val path = directory.resolve("command").resolve(project).resolve(command)
    new ProcessLoggerImpl(files, path, rootLogger)
  }
}
