package com.seanshubin.builder.domain

class Consumer(buildEventQueue: BuildEventQueue, buildEventHandler: BuildEventHandler) extends Runnable {
  override def run(): Unit = {
    while (buildEventQueue.nonEmpty) {
      val buildEvent = buildEventQueue.pull()
      buildEventHandler.handle(buildEvent)
    }
  }
}
