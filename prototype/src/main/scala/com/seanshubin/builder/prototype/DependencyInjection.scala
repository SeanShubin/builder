package com.seanshubin.builder.prototype

import java.util.concurrent.TimeUnit

import akka.typed.{ActorSystem, Behavior}

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Promise}

class DependencyInjection {
  implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global
  val emit: String => Unit = println
  val notifications: Notifications = new LineEmittingNotifications(emit)
  val futureRunner = new ExecutionContextFutureRunner(notifications.asyncException _)
  val done: Promise[Unit] = Promise()
  val dispatcher: Dispatcher = new DispatcherImpl(futureRunner, done)
  val behavior: Behavior[Event] = new PrototypeBehavior(
    dispatcher,
    notifications.signal _,
    notifications.event _)
  val actorSystem: ActorSystem[Event] = ActorSystem("behavior", behavior)
  val duration: Duration = Duration(5, TimeUnit.SECONDS)
  val runnable: Runnable = new Runner(actorSystem, done.future, duration)
}
