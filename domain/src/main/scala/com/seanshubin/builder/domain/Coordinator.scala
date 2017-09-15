package com.seanshubin.builder.domain

import akka.typed.{ActorContext, Behavior, Signal}
import com.seanshubin.builder.domain.Event.{ErrorFindingProjectsInGithub, ErrorFindingProjectsLocally, ProjectsFoundInGithub, ProjectsFoundLocally}

import scala.concurrent.Promise

class Coordinator(done: Promise[State]) extends Behavior[Event] {
  private var state: State = State.Initial

  override def management(ctx: ActorContext[Event], msg: Signal): Behavior[Event] = {
    this
  }

  override def message(ctx: ActorContext[Event], msg: Event): Behavior[Event] = {
    msg match {
      case ProjectsFoundInGithub(names: Seq[String]) => state = state.setGithub(names)
      case ProjectsFoundLocally(names: Seq[String]) => state = state.setLocal(names)
      case ErrorFindingProjectsInGithub(ex: Throwable) => state = state.setError(ex)
      case ErrorFindingProjectsLocally(ex: Throwable) => state = state.setError(ex)
    }
    if (state.isFinal) {
      done.success(state)
    }
    this
  }
}
