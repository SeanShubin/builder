package com.seanshubin.builder.prototype

import com.seanshubin.builder.prototype.Event.{Found, Processed, Ready}

sealed trait State {
  def update(name: String, status: Int): State = ???

  def results: Map[String, Int] = ???

  def handle(event: Event): (State, Seq[Effect]) = {
    val stateName: String = getClass.getSimpleName
    throw new UnsupportedOperationException(s"State $stateName does not support event $event")
  }
}

object State {

  case object Initial extends State {
    override def handle(event: Event): (State, Seq[Effect]) = {
      event match {
        case Ready => (State.Searching, Seq(Effect.Search))
        case _ => super.handle(event)
      }
    }
  }

  case object Searching extends State {
    override def handle(event: Event): (State, Seq[Effect]) = {
      event match {
        case Found(names) =>
          val effects = names.map(Effect.Process)
          val statusOfProjects = StatusOfProjects.fromNames(names)
          val state = State.Processing(statusOfProjects)
          (state, effects)
        case _ => super.handle(event)
      }
    }
  }

  case class Processing(status: StatusOfProjects) extends State {
    override def handle(event: Event): (State, Seq[Effect]) = {
      event match {
        case Processed(name, code) =>
          val newStatus = status.completeProject(name, code)
          val effects = if (newStatus.isDone) {
            Seq(Effect.Done)
          } else {
            Seq()
          }
          (Processing(newStatus), effects)
        case _ => super.handle(event)
      }
    }
  }

  case object Error extends State

}
