package com.seanshubin.builder.domain

import akka.typed.ActorRef
import com.seanshubin.builder.domain.Event.{ProjectsFoundInGithub, ProjectsFoundLocally}

import scala.util.{Success, Try}

class DispatchResultHandler(actorRef: ActorRef[Event]) {
  def foundLocalProjects(result: Try[Seq[String]]): Unit = {
    result match {
      case Success(names) => actorRef ! ProjectsFoundLocally(names)
    }
  }
  def foundRemoteProjects(result: Try[Seq[String]]): Unit = {
    result match {
      case Success(names) => actorRef ! ProjectsFoundInGithub(names)
    }
  }
}
