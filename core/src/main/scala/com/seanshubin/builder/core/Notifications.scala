package com.seanshubin.builder.core

trait Notifications {
  def effectiveConfiguration(configuration: Configuration)

  def configurationError(lines: Seq[String])

  def topLevelException(exception: Throwable)
}
