package com.seanshubin.builder.prototype

case class StatusOfProjects(map: Map[String, Option[Int]]) {
  def isDone: Boolean = {
    map.forall((StatusOfProjects.isEntryDone _).tupled)
  }

  def completeProject(name: String, code: Int): StatusOfProjects = {
    StatusOfProjects(map.updated(name, Some(code)))
  }
}

object StatusOfProjects {
  def fromNames(names: Seq[String]): StatusOfProjects = {
    val emptyStateMap = Map[String, Option[Int]]()
    val withNames = names.foldLeft(emptyStateMap)(addName)
    StatusOfProjects(withNames)
  }

  private def addName(stateMap: Map[String, Option[Int]], build: String): Map[String, Option[Int]] = {
    stateMap + (build -> None)
  }

  private def isEntryDone(name: String, maybeCode: Option[Int]): Boolean = {
    maybeCode.isDefined
  }
}