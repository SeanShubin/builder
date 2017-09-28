package com.seanshubin.builder.domain

trait ProcessLoggerFactory {
  def createAction(action: String): ProcessLogger

  def createProjectAction(project: String, action: String): ProcessLogger

  def createCommandProjectRetry(project: String, command: String, retry: Int): ProcessLogger
}
