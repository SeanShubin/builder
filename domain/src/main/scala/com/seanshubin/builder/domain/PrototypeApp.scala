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
  val resultsSeqFuture: Future[Seq[ProcessResult]] = Future.sequence(dirs.map(gitFetch)).map(_.flatten)
  val processResults = Await.result(resultsSeqFuture, Duration.Inf)
  processResults.filterNot(_.isSuccess).flatMap(_.toMultipleLineString).foreach(println)

  def gitFetch(dir: Path): Future[Seq[ProcessResult]] = {
    val futureFetch: Future[ProcessResult] = ProcessRunner.run(Seq("git", "fetch"), dir)
    val futureRebase: Future[Seq[ProcessResult]] = for {
      fetchResult <- futureFetch
      rebaseResult <- gitRebase(dir, fetchResult)
    } yield {
      rebaseResult
    }
    futureRebase
  }

  def gitRebase(dir: Path, fetchResult: ProcessResult): Future[Seq[ProcessResult]] = {
    if (fetchResult.isSuccess) {
      val result: Future[Seq[ProcessResult]] = for {
        rebaseResult <- ProcessRunner.run(Seq("git", "rebase"), dir)
      } yield {
        Seq(fetchResult, rebaseResult)
      }
      result
    } else {
      val result: Future[Seq[ProcessResult]] = Future.successful(Seq(fetchResult))
      result
    }
  }
}
