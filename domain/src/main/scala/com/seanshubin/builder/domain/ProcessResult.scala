package com.seanshubin.builder.domain

import java.nio.file.Path

import com.seanshubin.devon.core.devon.DevonMarshallerWiring

case class ProcessResult(command: Seq[String], directory: Path, exitCode: Int, inputLines: Seq[String], outputLines: Seq[String], errorLines: Seq[String]) {
  def toMultipleLineString: Seq[String] = DevonMarshallerWiring.Default.valueToPretty(this)

  def isSuccess: Boolean = exitCode == 0
}

