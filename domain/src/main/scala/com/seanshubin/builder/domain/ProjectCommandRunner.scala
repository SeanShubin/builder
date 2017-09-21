package com.seanshubin.builder.domain

import scala.concurrent.Future

trait ProjectCommandRunner {
  def exec(name: String, command: String*): Future[ProcessOutput]
}
