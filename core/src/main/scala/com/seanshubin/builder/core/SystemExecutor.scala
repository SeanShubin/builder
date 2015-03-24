package com.seanshubin.builder.core

import java.nio.file.Path

trait SystemExecutor {
  def executeSynchronous(command: Seq[String], directory: Path): ExecutionResult
}
