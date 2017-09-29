package com.seanshubin.builder.domain


import java.time.{Clock, Instant}

import akka.typed.ActorSystem

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class Runner(clock: Clock,
             actorSystem: ActorSystem[Event],
             done: Future[Unit],
             duration: Duration,
             notifyStartAndEndTime: (Instant, Instant) => Unit) extends Runnable {
  override def run(): Unit = {
    val startTime = clock.instant()
    actorSystem ! Event.Initialize
    try {
      Await.ready(done, duration)
      val endTime = clock.instant()
      notifyStartAndEndTime(startTime, endTime)
    } finally {
      actorSystem.terminate()
    }
  }
}
