package com.seanshubin.builder.core

trait Api {
  def projectNamesInGithub(): Set[String]

  def projectNamesLocal(): Set[String]

  def pendingLocalEdits(projectName: String): Boolean

  def deploy(name: String): Seq[ExecutionResult]

  def verify(name: String): Seq[ExecutionResult]

  def install(name: String): Seq[ExecutionResult]

  def compile(name: String): Seq[ExecutionResult]

  def addCommitPush(projectName: String, gitMessage: String): Seq[ExecutionResult]

  def fetchRebase(projectName: String): Seq[ExecutionResult]

  def cloneIfMissing(projectName: String): Seq[ExecutionResult]
}
