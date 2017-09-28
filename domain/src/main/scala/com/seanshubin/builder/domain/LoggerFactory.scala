package com.seanshubin.builder.domain

trait LoggerFactory {
  def createAction(action: String): Logger

  def createProjectAction(project: String, action: String): Logger

  def createCommandProjectRetry(project: String, command: String, retry: Int): Logger
}
