package com.seanshubin.builder.domain

trait Builder {
  def build(name: String): Unit
}
