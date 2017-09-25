package com.seanshubin.builder.prototype

import scala.concurrent.Future

trait Dispatcher {
  def doSearch(): Future[Seq[String]]

  def process(name: String): Future[ProcessResult]

  def setDone(): Unit
}
