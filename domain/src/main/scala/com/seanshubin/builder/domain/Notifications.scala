package com.seanshubin.builder.domain

trait Notifications {
  def projectsFoundInGithub(names: Seq[String]): Unit

  def projectsFoundLocally(names: Seq[String]): Unit

  def errorFindingProjectsInGithub(): Unit

  def errorFindingProjectsLocally(ex: Throwable): Unit

  def unhandledException(ex: Throwable): Unit
}
