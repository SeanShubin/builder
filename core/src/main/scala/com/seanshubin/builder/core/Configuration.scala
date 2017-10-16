package com.seanshubin.builder.core

import java.nio.file.Path

import com.seanshubin.uptodate.logic.{Configuration => UpToDateConfiguration}

case class Configuration(githubUserName: String,
                         reportDirectory: Path,
                         settingsByUserHome: Map[String, Settings],
                         projects: Seq[ProjectConfig],
                         upToDateConfiguration: UpToDateConfiguration)
