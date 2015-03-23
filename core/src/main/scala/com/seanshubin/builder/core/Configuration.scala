package com.seanshubin.builder.core

import java.nio.file.Path

case class Configuration(githubUserName: String, reportDirectory: Path, environmentByOsName: Map[String, Environment], projects: Seq[ProjectConfig])
