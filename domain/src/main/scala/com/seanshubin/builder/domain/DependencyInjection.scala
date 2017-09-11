package com.seanshubin.builder.domain

import com.seanshubin.http.values.client.google.HttpSender
import com.seanshubin.http.values.domain.Sender

trait DependencyInjection {
  val userName: String = "SeanShubin"
  val sender: Sender = new HttpSender
  val jsonMarshaller: JsonMarshaller = JacksonJsonMarshaller
  val emit: String => Unit = println
  val notifications: Notifications = new LineEmittingNotifications(emit)
  val projectFound: String => Unit = notifications.projectFoundInGithub
  val projectFinder: ProjectFinder = new ProjectFinderImpl(userName, sender, jsonMarshaller, projectFound)
  val runner: Runnable = new Runner(projectFinder)
}
