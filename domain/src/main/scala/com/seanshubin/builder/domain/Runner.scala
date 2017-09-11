package com.seanshubin.builder.domain

class Runner(projectFinder: ProjectFinder) extends Runnable {
  override def run() = {
    projectFinder.findProjects()
  }
}
