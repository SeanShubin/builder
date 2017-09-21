package com.seanshubin.builder.domain

trait Dispatcher {
  def findLocalProjects(): Unit

  def findRemoteProjects(): Unit

  def cloneProject(name: String): Unit

  def buildProject(name: String): Unit
}
