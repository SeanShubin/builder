package com.seanshubin.builder.core

case class Settings(commandPrefix: Seq[String],
                    directoryListingCommand: Seq[String],
                    shouldUpgradeDependencies: Boolean,
                    mavenSettings: Option[String])
