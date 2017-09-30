package com.seanshubin.builder.domain

trait SystemSpecificFactory {
  def systemSpecificSettings(): SystemSpecific
}
