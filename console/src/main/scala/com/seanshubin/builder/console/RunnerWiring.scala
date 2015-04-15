package com.seanshubin.builder.console

import java.nio.charset.{Charset, StandardCharsets}

import com.seanshubin.builder.core._
import com.seanshubin.devon.core.devon.DevonMarshaller
import com.seanshubin.http.values.client.google.HttpSender
import com.seanshubin.utility.filesystem.{FileSystemIntegration, FileSystemIntegrationImpl}
import com.seanshubin.utility.json.{JsonMarshaller, JsonMarshallerImpl}

trait RunnerWiring {
  def configuration: Configuration

  lazy val osName = System.getProperty("os.name")
  lazy val emitLine: String => Unit = println
  lazy val devonMarshaller: DevonMarshaller = CustomDevonMarshallerWiring.devonMarshaller
  lazy val fileSystem: FileSystemIntegration = new FileSystemIntegrationImpl
  lazy val charset: Charset = StandardCharsets.UTF_8
  lazy val reporter: Reporter = new ReporterImpl(configuration.reportDirectory, fileSystem, devonMarshaller, charset)
  lazy val notifications: Notifications = new LineEmittingNotifications(devonMarshaller, emitLine)
  lazy val environment: Environment = configuration.environmentByOsName(osName)
  lazy val emitProcessLine: String => Unit = notifications.execOutput
  lazy val processLoggerFactory: ProcessLoggerFactory = new ProcessLoggerFactoryImpl(emitProcessLine)
  lazy val systemExecutor: SystemExecutor = new SystemExecutorImpl(processLoggerFactory)
  lazy val jsonMarshaller: JsonMarshaller = new JsonMarshallerImpl
  lazy val httpSender: HttpSender = new HttpSender
  lazy val githubApi: GithubApi = new GithubApiImpl(httpSender, jsonMarshaller)
  lazy val systemApi: SystemApi = new SystemApiImpl(systemExecutor, environment, notifications)
  lazy val api: Api = new ApiImpl(systemApi, githubApi, configuration.githubUserName)
  lazy val systemClock: SystemClock = new SystemClockImpl()
  lazy val timer: Timer = new TimerImpl(systemClock)
  lazy val runner: Runner = new RunnerImpl(configuration, api, notifications, reporter, timer, environment.shouldUpgradeDependencies)
}
