package com.seanshubin.builder.core

import java.nio.file.Path

import com.seanshubin.up_to_date.logic.{Configuration => UpToDateConfiguration}

case class Configuration(githubUserName: String,
                         reportDirectory: Path,
                         settingsByUserHome: Map[String, Settings],
                         projects: Seq[ProjectConfig],
                         upToDateConfiguration: UpToDateConfiguration)
