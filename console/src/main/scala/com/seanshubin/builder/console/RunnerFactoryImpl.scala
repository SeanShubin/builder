package com.seanshubin.builder.console

import com.seanshubin.builder.core.{Configuration, Runner, RunnerFactory}

class RunnerFactoryImpl extends RunnerFactory {
  override def createRunner(theConfiguration: Configuration): Runner = {
    new RunnerWiring {
      override def configuration: Configuration = theConfiguration
    }.runner
  }
}
