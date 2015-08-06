package com.seanshubin.builder.core

case class StackTraceValue(declaringClass: String, methodName: String, fileName: String, lineNumber: Int)

object StackTraceValue {
  def fromStackTraceElement(stackTraceElement: StackTraceElement): StackTraceValue = {
    StackTraceValue(
      declaringClass = stackTraceElement.getClassName,
      methodName = stackTraceElement.getMethodName,
      fileName = stackTraceElement.getFileName,
      lineNumber = stackTraceElement.getLineNumber)
  }
}
