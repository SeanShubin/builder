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

  case object InLocalNotGithub extends OneOfTheFinishedStates

  case object InLocalAndGithub extends ProjectState

  case object Fetching extends ProjectState

  case object Merging extends ProjectState

  case object PushingAfterMerge extends ProjectState

  case object Upgrading extends ProjectState

  case object Adding extends ProjectState

  case object HasPendingEdits extends OneOfTheFinishedStates

  case object UpgradingAfterClone extends ProjectState

  case object BuildSuccess extends OneOfTheFinishedStates

  case object UpgradeAndBuildSuccess extends OneOfTheFinishedStates

  case object FailedToClone extends OneOfTheFinishedStates

  case object FailedToBuild extends OneOfTheFinishedStates

  case object FailedToUpdateDependencies extends OneOfTheFinishedStates

  case object FailedToMerge extends OneOfTheFinishedStates

  case object Committing extends ProjectState

  case object PushingAfterUpgrade extends ProjectState

  case object BuildingAfterUpgrade extends ProjectState

  case object Building extends ProjectState

  case object Ignored extends OneOfTheFinishedStates

  case object FailedToGetPendingEdits extends OneOfTheFinishedStates

  case object FailedToPush extends OneOfTheFinishedStates
}
