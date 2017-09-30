package com.seanshubin.builder.domain

import java.nio.file.Path

case class SystemSpecific(commandPrefix: Seq[String],
                          githubDirectory: Path,
                          directoryListingCommand: Seq[String],
                          mavenSettings: Option[String])
