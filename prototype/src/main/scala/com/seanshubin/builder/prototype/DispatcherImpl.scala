package com.seanshubin.builder.prototype

import scala.concurrent.{Future, Promise}

class DispatcherImpl(futureRunner: FutureRunner, done: Promise[Unit]) extends Dispatcher {
  override def doSearch(): Future[Seq[String]] = futureRunner.runInFuture(Seq("found-1", "found-2"))

  override def process(name: String): Future[ProcessResult] = futureRunner.runInFuture(ProcessResult(name, 0))

  override def setDone(): Unit = {
    done.success(())
  }
}
