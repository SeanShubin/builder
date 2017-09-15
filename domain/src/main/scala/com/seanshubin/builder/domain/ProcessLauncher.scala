package com.seanshubin.builder.domain

import scala.concurrent.Future

trait ProcessLauncher {
  def launch(input: ProcessInput): Future[ProcessOutput]
}
