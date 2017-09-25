package com.seanshubin.builder.prototype

import java.io.{PrintWriter, StringWriter}

object ExceptionUtil {
  def toStringWithStackTrace(ex: Throwable): String = {
    val stringWriter = new StringWriter()
    val printWriter = new PrintWriter(stringWriter)
    ex.printStackTrace(printWriter)
    val s = stringWriter.getBuffer.toString
    s
  }
}
