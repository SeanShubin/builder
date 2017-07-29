package com.seanshubin.builder.domain

import java.io.PrintWriter
import java.nio.file.{Path, StandardOpenOption}
import java.time.Duration

import com.seanshubin.builder.domain.ProcessLoggerImpl.ProcessOutputReport
import com.seanshubin.devon.domain.DevonMarshallerWiring
import com.seanshubin.up_to_date.logic.DurationFormat

import scala.reflect.runtime.universe

class ProcessLoggerImpl(files: FilesContract, directory: Path, baseName: String) extends ProcessLogger {
  private val outPath = directory.resolve(baseName + ".stream.out.txt")
  private val errPath = directory.resolve(baseName + ".stream.err.txt")
  private val inputPath = directory.resolve(baseName + ".process.input.txt")
  private val outputPath = directory.resolve(baseName + ".process.output.txt")
  files.createDirectories(directory)

  override def emitInput(input: ProcessInput): Unit = {
    emitDevon(inputPath, input)
  }

  override def emitOutput(output: ProcessOutput): Unit = {
    val report = ProcessOutputReport.fromProcessOutput(output)
    emitDevon(outputPath, report)
  }

  override def emitOutLine(line: String): Unit = {
    emit(outPath, line)
  }

  override def emitErrLine(line: String): Unit = {
    emit(errPath, line)
  }

  private def emitDevon[T: universe.TypeTag](path: Path, data: T): Unit = {
    val lines = DevonMarshallerWiring.Default.valueToPretty(data)
    withPrintWriter(path) { printWriter =>
      lines.foreach(printWriter.println)
    }
  }

  private def emit(path: Path, line: String): Unit = {
    withPrintWriter(path) { printWriter =>
      printWriter.println(line)
    }
  }

  private def withPrintWriter(path: Path)(block: PrintWriter => Unit): Unit = {
    import StandardOpenOption._
    val writer = files.newBufferedWriter(path, CREATE, APPEND)
    val printWriter = new PrintWriter(writer)
    block(printWriter)
    printWriter.close()
  }
}

object ProcessLoggerImpl {

  case class ProcessOutputReport(processInput: ProcessInput,
                                 exitCode: Int,
                                 outputSummary: String,
                                 errorSummary: String,
                                 duration: String)

  object ProcessOutputReport {
    def fromProcessOutput(processOutput: ProcessOutput): ProcessOutputReport = {
      val outputLineCount = processOutput.outputLines.size
      val errorLineCount = processOutput.errorLines.size
      val durationInMilliseconds = Duration.between(processOutput.started, processOutput.ended).toMillis
      val outputSummary = s"$outputLineCount output lines"
      val errorSummary = s"$errorLineCount error lines"
      val duration = DurationFormat.MillisecondsFormat.format(durationInMilliseconds)
      ProcessOutputReport(
        processOutput.processInput,
        processOutput.exitCode,
        outputSummary,
        errorSummary,
        duration)
    }
  }

}