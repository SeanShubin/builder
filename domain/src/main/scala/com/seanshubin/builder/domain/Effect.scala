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
      dispatcher.clone(projectName).onComplete(handler.finishedClone(projectName))
    }
  }

  case class Build(projectName: String) extends Effect {
    override def applyEffect(dispatcher: Dispatcher, actorRef: ActorRef[Event])(implicit executionContext: ExecutionContext): Unit = {
      val handler = new DispatchResultHandler(actorRef)
      dispatcher.build(projectName).onComplete(handler.finishedBuild(projectName))
    }
  }

  case class Fetch(projectName: String) extends Effect {
    override def applyEffect(dispatcher: Dispatcher, actorRef: ActorRef[Event])(implicit executionContext: ExecutionContext): Unit = {
      val handler = new DispatchResultHandler(actorRef)
      dispatcher.fetch(projectName).onComplete(handler.finishedFetch(projectName))
    }
  }

  case class Merge(projectName: String) extends Effect {
    override def applyEffect(dispatcher: Dispatcher, actorRef: ActorRef[Event])(implicit executionContext: ExecutionContext): Unit = {
      val handler = new DispatchResultHandler(actorRef)
      dispatcher.merge(projectName).onComplete(handler.finishedMerge(projectName))
    }
  }

  case class Push(projectName: String) extends Effect {
    override def applyEffect(dispatcher: Dispatcher, actorRef: ActorRef[Event])(implicit executionContext: ExecutionContext): Unit = {
      val handler = new DispatchResultHandler(actorRef)
      dispatcher.push(projectName).onComplete(handler.finishedPush(projectName))
    }
  }

  case class UpgradeDependencies(projectName: String) extends Effect {
    override def applyEffect(dispatcher: Dispatcher, actorRef: ActorRef[Event])(implicit executionContext: ExecutionContext): Unit = {
      val handler = new DispatchResultHandler(actorRef)
      if(ProjectOverrides.shouldUpgrade(projectName)){
        dispatcher.upgradeDependencies(projectName).onComplete(handler.finishedUpgradingDependencies(projectName))
      } else {
        handler.skipUpgradingDependencies(projectName)
      }
    }
  }

  case class AddAfterUpdate(projectName: String) extends Effect {
    override def applyEffect(dispatcher: Dispatcher, actorRef: ActorRef[Event])(implicit executionContext: ExecutionContext): Unit = {
      val handler = new DispatchResultHandler(actorRef)
      dispatcher.add(projectName).onComplete(handler.finishedAdd(projectName))
    }
  }

  case class CommitAfterUpdate(projectName: String) extends Effect {
    override def applyEffect(dispatcher: Dispatcher, actorRef: ActorRef[Event])(implicit executionContext: ExecutionContext): Unit = {
      val handler = new DispatchResultHandler(actorRef)
      dispatcher.commit(projectName).onComplete(handler.finishedCommit(projectName))
    }
  }

  case class PushAfterUpdate(projectName: String) extends Effect {
    override def applyEffect(dispatcher: Dispatcher, actorRef: ActorRef[Event])(implicit executionContext: ExecutionContext): Unit = {
      val handler = new DispatchResultHandler(actorRef)
      dispatcher.push(projectName).onComplete(handler.finishedPushAfterUpdate(projectName))
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

  case class LogFailure(action: String, projectName: String, failReason: FailReason) extends Effect {
    override def applyEffect(dispatcher: Dispatcher, actorRef: ActorRef[Event])(implicit executionContext: ExecutionContext): Unit = {
      dispatcher.logFailure(action, projectName, failReason)
    }
  }

}
