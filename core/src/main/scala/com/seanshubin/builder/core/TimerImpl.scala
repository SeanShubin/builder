package com.seanshubin.builder.core

class TimerImpl(systemClock: SystemClock) extends Timer {
  override def elapsedTimeFor[T](f: => T): Long = {
    val timeBefore = systemClock.currentTimeMillis
    f
    val timeAfter = systemClock.currentTimeMillis
    val duration = timeAfter - timeBefore
    duration
  }
}
