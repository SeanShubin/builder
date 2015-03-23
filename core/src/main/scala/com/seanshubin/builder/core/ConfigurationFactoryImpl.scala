package com.seanshubin.builder.core

import java.nio.charset.Charset
import java.nio.file.Paths

import com.seanshubin.devon.core.devon.DevonMarshaller
import com.seanshubin.up_to_date.logic.{GroupArtifactVersion, GroupAndArtifact}
import com.seanshubin.utility.filesystem.FileSystemIntegration
import com.seanshubin.up_to_date.logic.{Configuration => UpToDateConfiguration}

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
  private val sampleUpToDateConfiguration = new UpToDateConfiguration(
    pomFileName = "pom.xml",
    directoryNamesToSkip = Set("target"),
    directoriesToSearch = Seq(Paths.get(".")),
    mavenRepositories = Seq(
      "http://repo.maven.apache.org/maven2",
      "http://onejar-maven-plugin.googlecode.com/svn/mavenrepo",
      "http://oss.sonatype.org/content/groups/scala-tools"),
    doNotUpgradeFrom = Set(GroupAndArtifact("groupIdToIgnore", "artifactIdToIgnore")),
    doNotUpgradeTo = Set(GroupArtifactVersion("groupIdToIgnore", "artifactIdToIgnore", "1.2.3")),
    automaticallyUpgrade = true,
    reportDirectory = Paths.get("generated", "sample", "report"),
    cacheDirectory = Paths.get("generated", "cache"),
    cacheExpire = "5 days")

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
      ProjectConfig("my-application", CommandEnum.Verify)),
    upToDateConfiguration = sampleUpToDateConfiguration)
}
