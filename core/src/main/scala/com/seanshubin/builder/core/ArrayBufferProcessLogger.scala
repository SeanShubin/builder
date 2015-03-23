package com.seanshubin.builder.core

import scala.collection.mutable.ArrayBuffer

class ArrayBufferProcessLogger(emitLine: String => Unit) extends OutputCapturingProcessLogger {
  val outputLines = new ArrayBuffer[String]()
  val errorLines = new ArrayBuffer[String]()

  override def out(s: => String): Unit = {
    outputLines.append(s)
    emitLine(s)
  }

  override def err(s: => String): Unit = {
    errorLines.append(s)
    emitLine(s)
  }

  override def buffer[T](f: => T): T = f
}
