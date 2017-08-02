package com.seanshubin.builder.domain

import java.time.Clock

class Checkout(clock: Clock, checkoutProcessFinished: CheckoutResult => Unit) {
  def accept(sshUrl: String, previousAttempts: Int): Unit = {
    if (sshUrl == "succeed first try") {
      checkoutProcessFinished(CheckoutResult(sshUrl, previousAttempts + 1, createSuccess()))
    } else if (sshUrl == "succeed second try") {
      if (previousAttempts == 0) {
        checkoutProcessFinished(CheckoutResult(sshUrl, previousAttempts + 1, createFailure()))
      } else {
        checkoutProcessFinished(CheckoutResult(sshUrl, previousAttempts + 1, createSuccess()))
      }
    } else if (sshUrl == "always fail") {
      checkoutProcessFinished(CheckoutResult(sshUrl, previousAttempts + 1, createFailure()))
    } else {
      throw new RuntimeException(s"unexpected project '$sshUrl'")
    }
  }

  def retry(previousResult: CheckoutResult): Unit = {
    accept(previousResult.sshUrl, previousResult.previousAttempts + 1)
  }

  private def createSuccess(): ProcessOutput = {
    createWithExitCode(0)
  }

  private def createFailure(): ProcessOutput = {
    createWithExitCode(128)
  }

  private def createWithExitCode(exitCode: Int): ProcessOutput = {
    val outputLines = Seq()
    val errorLines = Seq()
    val started = clock.instant()
    val ended = clock.instant()
    val processOutput = ProcessOutput(exitCode, outputLines, errorLines, started, ended)
    processOutput
  }
}
