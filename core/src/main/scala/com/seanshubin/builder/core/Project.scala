package com.seanshubin.builder.core

case class Project(name: String,
                   isConfigured: Boolean,
                   isIgnored: Boolean,
                   isInGithub: Boolean,
                   isLocal: Boolean,
                   hasPendingEdits: Boolean) {
  def isOkToBuild: Boolean = isIgnored || (isConfigured && isInGithub && isLocal && !hasPendingEdits)
}
