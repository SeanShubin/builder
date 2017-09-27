package com.seanshubin.builder.domain

object ProjectLogic {
  def createStateMap(local: Seq[String], github: Seq[String]): Map[String, ProjectState] = {
    val inLocalAndGithub = local.intersect(github).map((_, ProjectState.InLocalAndGithub)).toMap
    val inGithubNotLocal = (github.toSet -- local.toSet).map((_, ProjectState.InGithubNotLocal)).toMap
    val inLocalNotGithub = (local.toSet -- github.toSet).map((_, ProjectState.InLocalNotGithub)).toMap
    inLocalAndGithub ++ inGithubNotLocal ++ inLocalNotGithub
  }
}
