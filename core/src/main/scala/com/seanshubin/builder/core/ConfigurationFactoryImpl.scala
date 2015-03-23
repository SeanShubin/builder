package com.seanshubin.builder.core

import java.nio.charset.Charset
import java.nio.file.Paths

import com.seanshubin.devon.core.devon.DevonMarshaller
import com.seanshubin.utility.filesystem.FileSystemIntegration

class ConfigurationFactoryImpl(fileSystem: FileSystemIntegration,
                               devonMarshaller: DevonMarshaller,
                               charset: Charset) extends ConfigurationFactory {
  override def validate(args: Seq[String]): Either[Seq[String], Configuration] = {
    if (args.length == 1) {
      val configFilePath = Paths.get(args(0))
      try {
        if (fileSystem.exists(configFilePath)) {
          val bytes = fileSystem.readAllBytes(configFilePath)
          val text = new String(bytes, charset)
          val config = devonMarshaller.stringToValue(text, classOf[Configuration])
          Right(config)
        } else {
          Left(Seq(s"Configuration file named '$configFilePath' not found"))
        }
      } catch {
        case ex: Throwable =>
          Left(Seq(s"There was a problem reading the configuration file '$configFilePath': ${ex.getMessage}"))
      }
    } else {
      val sampleConfigDevon = devonMarshaller.fromValue(ConfigurationFactoryImpl.sampleConfiguration)
      val prettySampleLines = devonMarshaller.toPretty(sampleConfigDevon)
      Left(Seq(
        "Expected exactly one argument, the name of the configuration file",
        "A typical configuration file might look something like this",
        "") ++ prettySampleLines)
    }
  }
}

object ConfigurationFactoryImpl {
  val sampleConfiguration: Configuration = Configuration(
    githubUserName = "SeanShubin",
    reportDirectory = Paths.get("generated", "reports"),
    environmentByOsName = Map(
      "Linux" -> Environment(
        baseDirectory = Paths.get("/home/sshubin/temp/git"),
        commandPrefix = "",
        directoryListingCommand = "ls -1"),
      "Windows 7" -> Environment(
        baseDirectory = Paths.get("G:\\keep\\temp\\git"),
        commandPrefix = "cmd /C ",
        directoryListingCommand = "ls -1")
    ),
    projects = Seq(
      ProjectConfig("my-library", CommandEnum.Deploy),
      ProjectConfig("my-application", CommandEnum.Verify)))
}
