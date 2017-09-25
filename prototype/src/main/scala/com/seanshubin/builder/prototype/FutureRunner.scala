package com.seanshubin.builder.prototype

import scala.concurrent.Future

trait FutureRunner {
  def runInFuture[T](block: => T): Future[T]
}
