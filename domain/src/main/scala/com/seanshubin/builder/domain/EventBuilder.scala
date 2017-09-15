package com.seanshubin.builder.domain

import akka.typed.ActorSystem

class EventBuilder(actorSystem: ActorSystem[Event]) {
  def projectsFoundInGithub(names: Seq[String]): Unit = actorSystem ! Event.ProjectsFoundInGithub(names)

  def projectsFoundLocally(names: Seq[String]): Unit = actorSystem ! Event.ProjectsFoundLocally(names)

  def errorFindingProjectsInGithub(ex: Throwable): Unit = actorSystem ! Event.ErrorFindingProjectsInGithub(ex)

  def errorFindingProjectsLocally(ex: Throwable): Unit = actorSystem ! Event.ErrorFindingProjectsLocally(ex)

}
