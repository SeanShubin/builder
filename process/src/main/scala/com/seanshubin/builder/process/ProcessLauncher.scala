package com.seanshubin.builder.process

import scala.concurrent.Future

trait ProcessLauncher {
  def launch(input: ProcessInput): Future[ProcessOutput]
}
