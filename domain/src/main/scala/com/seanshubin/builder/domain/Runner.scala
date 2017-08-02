package com.seanshubin.builder.domain

class Runner(projectFinder: Runnable, consumer: Consumer) extends Runnable {
  override def run(): Unit = {
    projectFinder.run()
    consumer.run()
  }
}
