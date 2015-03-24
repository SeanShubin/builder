package com.seanshubin.builder.core

import java.nio.file.Path

trait ProcessLoggerFactory {
  def newProcessLogger(command: Seq[String], directory: Path, env: Seq[(String, String)]): OutputCapturingProcessLogger
}
