package com.seanshubin.builder.domain

import scala.concurrent.ExecutionContext

class SynchronousExecutionContext extends ExecutionContext {
  override def execute(runnable: Runnable): Unit = runnable.run()

  override def reportFailure(cause: Throwable): Unit = throw cause
}
