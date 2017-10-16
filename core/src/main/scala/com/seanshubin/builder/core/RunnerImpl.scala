package com.seanshubin.builder.core

import java.nio.file.Path

import com.seanshubin.uptodate.console.ApplicationDependencyInjection

class RunnerImpl(configuration: Configuration,
                 api: Api,
                 notifications: Notifications,
                 reporter: Reporter,
                 timer: Timer,
                 shouldUpgradeDependencies: Boolean,
                 githubDirectory: Path) extends Runnable {
  override def run(): Unit = {
    val elapsed = timer.elapsedTimeFor {
      val configuredMap = configuration.projects.map(p => (p.name, p)).toMap
      val configuredNames = configuredMap.keySet.toSeq.sorted
      configuredNames.foreach(api.cloneIfMissing)
      val localNames = api.projectNamesLocal()
      val githubNames = api.projectNamesInGithub()
      val allNames = (configuredMap.keySet ++ localNames ++ githubNames).toSeq.sorted
      val projects = for {
        name <- allNames
      } yield {
        Project(
          name,
          configuredMap.contains(name),
          configuredMap.get(name).exists(p => p.command == CommandEnum.Ignore),
          githubNames.contains(name),
          localNames.contains(name),
          api.pendingLocalEdits(name)
        )
      }
      notifications.projects(projects)

      val okToBuild = projects.forall(p => p.isOkToBuild)

      if (okToBuild) {
        if (shouldUpgradeDependencies) {
          upgradeDependencies()
        }
        doBuildProcess()
      }
    }
    notifications.totalMillisecondsElapsed(elapsed)
  }

  def doBuildProcess(): Unit = {
    def processProject(project: ProjectConfig): Report = {
      try {
        project.command.execute(project.name, api)
      } catch {
        case ex: Throwable =>
          ExceptionReport(project.name, ex)
      }
    }

    val results = configuration.projects.map(processProject)
    notifications.summarize(results)
    reporter.storeAllReports(results)
    notifications.highlightErrors(results)
  }

  def upgradeDependencies(): Unit = {
    val originalUpToDateConfiguration = configuration.upToDateConfiguration
    val upToDateConfiguration = originalUpToDateConfiguration.copy(directoriesToSearch = Seq(githubDirectory))
    ApplicationDependencyInjection.createRunner(upToDateConfiguration).run()

    def processProject(project: ProjectConfig): Seq[ExecutionResult] = {
      if (api.pendingLocalEdits(project.name) && project.name != "learn-spark") {
        val fetchRebaseResult = api.fetchRebase(project.name)
        val addCommitPushResult = api.addCommitPush(project.name, "Automatically upgrade maven dependencies to latest")
        fetchRebaseResult ++ addCommitPushResult
      } else {
        Seq()
      }
    }

    val upgradeResults = configuration.projects.flatMap(processProject)
    reporter.storeUpgradeResults(upgradeResults)
  }
}
