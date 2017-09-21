package com.seanshubin.builder.core

import java.nio.file.Path

class ProcessLoggerFactoryImpl(emitProcessLine: String => Unit) extends ProcessLoggerFactory {
  override def newProcessLogger(command: Seq[String], directory: Path, env: Seq[(String, String)]): OutputCapturingProcessLogger = {
    val commandString = command.mkString(" ")

    def annotatedEmit(line: String) = emitProcessLine(s"$directory> $commandString: $line")

    new ArrayBufferProcessLogger(annotatedEmit)
  }
}
