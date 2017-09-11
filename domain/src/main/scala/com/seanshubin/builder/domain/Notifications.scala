package com.seanshubin.builder.domain

trait Notifications {
  def projectFoundInGithub(name: String): Unit
}
