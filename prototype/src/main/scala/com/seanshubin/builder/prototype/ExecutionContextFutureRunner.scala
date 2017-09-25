package com.seanshubin.builder.prototype

import scala.concurrent.{ExecutionContext, Future}

class ExecutionContextFutureRunner(notifyOfException: Throwable => Unit)
                                  (implicit executionContext: ExecutionContext) extends FutureRunner {
  override def runInFuture[T](block: => T): Future[T] = {
    Future {
      try {
        block
      } catch {
        case ex: Throwable =>
          notifyOfException(ex)
          throw ex
      }
    }
  }
}
