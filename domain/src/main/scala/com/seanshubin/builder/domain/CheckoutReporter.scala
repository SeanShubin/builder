package com.seanshubin.builder.domain

class CheckoutReporter {
  def accept(checkoutResult: CheckoutResult): Unit = {
    println(s"successfully checked out '${checkoutResult.sshUrl}'")
  }

  def giveUp(checkoutResult: CheckoutResult): Unit = {
    println(s"gave up trying to check out '${checkoutResult.sshUrl}'")
  }
}
