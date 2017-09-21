package com.seanshubin.builder.domain

object ProjectLogic {
  def createStateMap(local: Seq[String], github: Seq[String]): Map[String, ProjectState] = {
    val needBuild = local
    val needClone = github.toSet -- local.toSet
    val emptyStateMap = Map[String, ProjectState]()
    val withBuild = needBuild.foldLeft(emptyStateMap)(addBuild)
    val withBuildAndClone = needClone.foldLeft(withBuild)(addClone)
    withBuildAndClone
  }

  def addBuild(stateMap:Map[String, ProjectState], build:String):Map[String, ProjectState] = {
    stateMap + (build -> ProjectState.Building)
  }
  def addClone(stateMap:Map[String, ProjectState], build:String):Map[String, ProjectState] = {
    stateMap + (build -> ProjectState.Cloning)
  }
}
