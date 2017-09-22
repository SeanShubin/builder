package com.seanshubin.builder.domain

import java.io.{PrintWriter, StringWriter}

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
    val stringWriter = new StringWriter()
    val printWriter = new PrintWriter(stringWriter)
    ex.printStackTrace(printWriter)
    val s = stringWriter.getBuffer.toString
    emit(s)
  }
}
