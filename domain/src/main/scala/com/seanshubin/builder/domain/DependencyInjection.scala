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
  val clock: Clock = Clock.systemUTC()
  val startTime: Instant = clock.instant()
  val logDirectory: Path = baseDirectory.resolve("builder/logs").resolve(PathUtil.makeFileNameSafeForOperatingSystem(startTime.toString))
  val pathToStoreState: Path = logDirectory.resolve("current-status.txt")
  val sender: Sender = new HttpSender
  val jsonMarshaller: JsonMarshaller = JacksonJsonMarshaller
  val emitToView: String => Unit = println
  val rootLogger: String => Unit = new RootLogger(logDirectory, emitToView, files, system)
  val charset: Charset = StandardCharsets.UTF_8
  val statefulLogger: StatefulLogger = new StatefulLoggerImpl(pathToStoreState, files, charset)
  val notifications: Notifications = new LineEmittingNotifications(rootLogger, statefulLogger)
  val createProcessBuilder: () => ProcessBuilderContract = ProcessBuilderDelegate.apply _
  val futureRunner: FutureRunner = new ExecutionContextFutureRunner(notifications.unhandledException(_))
  val processLauncher: ProcessLauncher = new ProcessLauncherImpl(
    createProcessBuilder,
    systemSpecific,
    futureRunner,
    clock,
    charset,
    notifications.processLaunched)
  val notifySignal: Signal => Unit = notifications.signal
  val notifyEvent: Event => Unit = notifications.event
  val loggerFactory = new ProcessLoggerFactoryImpl(files, logDirectory, rootLogger)
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
    logDirectory
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
