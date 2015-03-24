package com.seanshubin.builder.core

trait Timer {
  def elapsedTimeFor[T](f: => T): Long
}
