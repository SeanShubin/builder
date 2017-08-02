package com.seanshubin.builder.domain

class BuildEventHandler(ignoreSorter: IgnoreSorter,
                        ignoredHandler: IgnoredHandler,
                        checkout: Checkout,
                        checkoutSorter: CheckoutSorter,
                        checkoutReporter: CheckoutReporter) {
  def handle(buildEvent: BuildEvent): Unit = {
    buildEvent match {
      case BuildEvent.ProjectDiscovered(sshUrl) => ignoreSorter.accept(sshUrl)
      case BuildEvent.Ignored(sshUrl) => ignoredHandler.accept(sshUrl)
      case BuildEvent.NotIgnored(sshUrl) => checkout.accept(sshUrl, previousAttempts = 0)
      case BuildEvent.CheckoutFinished(checkoutResult) => checkoutSorter.accept(checkoutResult)
      case BuildEvent.CheckoutSuccess(checkoutResult) => checkoutReporter.accept(checkoutResult)
      case BuildEvent.CheckoutFailure(checkoutResult) => checkout.retry(checkoutResult)
      case BuildEvent.CheckoutGiveUp(checkoutResult) => checkoutReporter.giveUp(checkoutResult)
    }
  }
}
