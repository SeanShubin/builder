package com.seanshubin.builder.core

class ApiImpl(systemApi: SystemApi, githubApi: GithubApi, userName: String) extends Api {
  override def projectNamesInGithub(): Set[String] = {
    githubApi.getProjectsForUser(userName).toSet
  }

  override def projectNamesLocal(): Set[String] = {
    systemApi.listLocalDirectoryNames().toSet
  }

  override def verify(name: String): Seq[ExecutionResult] = systemApi.verify(name)

  override def deploy(name: String): Seq[ExecutionResult] = systemApi.deploy(name)

  override def pendingLocalEdits(projectName: String): Boolean = systemApi.hasPendingEdits(projectName)

  override def compile(name: String): Seq[ExecutionResult] = systemApi.compile(name)

  override def addCommitPush(projectName: String, gitMessage: String): Seq[ExecutionResult] = {
    systemApi.gitAddCommitPush(projectName, gitMessage)
  }
}
