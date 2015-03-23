package com.seanshubin.builder.core

import java.nio.file.Path

class ProcessLoggerFactoryImpl(emitProcessLine: String => Unit) extends ProcessLoggerFactory {
  override def newProcessLogger(command: String, directory: Path, env: Seq[(String, String)]): OutputCapturingProcessLogger = {
    def annotatedEmit(line: String) = emitProcessLine(s"$directory> $command: $line")
    new ArrayBufferProcessLogger(annotatedEmit)
  }
}
