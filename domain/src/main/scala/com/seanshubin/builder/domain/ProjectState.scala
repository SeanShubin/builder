package com.seanshubin.builder.domain

sealed trait ProjectState

object ProjectState {

  case object InGithubNotLocal extends ProjectState

  case object InLocalNotGithub extends ProjectState

  case object InLocalAndGithub extends ProjectState

  case object Cloning extends ProjectState

  case object Upgrading extends ProjectState

  case object Building extends ProjectState

  case object Finished extends ProjectState

}