package com.seanshubin.builder.core

import java.nio.file.Path

trait ProcessLoggerFactory {
  def newProcessLogger(command: String, directory: Path, env: Seq[(String, String)]): OutputCapturingProcessLogger
}
