package com.seanshubin.builder.core

trait RunnerFactory {
  def createRunner(configuration: Configuration): Runner
}
