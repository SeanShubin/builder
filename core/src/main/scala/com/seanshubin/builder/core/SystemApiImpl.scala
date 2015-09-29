package com.seanshubin.builder.core

import java.nio.file.{Files, Path}

class SystemApiImpl(systemExecutor: SystemExecutor,
                   homeDirectory:Path,
                    environment: Settings,
                    notifications: Notifications) extends SystemApi {
  override def listLocalDirectoryNames(): Seq[String] = {
    val command = environment.commandPrefix ++ environment.directoryListingCommand
    val directory = homeDirectory
    val result = exec(command, directory)
    result.throwIfError()
    result.outputLines
  }

  override def hasPendingEdits(projectName: String): Boolean = {
    val command = environment.commandPrefix ++ Seq("git", "status", "-s")
    val directory = homeDirectory.resolve(projectName)
    val result = exec(command, directory)
    result.throwIfError()
    result.outputLines.size > 0
  }

  override def deploy(projectName: String): Seq[ExecutionResult] = {
    maven("deploy", projectName)
  }

  override def verify(projectName: String): Seq[ExecutionResult] = {
    maven("verify", projectName)
  }

  override def compile(projectName: String): Seq[ExecutionResult] = {
    maven("compile", projectName)
  }

  override def gitAddCommitPush(projectName: String, commitMessage: String): Seq[ExecutionResult] = {
    val addResult = gitAdd(projectName)
    val commitResult = gitCommit(projectName, commitMessage)
    val pushResult = gitPush(projectName)
    Seq(addResult, commitResult, pushResult)
  }

  private def gitAdd(projectName: String): ExecutionResult = {
    val command = environment.commandPrefix ++ Seq("git", "add", "--all")
    val directory = homeDirectory.resolve(projectName)
    val result = exec(command, directory)
    result
  }

  private def gitCommit(projectName: String, commitMessage: String): ExecutionResult = {
    val command = environment.commandPrefix ++ Seq("git", "commit", "-m", commitMessage)
    val directory = homeDirectory.resolve(projectName)
    val result = exec(command, directory)
    result
  }

  private def gitPush(projectName: String): ExecutionResult = {
    val command = environment.commandPrefix ++ Seq("git", "push")
    val directory = homeDirectory.resolve(projectName)
    val result = exec(command, directory)
    result
  }

  private def exec(command: Seq[String], directory: Path): ExecutionResult = {
    val result = systemExecutor.executeSynchronous(command, directory)
    result
  }

  private def maven(mavenCommand: String, projectName: String): Seq[ExecutionResult] = {
    val fetchResult = fetch(projectName)
    val rebaseResult = rebase(projectName)
    val mavenSettings: Seq[String] = environment.mavenSettings match {
      case Some(mavenSettingsFile) => Seq("--settings", mavenSettingsFile)
      case None => Seq()
    }
    val command = environment.commandPrefix ++ Seq("mvn", "clean", mavenCommand) ++ mavenSettings
    val directory = homeDirectory.resolve(projectName)
    val mavenResult = exec(command, directory)
    Seq(fetchResult, rebaseResult, mavenResult)
  }

  private def fetch(projectName: String): ExecutionResult = {
    val command = environment.commandPrefix ++ Seq("git", "fetch")
    val directory = homeDirectory.resolve(projectName)
    val result = exec(command, directory)
    result
  }

  private def rebase(projectName: String): ExecutionResult = {
    val command = environment.commandPrefix ++ Seq("git", "rebase")
    val directory = homeDirectory.resolve(projectName)
    val result = exec(command, directory)
    result
  }

  override def gitFetchRebase(projectName: String): Seq[ExecutionResult] = {
    val fetchResult = fetch(projectName)
    val rebaseResult = rebase(projectName)
    Seq(fetchResult, rebaseResult)
  }

  override def exists(projectName: String): Boolean = Files.exists(homeDirectory.resolve(projectName))

  override def clone(projectName: String): Seq[ExecutionResult] = {
    val command = environment.commandPrefix ++ Seq("git", "clone", s"git@github.com:SeanShubin/$projectName.git")
    val result = exec(command, homeDirectory)
    Seq(result)
  }
}
