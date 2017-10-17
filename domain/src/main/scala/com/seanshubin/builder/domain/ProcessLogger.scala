package com.seanshubin.builder.domain

trait ProcessLogger {
  def emitInput(input: ProcessInput): Unit

  def emitOutput(output: ProcessOutput): Unit

  def emitOutLine(line: String): Unit

  def emitErrLine(line: String): Unit

  def unexpected(failReason: FailReason): Unit
}
