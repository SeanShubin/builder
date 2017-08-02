package com.seanshubin.builder.domain

class IgnoredHandler {
  def accept(sshUrl: String): Unit = {
    println(s"ignored: $sshUrl")
  }
}
