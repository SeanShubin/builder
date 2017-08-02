package com.seanshubin.builder.domain

sealed trait BuildEvent

object BuildEvent {

  case class ProjectDiscovered(sshUrl: String) extends BuildEvent

  case class Ignored(sshUrl: String) extends BuildEvent

  case class NotIgnored(sshUrl: String) extends BuildEvent

  case class CheckoutFinished(checkoutResult: CheckoutResult) extends BuildEvent

  case class CheckoutGiveUp(checkoutResult: CheckoutResult) extends BuildEvent

  case class CheckoutSuccess(checkoutResult: CheckoutResult) extends BuildEvent

  case class CheckoutFailure(checkoutResult: CheckoutResult) extends BuildEvent

}
