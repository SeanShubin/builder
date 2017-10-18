package com.seanshubin.builder.domain

import akka.typed.{ActorContext, ExtensibleBehavior, Signal}

import scala.concurrent.ExecutionContext

class StateMachine(dispatcher: Dispatcher,
                   notifySignal: Signal => Unit,
                   notifyEvent: Event => Unit)
                  (implicit executionContext: ExecutionContext) extends ExtensibleBehavior[Event] {
  private var state: State = State.Initial

  override def receiveSignal(ctx: ActorContext[Event], msg: Signal): StateMachine = {
    notifySignal(msg)
    this
  }

  override def receiveMessage(ctx: ActorContext[Event], msg: Event): StateMachine = {
    notifyEvent(msg)
    val (newState, effects) = state.handle(msg)
    state = newState
    effects.foreach(_.applyEffect(dispatcher, ctx.asScala.self))
    this
  }
}
