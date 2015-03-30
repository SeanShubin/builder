package com.seanshubin.builder.core

import com.seanshubin.up_to_date.console.RunnerWiring

class RunnerImpl(configuration: Configuration,
                 api: Api,
                 notifications: Notifications,
                 reporter: Reporter,
                 timer: Timer,
                 shouldUpgradeDependencies: Boolean) extends Runner {

  override def run(): Unit = {
    val elapsed = timer.elapsedTimeFor {
      val configuredMap = configuration.projects.map(p => (p.name, p)).toMap
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
      project.command.execute(project.name, api)
    }
    val results = configuration.projects.map(processProject)
    notifications.summarize(results)
    reporter.storeAllReports(results)
    notifications.highlightErrors(results)
  }

  def upgradeDependencies(): Unit = {
    RunnerWiring(configuration.upToDateConfiguration).runner.run()
    def processProject(project: ProjectConfig): Seq[ExecutionResult] = {
      if (api.pendingLocalEdits(project.name) && project.name != "learn-spark") {
        api.addCommitPush(project.name, "Automatically upgrade maven dependencies to latest")
      } else {
        Seq()
      }
    }
    val upgradeResults = configuration.projects.flatMap(processProject)
    reporter.storeUpgradeResults(upgradeResults)
  }
}
