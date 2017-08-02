package com.seanshubin.builder.process

import java.nio.file.Path

case class ProcessInput(command: Seq[String],
                        directory: Path,
                        environment: Map[String, String])
