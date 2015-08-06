package com.seanshubin.builder.core

case class ExceptionValue(maybeMessage: Option[String],
                          stackTrace: Seq[StackTraceValue],
                          maybeCause: Option[ExceptionValue])

object ExceptionValue {
  def fromException(exception: Throwable): ExceptionValue = {
    val maybeMessage = Option(exception.getMessage)
    val stackTrace: Seq[StackTraceValue] =
      if (exception.getStackTrace == null) Seq()
      else exception.getStackTrace.map(StackTraceValue.fromStackTraceElement)
    val maybeCause = Option(exception.getCause).map(ExceptionValue.fromException)
    new ExceptionValue(maybeMessage, stackTrace, maybeCause)
  }
}
