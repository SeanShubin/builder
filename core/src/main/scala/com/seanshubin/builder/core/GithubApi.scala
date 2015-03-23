package com.seanshubin.builder.core

trait GithubApi {
  def getProjectsForUser(userName: String): Seq[String]
}
