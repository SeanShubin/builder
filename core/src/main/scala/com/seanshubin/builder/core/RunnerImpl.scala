package com.seanshubin.builder.core

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
    if (projects.forall(p => p.isOkToBuild)) {
      def processProject(project: ProjectConfig): Report = {
        project.command.execute(project.name, api)
      }
      val results = configuration.projects.map(processProject)
      notifications.summarize(results)
      reporter.storeAllReports(results)
      notifications.highlightErrors(results)
    }
  }
}
