package com.seanshubin.builder.prototype

sealed trait Event

object Event {

  case object Ready extends Event

  case class Found(names: Seq[String]) extends Event

  case class Processed(name: String, result: Int) extends Event

}
