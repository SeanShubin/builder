package com.seanshubin.builder.domain

import java.nio.file.Path
import java.time.Instant

class ProcessLoggerFactoryImpl(files: FilesContract,
                               baseDirectory: Path,
                               startTime: Instant,
                               rootLogger: String => Unit) extends ProcessLoggerFactory {
  val directory = baseDirectory.resolve(startTime.toString)

  override def createAction(action: String): ProcessLogger = {
    val path = directory.resolve("action")
    new ProcessLoggerImpl(files, path, action, rootLogger)
  }

  override def createProjectAction(project: String, action: String): ProcessLogger = {
    val path = directory.resolve("project").resolve(project)
    new ProcessLoggerImpl(files, path, action, rootLogger)
  }

  override def createCommandProjectRetry(project: String, command: String, retry: Int): ProcessLogger = {
    val retryName = f"$retry%02d"
    val path = directory.resolve("command").resolve(project).resolve(command)
    new ProcessLoggerImpl(files, path, retryName, rootLogger)
  }
}
