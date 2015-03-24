package com.seanshubin.builder.core

import java.nio.file.Path

case class Environment(baseDirectory: Path, commandPrefix: Seq[String], directoryListingCommand: Seq[String])
