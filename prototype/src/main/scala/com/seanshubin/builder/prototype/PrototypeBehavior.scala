package com.seanshubin.builder.prototype

import akka.typed.{ActorContext, ExtensibleBehavior, Signal}

import scala.concurrent.ExecutionContext

class PrototypeBehavior(dispatcher: Dispatcher,
                        notifySignal: Signal => Unit,
                        notifyEvent: Event => Unit)(implicit executionContext: ExecutionContext) extends ExtensibleBehavior[Event] {
  private var state: State = State.Initial

  override def receiveSignal(ctx: ActorContext[Event], msg: Signal): PrototypeBehavior = {
    notifySignal(msg)
    this
  }

  override def receiveMessage(ctx: ActorContext[Event], msg: Event): PrototypeBehavior = {
    notifyEvent(msg)
    val (newState, effects) = state.handle(msg)
    state = newState
    effects.foreach(effect => effect.apply(dispatcher, ctx.asScala.self))
    this
  }
}
