package com.seanshubin.builder.domain

trait ProcessLoggerFactory {
  def createAction(action: String): ProcessLogger

  def createProjectCommand(project: String, command: String): ProcessLogger
}
