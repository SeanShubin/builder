package com.seanshubin.builder.domain

sealed trait ProjectState {
  def isFinished: Boolean = false

  def name: String = getClass.getSimpleName
}

object ProjectState {

  trait OneOfTheFinishedStates extends ProjectState {
    override def isFinished: Boolean = true
  }

  case object InGithubNotLocal extends ProjectState

  case object InLocalNotGithub extends ProjectState

  case object InLocalAndGithub extends ProjectState

  case object Cloning extends ProjectState

  case object Upgrading extends ProjectState

  case object Building extends ProjectState

  case object BuildSuccess extends OneOfTheFinishedStates

  case object FailedToClone extends OneOfTheFinishedStates

  case object FailedToBuild extends OneOfTheFinishedStates

}