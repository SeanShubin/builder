package com.seanshubin.builder.domain

import java.nio.file.{Path, StandardOpenOption}

class LoggerImpl(files: FilesContract, directory: Path, baseName: String) extends Logger {
  private val outPath = directory.resolve(baseName + ".out.txt")
  private val errPath = directory.resolve(baseName + ".err.txt")
  files.createDirectories(directory)
  files.createFile(outPath)
  files.createFile(errPath)

  override def emitOut(line: String): Unit = {
    emit(outPath, line)
  }

  override def emitErr(line: String): Unit = {
    emit(errPath, line)
  }

  private def emit(path: Path, line: String): Unit = {
    val writer = files.newBufferedWriter(path, StandardOpenOption.APPEND)
    writer.write(line)
    writer.newLine()
    writer.flush()
    writer.close()
  }
}
