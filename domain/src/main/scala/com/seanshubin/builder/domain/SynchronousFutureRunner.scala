package com.seanshubin.builder.domain

import scala.concurrent.Future

class SynchronousFutureRunner(notifyOfException: Throwable => Unit) extends FutureRunner {
  override def runInFuture[T](block: => T): Future[T] = {
    try {
      val result = block
      Future {
        result
      }
    } catch {
      case ex: Throwable =>
        notifyOfException(ex)
        Future {
          throw ex
        }
    }
  }
}
