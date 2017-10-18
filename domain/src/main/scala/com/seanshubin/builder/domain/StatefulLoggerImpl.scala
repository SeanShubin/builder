package com.seanshubin.builder.domain

import java.nio.charset.Charset
import java.nio.file.Path

import scala.collection.JavaConverters._

class StatefulLoggerImpl(pathToStoreState: Path, files: FilesContract, charset: Charset) extends StatefulLogger {
  override def emitLines(lines: Seq[String]): Unit = {
    val javaLines = lines.asJava
    files.write(pathToStoreState, javaLines, charset)
  }
}
