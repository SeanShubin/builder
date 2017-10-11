package com.seanshubin.builder.domain

sealed trait ProjectState {
  def isFinished: Boolean = false

  def name: String = ClassUtil.getSimpleClassName(this)
}

object ProjectState {

  trait OneOfTheFinishedStates extends ProjectState {
    override def isFinished: Boolean = true
  }

  case object InGithubNotLocal extends ProjectState

  case object InLocalNotGithub extends ProjectState

  case object InLocalAndGithub extends ProjectState

  case object NoPendingEdits extends ProjectState

  case object Fetched extends ProjectState

  case object Merged extends ProjectState

  case object Pushed extends ProjectState

  case object HasPendingEdits extends OneOfTheFinishedStates

  case object CloneSuccess extends ProjectState

  case object BuildSuccess extends OneOfTheFinishedStates

  case object FailedToClone extends OneOfTheFinishedStates

  case object FailedToBuild extends OneOfTheFinishedStates

}