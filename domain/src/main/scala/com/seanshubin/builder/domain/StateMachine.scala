package com.seanshubin.builder.domain

import akka.typed.{ActorContext, Behavior, Signal}

class StateMachine(dispatcher: Dispatcher,
                   notifySignal: Signal => Unit,
                   notifyEvent: Event => Unit) extends Behavior[Event] {
  private var state: State = State.Initial

  override def management(ctx: ActorContext[Event], msg: Signal): Behavior[Event] = {
    notifySignal(msg)
    this
  }

  override def message(ctx: ActorContext[Event], msg: Event): Behavior[Event] = {
    notifyEvent(msg)
    state = state.handle(msg, dispatcher, ctx.getSelf())
    this
  }
}
