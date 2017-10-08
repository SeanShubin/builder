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

  case class Clone(projectName: String) extends Effect {
    override def applyEffect(dispatcher: Dispatcher, actorRef: ActorRef[Event])(implicit executionContext: ExecutionContext): Unit = {
      val handler = new DispatchResultHandler(actorRef)
      dispatcher.cloneProject(projectName).onComplete(handler.finishedCloning(projectName))
    }
  }

  case class Build(projectName: String) extends Effect {
    override def applyEffect(dispatcher: Dispatcher, actorRef: ActorRef[Event])(implicit executionContext: ExecutionContext): Unit = {
      val handler = new DispatchResultHandler(actorRef)
      dispatcher.buildProject(projectName).onComplete(handler.finishedBuilding(projectName))
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

  case class CheckForPendingEdits(projectName: String) extends Effect {
    override def applyEffect(dispatcher: Dispatcher, actorRef: ActorRef[Event])(implicit executionContext: ExecutionContext): Unit = {
      val handler = new DispatchResultHandler(actorRef)
      dispatcher.checkForPendingEdits(projectName).onComplete(handler.finishedCheckingForPendingEdits(projectName))
    }
  }

}
