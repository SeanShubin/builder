package com.seanshubin.builder.domain

import scala.concurrent.Future

trait ProjectCommandRunner {
  def exec(commandName: String, projectName: String, previousAttemptCount: Int, command: String*): Future[CommandResult]
}
