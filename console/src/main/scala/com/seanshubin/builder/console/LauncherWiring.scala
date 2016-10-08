package com.seanshubin.builder.console

import java.nio.charset.{Charset, StandardCharsets}

import com.seanshubin.builder.core._
import com.seanshubin.devon.domain.{DevonMarshaller, DevonMarshallerWiring}
import com.seanshubin.utility.filesystem.{FileSystemIntegration, FileSystemIntegrationImpl}

trait LauncherWiring {
  def commandLineArguments: Seq[String]

  lazy val emitLine: String => Unit = println
  lazy val fileSystem: FileSystemIntegration = new FileSystemIntegrationImpl
  lazy val devonMarshaller: DevonMarshaller = DevonMarshallerWiring.builder().addConversion(CommandDevonConversion).build()
  lazy val charset: Charset = StandardCharsets.UTF_8
  lazy val notifications: Notifications = new LineEmittingNotifications(devonMarshaller, emitLine)
  lazy val configurationFactory: ConfigurationFactory = new ConfigurationFactoryImpl(
    fileSystem, devonMarshaller, charset)
  lazy val runnerFactory: Configuration => Runnable = (theConfiguration) => new RunnerWiring {
    override def configuration: Configuration = theConfiguration
  }.runner
  lazy val launcher: Runnable = new LauncherImpl(
    commandLineArguments, configurationFactory, runnerFactory, notifications)
}
