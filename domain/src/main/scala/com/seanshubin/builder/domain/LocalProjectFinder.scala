package com.seanshubin.builder.domain

import scala.concurrent.{ExecutionContext, Future}


class LocalProjectFinder(systemSpecific: SystemSpecific,
                         processLauncher: ProcessLauncher,
                         loggerFactory: ProcessLoggerFactory)(
                          implicit executionContext: ExecutionContext) extends ProjectFinder {
  override def findProjects(): Future[Seq[String]] = {
    val logger = loggerFactory.createAction("find")
    val command = systemSpecific.composeDirectoryListingCommand()
    val futureProcessOutput = processLauncher.launch(command, logger)
    for {
      processOutput <- futureProcessOutput
    } yield {
      processOutput.outputLines
    }
  }
}
