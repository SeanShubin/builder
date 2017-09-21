package com.seanshubin.builder.domain

import com.seanshubin.builder.domain.State.HasLocalAndGithub

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class Runner(dispatcher:Dispatcher,
             done: Future[State],
             duration: Duration,
             cleaner: Cleaner) extends Runnable {
  override def run(): Unit = {
    dispatcher.findLocalProjects()
    dispatcher.findRemoteProjects()
    val finalState: State = Await.result(done, duration)
    cleaner.cleanup()
    finalState match {
      case HasLocalAndGithub(local, github) =>
        val inLocalNotGithub: Seq[String] = (local.toSet -- github.toSet).toSeq.sorted
        val inGithubNotLocal: Seq[String] = (github.toSet -- local.toSet).toSeq.sorted
        val inBoth: Seq[String] = (local.toSet & github.toSet).toSeq.sorted
        val lines = Seq("in both") ++
          inBoth.map(indent) ++
          Seq("in local not github") ++
          inLocalNotGithub.map(indent) ++
          Seq("in github not local") ++
          inGithubNotLocal.map(indent)
        lines.foreach(println)
      case x => println(x)
    }
  }

  private def indent(s: String): String = s"  $s"
}
