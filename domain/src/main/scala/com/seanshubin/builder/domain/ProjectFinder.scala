package com.seanshubin.builder.domain

import scala.concurrent.Future

trait ProjectFinder {
  def findProjects(): Future[Seq[String]]
}
