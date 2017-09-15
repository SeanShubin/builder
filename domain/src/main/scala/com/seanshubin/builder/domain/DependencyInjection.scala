package com.seanshubin.builder.domain

import java.nio.charset.{Charset, StandardCharsets}
import java.time.Clock
import java.util.concurrent.TimeUnit

import akka.typed.{ActorSystem, Behavior}
import com.seanshubin.http.values.client.google.HttpSender
import com.seanshubin.http.values.domain.Sender

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise
import scala.concurrent.duration.Duration

trait DependencyInjection {
  val done: Promise[State] = Promise()
  val userName: String = "SeanShubin"
  val sender: Sender = new HttpSender
  val jsonMarshaller: JsonMarshaller = JacksonJsonMarshaller
  val emit: String => Unit = println
  val notifications: Notifications = new LineEmittingNotifications(emit)
  val createProcessBuilder: () => ProcessBuilderContract = ProcessBuilderDelegate.apply
  val futureRunner: FutureRunner = new ExecutionContextFutureRunner
  val clock: Clock = Clock.systemUTC()
  val charset: Charset = StandardCharsets.UTF_8
  val systemSpecific: SystemSpecific = new SystemSpecificImpl
  val processLauncher: ProcessLauncher = new ProcessLauncherImpl(createProcessBuilder, futureRunner, clock, charset)
  val coordinator: Behavior[Event] = new Coordinator(done)
  val actorSystem: ActorSystem[Event] = ActorSystem("coordinator", coordinator)
  val eventBuilder = new EventBuilder(actorSystem)
  val localProjectFinder: ProjectFinder = new LocalProjectFinder(
    systemSpecific,
    processLauncher,
    eventBuilder.projectsFoundLocally,
    eventBuilder.errorFindingProjectsLocally)
  val githubProjectFinder: ProjectFinder = new GithubProjectFinder(
    userName,
    sender,
    jsonMarshaller,
    eventBuilder.projectsFoundInGithub)
  val duration = Duration(1, TimeUnit.HOURS)
  val cleaner: Cleaner = new CleanerImpl(actorSystem)
  val runner: Runnable = new Runner(
    localProjectFinder,
    githubProjectFinder,
    done.future,
    duration,
    cleaner)
}
