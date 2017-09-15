package com.seanshubin.builder.domain

import scala.concurrent.Future

trait FutureRunner {
  def runInFuture[T](block: => T): Future[T]
}
