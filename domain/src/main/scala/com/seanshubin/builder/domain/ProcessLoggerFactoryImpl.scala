package com.seanshubin.builder.domain

import java.nio.file.Path

class ProcessLoggerFactoryImpl(files: FilesContract,
                               directory: Path,
                               rootLogger: String => Unit) extends ProcessLoggerFactory {
  override def createAction(action: String): ProcessLogger = {
    val path = directory.resolve("action").resolve(action)
    new ProcessLoggerImpl(files, path, rootLogger)
  }

  override def createProjectCommand(project: String, command: String): ProcessLogger = {
    val path = directory.resolve("command").resolve(project).resolve(command)
    new ProcessLoggerImpl(files, path, rootLogger)
  }
}
