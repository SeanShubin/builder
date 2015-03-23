package com.seanshubin.builder.core

import com.seanshubin.up_to_date.console.RunnerWiring

class RunnerImpl(configuration: Configuration, api: Api, notifications: Notifications, reporter: Reporter) extends Runner {

  override def run(): Unit = {
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
      upgradeDependencies()
      doBuildProcess()
    }
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

  def upgradeDependencies(): Unit ={
    RunnerWiring(configuration.upToDateConfiguration).runner.run()
  }
}
