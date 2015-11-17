package com.seanshubin.builder.core

trait SystemApi {
  def gitAddCommitPush(projectName: String, commitMessage: String): Seq[ExecutionResult]

  def deploy(projectName: String): Seq[ExecutionResult]

  def verify(projectName: String): Seq[ExecutionResult]

  def compile(projectName: String): Seq[ExecutionResult]

  def hasPendingEdits(projectName: String): Boolean

  def listLocalDirectoryNames(): Seq[String]

  def gitFetchRebase(projectName: String): Seq[ExecutionResult]

  def exists(projectName: String): Boolean

  def clone(projectName: String): Seq[ExecutionResult]
}