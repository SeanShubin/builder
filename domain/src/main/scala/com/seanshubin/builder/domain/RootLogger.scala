package com.seanshubin.builder.domain

import java.io.PrintWriter
import java.nio.file.{Path, StandardOpenOption}

class RootLogger(directory: Path,
                 emitToView: String => Unit,
                 files: FilesContract,
                 system: SystemContract) extends (String => Unit) {
  files.createDirectories(directory)
  val path = directory.resolve("root.txt")

  override def apply(line: String) = {
    emitToView(line)
    emitToStorage(path, line)
  }

  private def emitToStorage(path: Path, line: String): Unit = {
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
