package com.seanshubin.builder.domain

import scala.concurrent.{ExecutionContext, Future}


class LocalProjectFinder(systemSpecific: SystemSpecific,
                         processLauncher: ProcessLauncher)(
                          implicit executionContext: ExecutionContext) extends ProjectFinder {
  override def findProjects(): Future[Seq[String]] = {
    val command = systemSpecific.composeDirectoryListingCommand()
    val futureProcessOutput = processLauncher.launch(command)
    for {
      processOutput <- futureProcessOutput
    } yield {
      processOutput.outputLines
    }
  }
}
