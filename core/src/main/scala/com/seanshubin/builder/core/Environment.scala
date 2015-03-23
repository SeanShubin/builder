package com.seanshubin.builder.core

import java.nio.file.Path

case class Environment(baseDirectory: Path, commandPrefix: String, directoryListingCommand: String)
