package com.seanshubin.builder.domain

object EntryPoint extends App {
  new SystemSpecificDependencyInjection {}.runner.run()
}
