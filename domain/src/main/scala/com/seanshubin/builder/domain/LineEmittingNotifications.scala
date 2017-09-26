package com.seanshubin.builder.domain

import akka.typed.Signal

class LineEmittingNotifications(emit: String => Unit) extends Notifications {
  override def projectsFoundInGithub(names: Seq[String]): Unit = {
    names.map(x => s"project found in github: $x").foreach(emit)
  }

  override def projectsFoundLocally(names: Seq[String]): Unit = {
    names.map(x => s"project found locally: $x").foreach(emit)
  }

  override def errorFindingProjectsInGithub(): Unit = ???

  override def errorFindingProjectsLocally(ex: Throwable): Unit = ???

  override def unhandledException(ex: Throwable): Unit = {
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
