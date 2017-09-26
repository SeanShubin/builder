package com.seanshubin.builder.domain


import akka.typed.ActorSystem

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class Runner(actorSystem: ActorSystem[Event], done: Future[Unit], duration: Duration) extends Runnable {
  override def run(): Unit = {
    actorSystem ! Event.Initialize
    Await.ready(done, duration)
    actorSystem.terminate()
  }
}
