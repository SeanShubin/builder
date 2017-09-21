package com.seanshubin.builder.domain

import scala.concurrent.ExecutionContext

class MyExecutionContext(delegate: ExecutionContext) extends ExecutionContext {
  override def execute(runnable: Runnable): Unit = {
    try {
      delegate.execute(runnable)
    } catch {
      case ex: Throwable =>
        ex.printStackTrace()
        throw ex
    }
  }

  override def reportFailure(cause: Throwable): Unit = {
    cause.printStackTrace()
    delegate.reportFailure(cause)
  }
}
