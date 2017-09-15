package com.seanshubin.builder.domain

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}


class LocalProjectFinder(systemSpecific: SystemSpecific,
                         processLauncher: ProcessLauncher,
                         fireFoundEvent: Seq[String] => Unit,
                         fireErrorEvent: Throwable => Unit)(
                          implicit executionContext: ExecutionContext) extends ProjectFinder {
  override def findProjects(): Unit = {
    val command = systemSpecific.composeDirectoryListingCommand()
    val futureResult = processLauncher.launch(command)
    futureResult.onComplete {
      case Success(value) =>
        fireFoundEvent(value.outputLines)
      case Failure(exception) =>
        fireErrorEvent(exception)
    }
  }
}
