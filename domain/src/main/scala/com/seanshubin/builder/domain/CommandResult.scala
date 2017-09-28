package com.seanshubin.builder.domain

import com.seanshubin.devon.domain.DevonMarshallerWiring

case class CommandResult(command: String, project: String, processOutput: ProcessOutput, previousAttemptCount: Int) {
  def isSuccess: Boolean = {
    DevonMarshallerWiring.Default.valueToPretty(this).foreach(println)
    ???
  }

  def shouldRetry: Boolean = false
}
