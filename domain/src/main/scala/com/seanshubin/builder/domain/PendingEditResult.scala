package com.seanshubin.builder.domain

sealed trait PendingEditResult {
  def project: String
}

object PendingEditResult {

  case class NoPendingEdits(project: String) extends PendingEditResult

  case class HasPendingEdits(project: String) extends PendingEditResult

  case class UnableToDeterminePendingEdits(project: String) extends PendingEditResult

}
