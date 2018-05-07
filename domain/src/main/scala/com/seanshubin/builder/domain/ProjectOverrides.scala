package com.seanshubin.builder.domain

object ProjectOverrides {
  private val projectsNotToBuild = Set(
    "learn-java-module",
    "parser",
    "kotlin-tryme"
  )

  private val projectsNotToUpgrade = Set("learn-spark")

  def shouldBuild(projectName: String): Boolean = {
    !projectsNotToBuild.contains(projectName)
  }

  def shouldUpgrade(projectName: String): Boolean = {
    !projectsNotToUpgrade.contains(projectName)
  }
}
