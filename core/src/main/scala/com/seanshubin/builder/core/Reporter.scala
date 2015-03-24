package com.seanshubin.builder.core

trait Reporter {
  def storeAllReports(reports: Seq[Report])

  def storeUpgradeResults(upgradeResults: Seq[ExecutionResult])
}
