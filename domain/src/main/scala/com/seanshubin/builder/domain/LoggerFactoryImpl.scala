package com.seanshubin.builder.domain

import java.nio.file.Path
import java.time.Instant

class LoggerFactoryImpl(files: FilesContract,
                        baseDirectory: Path,
                        startTime: Instant) extends LoggerFactory {
  val directory = baseDirectory.resolve(startTime.toString)

  override def createAction(action: String): Logger = {
    val path = directory.resolve("action")
    new LoggerImpl(files, path, action)
  }

  override def createProjectAction(project: String, action: String): Logger = {
    val path = directory.resolve("project").resolve(project)
    new LoggerImpl(files, path, action)
  }

  override def createCommandProjectRetry(project: String, command: String, retry: Int): Logger = {
    val retryName = f"$retry%02d"
    val path = directory.resolve("command").resolve(project).resolve(command)
    new LoggerImpl(files, path, retryName)
  }
}
