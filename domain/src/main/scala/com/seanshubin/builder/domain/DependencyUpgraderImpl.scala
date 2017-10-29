package com.seanshubin.builder.domain

import java.nio.file.Path

import com.seanshubin.uptodate.console.ConfigurationDependencyInjection
import com.seanshubin.uptodate.logic.{Configuration, GroupArtifactVersion, SummaryReport}

class DependencyUpgraderImpl(baseDirectory: Path,
                             baseLogDirectory: Path) extends DependencyUpgrader {
  override def upgradeDependencies(projectName: String): SummaryReport = {
    val path = baseDirectory.resolve(projectName)
    val logDir = baseLogDirectory.resolve("command").resolve(projectName).resolve("up-to-date")
    val upToDateCacheDir = baseLogDirectory.resolve("up-to-date-cache")
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
