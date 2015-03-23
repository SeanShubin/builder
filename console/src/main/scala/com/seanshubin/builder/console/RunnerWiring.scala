package com.seanshubin.builder.console

import com.seanshubin.builder.core._

trait RunnerWiring {
  def configuration: Configuration

  lazy val emitLine: String => Unit = println
  lazy val runner: Runner = new RunnerImpl(configuration.greetingTarget, emitLine)
}
