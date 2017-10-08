package com.seanshubin.builder.domain

import akka.typed.ActorRef

import scala.concurrent.ExecutionContext

sealed trait Effect {
  def applyEffect(dispatcher: Dispatcher, actorRef: ActorRef[Event])(implicit executionContext: ExecutionContext): Unit
}

object Effect {

  case object FindRemoteProjects extends Effect {
    override def applyEffect(dispatcher: Dispatcher, actorRef: ActorRef[Event])(implicit executionContext: ExecutionContext): Unit = {
      val handler = new DispatchResultHandler(actorRef)
      dispatcher.findRemoteProjects().onComplete(handler.foundRemoteProjects)
    }
  }

  case object FindLocalProjects extends Effect {
    override def applyEffect(dispatcher: Dispatcher, actorRef: ActorRef[Event])(implicit executionContext: ExecutionContext): Unit = {
      val handler = new DispatchResultHandler(actorRef)
      dispatcher.findLocalProjects().onComplete(handler.foundLocalProjects)
    }
  }

  case class UnsupportedEventFromState(eventName: String, stateName: String) extends Effect {
    override def applyEffect(dispatcher: Dispatcher, actorRef: ActorRef[Event])(implicit executionContext: ExecutionContext): Unit = {
      dispatcher.unsupportedEventFromState(eventName, stateName)
    }
  }

  case object Done extends Effect {
    override def applyEffect(dispatcher: Dispatcher, actorRef: ActorRef[Event])(implicit executionContext: ExecutionContext): Unit = {
      dispatcher.done()
    }
  }

  case class Clone(project: String) extends Effect {
    override def applyEffect(dispatcher: Dispatcher, actorRef: ActorRef[Event])(implicit executionContext: ExecutionContext): Unit = {
      val handler = new DispatchResultHandler(actorRef)
      dispatcher.cloneProject(project).onComplete(handler.finishedCloning)
    }
  }

  case class Build(project: String) extends Effect {
    override def applyEffect(dispatcher: Dispatcher, actorRef: ActorRef[Event])(implicit executionContext: ExecutionContext): Unit = {
      val handler = new DispatchResultHandler(actorRef)
      dispatcher.buildProject(project).onComplete(handler.finishedBuilding)
    }
  }

  case class MissingFromGithub(project: String) extends Effect {
    override def applyEffect(dispatcher: Dispatcher, actorRef: ActorRef[Event])(implicit executionContext: ExecutionContext): Unit = {
      val handler = new DispatchResultHandler(actorRef)
      handler.missingFromGithub(project)
    }
  }

  case class UnableToProcessProjectInThisState(project: String, state: ProjectState) extends Effect {
    override def applyEffect(dispatcher: Dispatcher, actorRef: ActorRef[Event])(implicit executionContext: ExecutionContext): Unit = {
      val handler = new DispatchResultHandler(actorRef)
      handler.unableToProcessProjectInThisState(project, state)
    }
  }

  case class StatusUpdate(statusOfProjects: StatusOfProjects) extends Effect {
    override def applyEffect(dispatcher: Dispatcher, actorRef: ActorRef[Event])(implicit executionContext: ExecutionContext): Unit = {
      dispatcher.statusUpdate(statusOfProjects)
    }
  }

  case class Summary(statusOfProjects: StatusOfProjects) extends Effect {
    override def applyEffect(dispatcher: Dispatcher, actorRef: ActorRef[Event])(implicit executionContext: ExecutionContext): Unit = {
      dispatcher.statusSummary(statusOfProjects)
    }
  }

}
