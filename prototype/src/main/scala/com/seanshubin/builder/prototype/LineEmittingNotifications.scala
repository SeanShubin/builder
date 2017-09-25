package com.seanshubin.builder.prototype

import akka.typed.Signal

class LineEmittingNotifications(emit: String => Unit) extends Notifications {
  override def asyncException(ex: Throwable): Unit = {
    val s = ExceptionUtil.toStringWithStackTrace(ex)
    emit(s)
  }

  override def event(event: Event): Unit = {
    emit(s"event: $event")
  }

  override def signal(signal: Signal): Unit = {
    emit(s"signal: $signal")
  }
}
