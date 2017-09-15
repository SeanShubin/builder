package com.seanshubin.builder.domain

trait SystemSpecific {
  def composeDirectoryListingCommand(): ProcessInput
}
