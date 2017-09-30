package com.seanshubin.builder.domain

class ConfigurationRunner(system: SystemContract, map: Map[String, SystemSpecific])(createRunner: SystemSpecific => Runnable) extends Runnable {
  override def run(): Unit = {
    val userHome = system.getProperty("user.home")
    val systemSpecific = map(userHome)
    val runner = createRunner(systemSpecific)
    runner.run()
  }
}
