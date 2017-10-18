package com.seanshubin.builder.domain

import java.nio.charset.{Charset, StandardCharsets}
import java.nio.file.Path
import java.time.{Clock, Instant}
import java.util.concurrent.TimeUnit

import akka.typed.{ActorSystem, Behavior, Signal}
import com.seanshubin.http.values.client.google.HttpSender
import com.seanshubin.http.values.domain.Sender

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Promise}

trait DependencyInjection {
  def systemSpecific: SystemSpecific

  implicit val executionContext: ExecutionContext = new MyExecutionContext(ExecutionContext.Implicits.global)
  val files: FilesContract = FilesDelegate
  val system: SystemContract = SystemDelegate
  val done: Promise[Unit] = Promise()
  val userName: String = "SeanShubin"
  val baseDirectory: Path = systemSpecific.githubDirectory
  val logDirectory: Path = baseDirectory.resolve("builder/logs")
  val sender: Sender = new HttpSender
  val jsonMarshaller: JsonMarshaller = JacksonJsonMarshaller
  val emitToView: String => Unit = println
  val clock: Clock = Clock.systemUTC()
  val startTime: Instant = clock.instant()
  val rootLogger: String => Unit = new RootLogger(logDirectory, startTime, emitToView, files, system)
  val notifications: Notifications = new LineEmittingNotifications(rootLogger)
  val createProcessBuilder: () => ProcessBuilderContract = ProcessBuilderDelegate.apply _
  val futureRunner: FutureRunner = new ExecutionContextFutureRunner(notifications.unhandledException(_))
  val charset: Charset = StandardCharsets.UTF_8
  val processLauncher: ProcessLauncher = new ProcessLauncherImpl(
    createProcessBuilder,
    futureRunner,
    clock,
    charset,
    notifications.processLaunched)
  val notifySignal: Signal => Unit = notifications.signal
  val notifyEvent: Event => Unit = notifications.event
  val loggerFactory = new ProcessLoggerFactoryImpl(files, logDirectory, startTime, rootLogger)
  val githubProjectFinder: ProjectFinder = new GithubProjectFinder(
    userName,
    sender,
    jsonMarshaller,
    futureRunner)
  val localProjectFinder: ProjectFinder = new LocalProjectFinder(
    systemSpecific,
    processLauncher,
    loggerFactory)
  val dependencyUpgrader: DependencyUpgrader = new DependencyUpgraderImpl(
    systemSpecific.githubDirectory,
    logDirectory,
    startTime
  )
  val dispatcher: Dispatcher = new DispatcherImpl(
    githubProjectFinder,
    localProjectFinder,
    systemSpecific,
    baseDirectory,
    processLauncher,
    loggerFactory,
    dependencyUpgrader,
    notifications.statusUpdate,
    notifications.statusSummary,
    notifications.unsupportedEventFromState,
    done)
  val stateMachine: Behavior[Event] = new StateMachine(dispatcher, notifySignal, notifyEvent)
  val actorSystem: ActorSystem[Event] = ActorSystem(stateMachine, "coordinator")
  val eventBuilder = new EventBuilder(actorSystem)
  val duration = Duration(1, TimeUnit.HOURS)
  val cleaner: Cleaner = new CleanerImpl(actorSystem)
  val runner: Runnable = new Runner(clock, actorSystem, done.future, duration, notifications.startAndEndTime)
}
