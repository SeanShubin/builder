package com.seanshubin.builder.core

import scala.sys.process.ProcessLogger

trait OutputCapturingProcessLogger extends ProcessLogger {
  def outputLines: Seq[String]

  def errorLines: Seq[String]
}
