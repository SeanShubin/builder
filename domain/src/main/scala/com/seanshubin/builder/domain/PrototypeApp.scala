package com.seanshubin.builder.domain

import java.nio.file.{Files, Path, Paths}
import java.util.stream.Collectors

import scala.collection.JavaConverters._
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

object PrototypeApp extends App {
  implicit val implicitExecutionContext = ExecutionContext.global

  val base = Paths.get("/Users/sshubin/github/sean")

  val dirs = Files.list(base).collect(Collectors.toList[Path]).asScala.filter(Files.isDirectory(_))
  val dirsF: Seq[Future[Seq[ProcessResult]]] = dirs.map(gitFetchRebase)

  val blah: Future[Seq[ProcessResult]] = Future.sequence(dirsF).map(_.flatten)
  val processResults = Await.result(blah, Duration.Inf)
  processResults.filter(_.isError).flatMap(_.toMultipleLineString).foreach(println)

  def gitFetchRebase(dir: Path): Future[Seq[ProcessResult]] = {
    val futureSeq: Future[Seq[ProcessResult]] = for {
      a <- ProcessRunner.run(Seq("git", "fetch"), dir)
      b <- ProcessRunner.run(Seq("git", "rebase"), dir)
    } yield {
      Seq(a, b)
    }
    futureSeq
  }
}
