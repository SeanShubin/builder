package com.seanshubin.builder.domain

import java.nio.file.Paths

class SystemSpecificImpl extends SystemSpecific {
  override def composeDirectoryListingCommand(): ProcessInput = {
    val command = Seq("ls", "-1")
    val directory = Paths.get("/Users/sshubin/github/sean")
    val environment = Map[String, String]()
    ProcessInput(command, directory, environment)
  }
}
