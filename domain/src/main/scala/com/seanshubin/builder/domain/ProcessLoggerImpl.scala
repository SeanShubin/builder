package com.seanshubin.builder.domain

import java.io.PrintWriter
import java.nio.file.{Path, StandardOpenOption}
import java.time.Duration

import com.seanshubin.builder.domain.FailReason.{ExceptionThrown, ExitCode}
import com.seanshubin.builder.domain.ProcessLoggerImpl.ProcessOutputReport
import com.seanshubin.devon.domain.DevonMarshallerWiring

import scala.reflect.runtime.universe

class ProcessLoggerImpl(files: FilesContract,
                        directory: Path,
                        rootLogger: String => Unit) extends ProcessLogger {
  private val outPath = createPath("stream.out.txt")
  private val errPath = createPath("stream.err.txt")
  private val inputPath = createPath("process.input.txt")
  private val outputPath = createPath("process.output.txt")
  private val unexpectedPath = createPath("unexpected.txt")
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

  override def unexpected(failReason: FailReason): Unit = {
    failReason match {
      case ExitCode(exitCode) => emit(unexpectedPath, s"exit code $exitCode")
      case ExceptionThrown(exception) =>
        val exceptionString = ExceptionUtil.toStringWithStackTrace(exception)
        emit(unexpectedPath, exceptionString)
    }
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
    val directoryName = directory.getParent.getFileName

    rootLogger(s"$directoryName $line")
  }

  private def withPrintWriter(path: Path)(block: PrintWriter => Unit): Unit = {
    import StandardOpenOption._
    val writer = files.newBufferedWriter(path, CREATE, APPEND)
    val printWriter = new PrintWriter(writer)
    block(printWriter)
    printWriter.close()
  }

  private def createPath(name: String): Path = {
    directory.resolve(name)
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