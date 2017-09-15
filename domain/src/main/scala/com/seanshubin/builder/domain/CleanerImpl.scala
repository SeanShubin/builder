package com.seanshubin.builder.domain

import akka.typed.ActorSystem

class CleanerImpl(actorSystem: ActorSystem[Event]) extends Cleaner {
  override def cleanup(): Unit = {
    actorSystem.terminate()
  }
}
