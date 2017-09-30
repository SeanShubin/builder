package com.seanshubin.builder.domain

import java.nio.file.Paths

trait SystemSpecificDependencyInjection {
  val system: SystemContract = SystemDelegate
  val map: Map[String, SystemSpecific] = Map(
    "/Users/sshubin" -> SystemSpecific(
      commandPrefix = Seq(),
      githubDirectory = Paths.get("/Users/sshubin/github/sean"),
      directoryListingCommand = Seq("ls", "-1"),
      mavenSettings = Some("/Users/sshubin/.m2/sean-settings.xml")),
    "/Users/seanshubin" -> SystemSpecific(
      commandPrefix = Seq(),
      githubDirectory = Paths.get("/Users/seanshubin/github/sean"),
      directoryListingCommand = Seq("ls", "-1"),
      mavenSettings = None),
    "C:\\Users\\Sean" -> SystemSpecific(
      commandPrefix = Seq("cmd", "/C"),
      githubDirectory = Paths.get("d:\\keep\\github\\sean"),
      directoryListingCommand = Seq("dir", "/b"),
      mavenSettings = None
    )
  )
  val createRunner: SystemSpecific => Runnable = theSystemSpecific =>
    new DependencyInjection {
      override def systemSpecific: SystemSpecific = theSystemSpecific
    }.runner

  val runner: Runnable = new ConfigurationRunner(system, map)(createRunner)
}
