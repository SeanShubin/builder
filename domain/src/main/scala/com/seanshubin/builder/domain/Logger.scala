package com.seanshubin.builder.domain

trait Logger {
  def emitOut(line: String): Unit

  def emitErr(line: String): Unit
}
