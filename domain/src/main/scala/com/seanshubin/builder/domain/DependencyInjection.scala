package com.seanshubin.builder.domain

import java.nio.charset.{Charset, StandardCharsets}
import java.nio.file.{Path, Paths}
import java.time.Clock
import java.util.concurrent.TimeUnit

import akka.typed.{ActorSystem, Behavior}
import com.seanshubin.http.values.client.google.HttpSender
import com.seanshubin.http.values.domain.Sender

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Promise}

trait DependencyInjection {
  implicit val executionContext: ExecutionContext = new MyExecutionContext(ExecutionContext.Implicits.global)
  val done: Promise[State] = Promise()
  val userName: String = "SeanShubin"
  val baseDirectory: Path = Paths.get("/Users/sshubin/github/sean")
  val sender: Sender = new HttpSender
  val jsonMarshaller: JsonMarshaller = JacksonJsonMarshaller
  val emit: String => Unit = println
  val notifications: Notifications = new LineEmittingNotifications(emit)
  val createProcessBuilder: () => ProcessBuilderContract = ProcessBuilderDelegate.apply _
  val futureRunner: FutureRunner = new ExecutionContextFutureRunner(notifications.unhandledException(_))
  val clock: Clock = Clock.systemUTC()
  val charset: Charset = StandardCharsets.UTF_8
  val systemSpecific: SystemSpecific = new SystemSpecificImpl
  val processLauncher: ProcessLauncher = new ProcessLauncherImpl(createProcessBuilder, futureRunner, clock, charset)
  val stateMachine: Behavior[Event] = new StateMachine(done)
  val actorSystem: ActorSystem[Event] = ActorSystem("coordinator", stateMachine)
  val eventBuilder = new EventBuilder(actorSystem)
  val localProjectFinder: ProjectFinder = new LocalProjectFinder(
    systemSpecific,
    processLauncher)
  val githubProjectFinder: ProjectFinder = new GithubProjectFinder(
    userName,
    sender,
    jsonMarshaller,
    futureRunner)
  val duration = Duration(1, TimeUnit.HOURS)
  val cleaner: Cleaner = new CleanerImpl(actorSystem)
  val projectCommandRunner: ProjectCommandRunner = new ProjectCommandRunnerImpl(baseDirectory, processLauncher)
  val dispatcher: Dispatcher = new DispatcherImpl(githubProjectFinder, localProjectFinder, projectCommandRunner, actorSystem.tell _)
  val runner: Runnable = new Runner(
    dispatcher,
    done.future,
    duration,
    cleaner)
}
