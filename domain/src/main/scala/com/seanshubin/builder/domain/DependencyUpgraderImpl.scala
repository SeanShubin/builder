package com.seanshubin.builder.domain

import java.nio.file.Path
import java.time.Instant

import com.seanshubin.uptodate.console.ConfigurationDependencyInjection
import com.seanshubin.uptodate.logic.{Configuration, GroupArtifactVersion, SummaryReport}

class DependencyUpgraderImpl(baseDirectory: Path,
                             baseLogDirectory: Path,
                             startTime: Instant) extends DependencyUpgrader {
  override def upgradeDependencies(projectName: String): SummaryReport = {
    val path = baseDirectory.resolve(projectName)
    val logDir = baseLogDirectory.resolve(startTime.toString).resolve("up-to-date").resolve(projectName)
    val upToDateCacheDir = baseDirectory.resolve("up-to-date-cache")
    val buggyJackson = GroupArtifactVersion(
      group = "com.fasterxml.jackson.module",
      artifact = "jackson-module-scala_2.12",
      version = "2.9.0"
    )
    val upToDateConfiguration = Configuration(
      pomFileName = "pom.xml",
      directoryNamesToSkip = Set("target"),
      directoriesToSearch = Seq(path),
      mavenRepositories = Seq("http://thoughtfulcraftsmanship.com/nexus/content/groups/public"),
      automaticallyUpgrade = true,
      doNotUpgradeTo = Set(buggyJackson),
      doNotUpgradeFrom = Set(),
      reportDirectory = logDir,
      cacheDirectory = upToDateCacheDir,
      cacheExpire = "5 days"
    )
    val summary = ConfigurationDependencyInjection(upToDateConfiguration).flow.run()
    summary
  }
}
