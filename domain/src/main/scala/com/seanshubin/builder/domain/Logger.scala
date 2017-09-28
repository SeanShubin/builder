package com.seanshubin.builder.domain

trait Logger {
  def emitInput(input:ProcessInput):Unit
  def emitOutput(output:ProcessOutput):Unit
  def emitOutLine(line: String): Unit
  def emitErrLine(line: String): Unit
}
