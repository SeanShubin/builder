package com.seanshubin.builder.core

import java.nio.charset.Charset
import java.nio.file.Paths

import com.seanshubin.devon.core.devon.DevonMarshaller
import com.seanshubin.up_to_date.logic.{Configuration => UpToDateConfiguration, GroupAndArtifact, GroupArtifactVersion}
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
          val text = new String(bytes.toArray, charset)
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
    settingsByUserHome = Map(
      "/home/sshubin/temp/git" -> Settings(
        githubDirectory = Paths.get("/","Users","sshubin","github","sean"),
        commandPrefix = Seq(""),
        directoryListingCommand = Seq("ls", "-1"),
        shouldUpgradeDependencies = true,
        mavenSettings = Some("/home/sshubin/bin/maven-settings/sean-settings.xml")
      ),
      "G:\\keep\\temp\\git" -> Settings(
        githubDirectory = Paths.get("G:\\","keep","github","sean"),
        commandPrefix = Seq("cmd", "/C"),
        directoryListingCommand = Seq("ls", "-1"),
        shouldUpgradeDependencies = false,
        mavenSettings = None)
    ),
    projects = Seq(
      ProjectConfig("my-library", CommandEnum.Deploy),
      ProjectConfig("my-application", CommandEnum.Verify)),
    upToDateConfiguration = sampleUpToDateConfiguration)
}
