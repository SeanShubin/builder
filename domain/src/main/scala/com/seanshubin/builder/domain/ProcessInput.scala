package com.seanshubin.builder.domain

import java.nio.file.Path

case class ProcessInput(command: Seq[String],
                        directory: Path,
                        environment: Map[String, String]) {
  def addCommandPrefix(commandPrefix: Seq[String]): ProcessInput = {
    copy(command = commandPrefix ++ command)
  }
}
