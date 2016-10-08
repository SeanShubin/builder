package com.seanshubin.builder.console

import java.nio.charset.{Charset, StandardCharsets}
import java.nio.file.Path

import com.seanshubin.builder.core._
import com.seanshubin.devon.domain.{DevonMarshaller, DevonMarshallerWiring}
import com.seanshubin.http.values.client.google.HttpSender
import com.seanshubin.utility.filesystem.{FileSystemIntegration, FileSystemIntegrationImpl}

trait RunnerWiring {
  def configuration: Configuration

  lazy val userHome = System.getProperty("user.home")
  lazy val emitLine: String => Unit = println
  lazy val devonMarshaller: DevonMarshaller = DevonMarshallerWiring.Default
  lazy val fileSystem: FileSystemIntegration = new FileSystemIntegrationImpl
  lazy val charset: Charset = StandardCharsets.UTF_8
  lazy val reporter: Reporter = new ReporterImpl(configuration.reportDirectory, fileSystem, devonMarshaller, charset)
  lazy val notifications: Notifications = new LineEmittingNotifications(devonMarshaller, emitLine)
  lazy val environment: Settings = configuration.settingsByUserHome(userHome)
  lazy val githubDirectory: Path = environment.githubDirectory
  lazy val emitProcessLine: String => Unit = notifications.execOutput
  lazy val processLoggerFactory: ProcessLoggerFactory = new ProcessLoggerFactoryImpl(emitProcessLine)
  lazy val systemExecutor: SystemExecutor = new SystemExecutorImpl(processLoggerFactory)
  lazy val jsonMarshaller: JsonMarshaller = new JsonMarshallerImpl
  lazy val httpSender: HttpSender = new HttpSender
  lazy val githubApi: GithubApi = new GithubApiImpl(httpSender, jsonMarshaller)
  lazy val systemApi: SystemApi = new SystemApiImpl(systemExecutor, githubDirectory, environment, notifications)
  lazy val api: Api = new ApiImpl(systemApi, githubApi, configuration.githubUserName)
  lazy val systemClock: SystemClock = new SystemClockImpl()
  lazy val timer: Timer = new TimerImpl(systemClock)
  lazy val runner: Runnable = new RunnerImpl(configuration, api, notifications, reporter, timer, environment.shouldUpgradeDependencies)
}
