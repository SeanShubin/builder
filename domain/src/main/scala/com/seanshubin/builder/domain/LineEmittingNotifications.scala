package com.seanshubin.builder.domain

class LineEmittingNotifications(emit: String => Unit) extends Notifications {
  override def projectFoundInGithub(name: String): Unit = {
    emit(s"project found in github: '$name'")
  }
}
