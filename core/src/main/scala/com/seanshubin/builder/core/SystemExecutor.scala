package com.seanshubin.builder.core

import java.nio.file.Path

trait SystemExecutor {
  def executeSynchronous(command: String, directory: Path): ExecutionResult
}
