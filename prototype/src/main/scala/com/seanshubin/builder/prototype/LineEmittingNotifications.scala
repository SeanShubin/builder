package com.seanshubin.builder.prototype

import java.io.{PrintWriter, StringWriter}

import akka.typed.Signal

class LineEmittingNotifications(emit: String => Unit) extends Notifications {
  override def asyncException(ex: Throwable): Unit = {
    val stringWriter = new StringWriter()
    val printWriter = new PrintWriter(stringWriter)
    ex.printStackTrace(printWriter)
    val s = stringWriter.getBuffer.toString
    emit(s)
  }

  override def event(event: Event): Unit = {
    emit(s"event: $event")
  }

  override def signal(signal: Signal): Unit = {
    emit(s"signal: $signal")
  }
}
