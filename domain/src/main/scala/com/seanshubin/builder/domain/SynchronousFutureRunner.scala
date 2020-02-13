package com.seanshubin.builder.domain

import scala.concurrent.{ExecutionContext, Future}

class SynchronousFutureRunner(notifyOfException: Throwable => Unit) extends FutureRunner {
  private implicit val synchronousExecutionContext:ExecutionContext = new SynchronousExecutionContext
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
