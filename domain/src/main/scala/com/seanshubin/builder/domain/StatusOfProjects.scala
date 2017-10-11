package com.seanshubin.builder.domain

case class StatusOfProjects(map: Map[String, ProjectState]) {
  def isDone: Boolean = {
    map.values.forall(_.isFinished)
  }

  def update(name: String, newState: ProjectState): StatusOfProjects = {
    StatusOfProjects(map.updated(name, newState))
  }

  def finished: Int = map.values.count(_.isFinished)

  def total: Int = map.size

  def remaining: Seq[String] = {
    (for {
      (name, state) <- map
      if !state.isFinished
    } yield {
      name
    }).toSeq
  }

  def byStateName: Map[String, Seq[String]] = {
    val initialMap = Map[String, Seq[String]]().withDefaultValue(Seq())
    map.foldLeft(initialMap)(StatusOfProjects.addToByStateNameMap)
  }
}

object StatusOfProjects {
  def create(local: Seq[String], github: Seq[String]): StatusOfProjects = {
    val inLocalAndGithub = local.intersect(github).map((_, ProjectState.InLocalAndGithub)).toMap
    val inGithubNotLocal = (github.toSet -- local.toSet).map((_, ProjectState.InGithubNotLocal)).toMap
    val inLocalNotGithub = (local.toSet -- github.toSet).map((_, ProjectState.InLocalNotGithub)).toMap
    val map = inLocalAndGithub ++ inGithubNotLocal ++ inLocalNotGithub
    StatusOfProjects(map)
    val onlyLookAt = Set("hello", "hello-web")
    val filteredMap = for {
      (key, value) <- map
      if onlyLookAt.contains(key)
    } yield {
      (key, value)
    }
    StatusOfProjects(filteredMap)
  }

  def addToByStateNameMap(accumulator: Map[String, Seq[String]], entry: (String, ProjectState)): Map[String, Seq[String]] = {
    val (project, state) = entry
    val stateName = state.name
    accumulator.updated(stateName, accumulator(stateName) :+ project)
  }
}