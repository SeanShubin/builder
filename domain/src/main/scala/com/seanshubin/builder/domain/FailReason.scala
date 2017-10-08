package com.seanshubin.builder.domain

sealed trait FailReason

object FailReason {

  case class ExitCode(exitCode: Int) extends FailReason

  case class ExceptionThrown(exception: Throwable) extends FailReason

}
