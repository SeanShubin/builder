package com.seanshubin.builder.domain

import akka.typed.{ActorContext, Behavior, Signal}
import com.seanshubin.builder.domain.Event._

import scala.concurrent.Promise

class StateMachine(done: Promise[State]) extends Behavior[Event] {
  private var state: State = State.Initial

  override def management(ctx: ActorContext[Event], msg: Signal): Behavior[Event] = {
    this
  }

  override def message(ctx: ActorContext[Event], msg: Event): Behavior[Event] = {
    msg match {
      case Initialize => state = State.Initial
      case ProjectsFoundInGithub(names) => state = state.setGithub(names)
      case ProjectsFoundLocally(names) => state = state.setLocal(names)
      case ErrorFindingProjectsInGithub(ex) => state = state.setError(ex)
      case ErrorFindingProjectsLocally(ex) => state = state.setError(ex)
      case Cloned(name) => state = state.cloned(name)
      case ErrorCloning(name, exception) => state = state.setProjectError(name, exception)
      case Built(name) => state = state.built(name)
      case ErrorBuilding(name, exception) => state = state.setProjectError(name, exception)
    }
    if (state.isFinal) {
      done.success(state)
    }
    this
  }
}
