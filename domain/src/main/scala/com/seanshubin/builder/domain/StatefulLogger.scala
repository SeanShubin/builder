package com.seanshubin.builder.domain

trait StatefulLogger {
  def emitLines(lines: Seq[String]): Unit
}
