package com.seanshubin.builder.domain

sealed trait Event

object Event {

  case class ProjectsFoundInGithub(names: Seq[String]) extends Event

  case class ProjectsFoundLocally(names: Seq[String]) extends Event

  case class ErrorFindingProjectsInGithub(ex: Throwable) extends Event

  case class ErrorFindingProjectsLocally(ex: Throwable) extends Event

}