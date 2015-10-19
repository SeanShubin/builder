package com.seanshubin.builder.core

import java.nio.file.Path

case class Settings(commandPrefix: Seq[String],
                    githubDirectory: Path,
                    directoryListingCommand: Seq[String],
                    shouldUpgradeDependencies: Boolean,
                    mavenSettings: Option[String])
