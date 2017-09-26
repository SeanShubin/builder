package com.seanshubin.builder.domain

case class StatusOfProjects(map: Map[String, ProjectState]) {
  def isDone: Boolean = {
    map.forall((StatusOfProjects.isEntryDone _).tupled)
  }
}

object StatusOfProjects {
  def create(local: Seq[String], github: Seq[String]): StatusOfProjects = {
    val needBuild = local
    val needClone = github.toSet -- local.toSet
    val emptyStateMap = Map[String, ProjectState]()
    val withBuild = needBuild.foldLeft(emptyStateMap)(addBuild)
    val withBuildAndClone = needClone.foldLeft(withBuild)(addClone)
    StatusOfProjects(withBuildAndClone)
  }

  private def addBuild(stateMap: Map[String, ProjectState], build: String): Map[String, ProjectState] = {
    stateMap + (build -> ProjectState.Building)
  }

  private def addClone(stateMap: Map[String, ProjectState], build: String): Map[String, ProjectState] = {
    stateMap + (build -> ProjectState.Cloning)
  }

  private def isEntryDone(name:String, status:ProjectState):Boolean = {
    status == ProjectState.Finished
  }
}