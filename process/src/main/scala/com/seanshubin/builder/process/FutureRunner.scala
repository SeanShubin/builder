package com.seanshubin.builder.process

import scala.concurrent.Future

trait FutureRunner {
  def runInFuture[T](block: => T): Future[T]
}
