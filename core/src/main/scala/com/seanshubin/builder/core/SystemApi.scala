package com.seanshubin.builder.core

trait SystemApi {
  def deploy(projectName: String): Seq[ExecutionResult]

  def verify(projectName: String): Seq[ExecutionResult]

  def compile(projectName: String): Seq[ExecutionResult]

  def hasPendingEdits(projectName: String): Boolean

  def listLocalDirectoryNames(): Seq[String]
}
