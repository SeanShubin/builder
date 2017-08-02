package com.seanshubin.builder.domain

class IgnoreSorter(ignore: String => Unit, doNotIgnore: String => Unit) {
  def accept(sshUrl: String): Unit = {
    if (sshUrl == "ignore me") {
      ignore(sshUrl)
    } else {
      doNotIgnore(sshUrl)
    }
  }
}
