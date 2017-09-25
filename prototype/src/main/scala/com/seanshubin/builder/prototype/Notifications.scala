package com.seanshubin.builder.prototype

import akka.typed.Signal

trait Notifications {
  def event(event: Event): Unit

  def signal(signal: Signal): Unit

  def asyncException(ex: Throwable): Unit
}
