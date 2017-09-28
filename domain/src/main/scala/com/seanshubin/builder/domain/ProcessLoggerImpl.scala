package com.seanshubin.builder.domain

import java.io.PrintWriter
import java.nio.file.{Path, StandardOpenOption}

import com.seanshubin.devon.domain.DevonMarshallerWiring

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
    emitDevon(outputPath, output)
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
