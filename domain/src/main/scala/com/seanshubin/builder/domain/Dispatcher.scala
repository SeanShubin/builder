package com.seanshubin.builder.domain

class Dispatcher(buildEventQueue: BuildEventQueue) {
  def projectFound(sshUrl: String): Unit = {
    buildEventQueue.accept(BuildEvent.ProjectDiscovered(sshUrl))
  }

  def ignore(sshUrl: String): Unit = {
    buildEventQueue.accept(BuildEvent.Ignored(sshUrl))
  }

  def doNotIgnore(sshUrl: String): Unit = {
    buildEventQueue.accept(BuildEvent.NotIgnored(sshUrl))
  }

  def checkoutProcessFinished(checkoutResult: CheckoutResult): Unit = {
    buildEventQueue.accept(BuildEvent.CheckoutFinished(checkoutResult))
  }

  def checkoutSuccess(checkoutResult: CheckoutResult): Unit = {
    buildEventQueue.accept(BuildEvent.CheckoutSuccess(checkoutResult))
  }

  def checkoutFailure(checkoutResult: CheckoutResult): Unit = {
    buildEventQueue.accept(BuildEvent.CheckoutFailure(checkoutResult))
  }

  def checkoutGiveUp(checkoutResult: CheckoutResult): Unit = {
    buildEventQueue.accept(BuildEvent.CheckoutGiveUp(checkoutResult))
  }
}
