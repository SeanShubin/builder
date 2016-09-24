package com.seanshubin.builder.domain

import java.nio.file.{Files, Path, Paths}
import java.util.stream.Collectors

import scala.collection.JavaConverters._
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

object PrototypeApp extends App {
  implicit val implicitExecutionContext = ExecutionContext.global

  val homeMac = Paths.get("/Users/seanshubin")
  val workMac = Paths.get("/Users/sshubin")
  val homePc = Paths.get("C:\\Users\\Sean")
  val homeDirectories = Seq(homeMac, workMac, homePc)
  val homeDirectory = homeDirectories.find(Files.exists(_)).get
  val githubDirectory = homeDirectory.resolve("github").resolve("sean")

  val dirs = Files.list(githubDirectory).collect(Collectors.toList[Path]).asScala.filter(Files.isDirectory(_))
  val resultsSeqFuture: Future[Seq[ProcessResult]] = Future.sequence(dirs.map(gitFetchRebase)).map(_.flatten)
  val processResults = Await.result(resultsSeqFuture, Duration.Inf)
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
