package com.seanshubin.builder.domain

import scala.concurrent.{ExecutionContext, Future}


class LocalProjectFinder(systemSpecific: SystemSpecific,
                         processLauncher: ProcessLauncher,
                         loggerFactory: ProcessLoggerFactory)(
                          implicit executionContext: ExecutionContext) extends ProjectFinder {
  override def findProjects(): Future[Seq[String]] = {
    val logger = loggerFactory.createAction("find")
    val command = systemSpecific.commandPrefix ++ systemSpecific.directoryListingCommand
    val directory = systemSpecific.githubDirectory
    val environment = Map[String, String]()
    val input = ProcessInput(command, directory, environment)
    val futureProcessOutput = processLauncher.launch(input, logger)
    for {
      processOutput <- futureProcessOutput
    } yield {
      processOutput.outputLines
    }
  }
}
