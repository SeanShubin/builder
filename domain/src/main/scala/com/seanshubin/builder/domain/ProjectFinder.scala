package com.seanshubin.builder.domain

class ProjectFinder(projectFound: String => Unit) extends Runnable {
  override def run(): Unit = {
    projectFound("ignore me")
    projectFound("succeed first try")
    projectFound("succeed second try")
    projectFound("always fail")
  }
}
