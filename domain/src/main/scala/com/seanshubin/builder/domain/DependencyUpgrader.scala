package com.seanshubin.builder.domain

import com.seanshubin.uptodate.logic.SummaryReport

trait DependencyUpgrader {
  def upgradeDependencies(projectName: String): SummaryReport
}
