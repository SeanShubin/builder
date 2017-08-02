package com.seanshubin.builder.domain

class CheckoutSorter(checkoutSuccess: CheckoutResult => Unit,
                     checkoutFailure: CheckoutResult => Unit,
                     checkoutGiveUp: CheckoutResult => Unit,
                     attemptLimit: Int) {
  def accept(checkoutResult: CheckoutResult): Unit = {
    if (checkoutResult.processOutput.exitCode == 0) {
      checkoutSuccess(checkoutResult)
    } else {
      if (checkoutResult.previousAttempts >= attemptLimit) {
        checkoutGiveUp(checkoutResult)
      } else {
        checkoutFailure(checkoutResult)
      }
    }
  }
}
