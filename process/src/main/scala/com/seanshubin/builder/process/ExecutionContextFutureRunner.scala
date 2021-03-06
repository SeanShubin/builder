package com.seanshubin.builder.process

import scala.concurrent.{ExecutionContext, Future}

class ExecutionContextFutureRunner(implicit executionContext: ExecutionContext) extends FutureRunner {
  override def runInFuture[T](block: => T): Future[T] = {
    Future {
      block
    }
  }
}
