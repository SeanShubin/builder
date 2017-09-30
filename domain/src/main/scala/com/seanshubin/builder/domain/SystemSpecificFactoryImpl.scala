package com.seanshubin.builder.domain

class SystemSpecificFactoryImpl(system: SystemContract, map: Map[String, SystemSpecific]) extends SystemSpecificFactory {
  def systemSpecificSettings(): SystemSpecific = {
    map(system.getProperty("user.home"))
  }
}
