package com.seanshubin.builder.domain

object EntryPoint extends App {
  new DependencyInjection {}.runner.run()
}
