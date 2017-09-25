package com.seanshubin.builder.prototype

import akka.typed.{ActorContext, Behavior, Signal}

import scala.concurrent.ExecutionContext

class PrototypeBehavior(dispatcher: Dispatcher,
                        notifySignal: Signal => Unit,
                        notifyEvent: Event => Unit)(implicit executionContext: ExecutionContext) extends Behavior[Event] {
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
