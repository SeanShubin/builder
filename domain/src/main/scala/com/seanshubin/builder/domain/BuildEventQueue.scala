package com.seanshubin.builder.domain

class BuildEventQueue() {
  private var allEvents = Vector[BuildEvent]()

  def accept(buildEvent: BuildEvent): Unit = {
    allEvents = allEvents :+ buildEvent
  }

  def nonEmpty: Boolean = allEvents.nonEmpty

  def pull(): BuildEvent = {
    val nextEvent = allEvents.head
    allEvents = allEvents.tail
    nextEvent
  }
}
