package com.seanshubin.builder.domain

sealed trait State {
  def isFinal: Boolean = false

  def setLocal(names: Seq[String]): State = ???

  def setGithub(names: Seq[String]): State = ???

  def setError(ex: Throwable): State = ???

  def cloned(name: String): State = ???

  def built(name: String): State = ???

  def setProjectError(name: String, ex: Throwable): State = ???
}

object State {

  trait FinalState extends State {
    override def isFinal: Boolean = true
  }

  case object Initial extends State {
    override def setLocal(names: Seq[String]): State = HasLocal(names)

    override def setGithub(names: Seq[String]): State = HasGithub(names)

    override def setError(ex: Throwable): State = Error(ex)
  }

  case class HasLocal(local: Seq[String]) extends State {
    override def setGithub(names: Seq[String]): State = HasLocalAndGithub(local, names)

    override def setError(ex: Throwable): State = Error(ex)
  }

  case class HasGithub(github: Seq[String]) extends State {
    override def setLocal(names: Seq[String]): State = HasLocalAndGithub(names, github)

    override def setError(ex: Throwable): State = Error(ex)
  }

  case class HasLocalAndGithub(local: Seq[String], github: Seq[String]) extends FinalState

  case class Processing(projects: Map[String, ProjectState]) extends State

  case class Error(ex: Throwable) extends FinalState

}
