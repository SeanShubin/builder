package com.seanshubin.builder.domain

import java.nio.file.{Files, Path, Paths}
import java.util.stream.Collectors

import com.seanshubin.devon.domain.DevonMarshallerWiring

import scala.collection.JavaConverters._
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

object PrototypeApp extends App {
  println(DevonMarshallerWiring.Default.valueToCompact((1, 2, 3)))
  implicit val implicitExecutionContext = ExecutionContext.global

  val homeMac = Paths.get("/Users/seanshubin")
  val workMac = Paths.get("/Users/sshubin")
  val homePc = Paths.get("G:\\keep")
  val homeDirectories = Seq(homeMac, workMac, homePc)
  val homeDirectory = homeDirectories.find(Files.exists(_)).get
  val githubDirectory = homeDirectory.resolve("github").resolve("sean")
  val commands: Seq[Seq[String]] = Seq(
    Seq("git", "fetch"),
    Seq("git", "rebase"))

  val dirs = Files.list(githubDirectory).collect(Collectors.toList[Path]).asScala.filter(Files.isDirectory(_))

  val resultsSeqFuture: Future[Seq[ProcessResult]] = Future.sequence(dirs.map(runSequence)).map(_.flatten)
  val processResults = Await.result(resultsSeqFuture, Duration.Inf)
  processResults.filterNot(_.isSuccess).flatMap(_.toMultipleLineString).foreach(println)

  def runSequence(dir: Path): Future[Seq[ProcessResult]] = {
    runSequence(dir, Seq(), commands)
  }

  def runSequence(dir: Path, soFar: Seq[ProcessResult], remain: Seq[Seq[String]]): Future[Seq[ProcessResult]] = {
    if (soFar.isEmpty || soFar.last.isSuccess) {
      if (remain.isEmpty) {
        Future.successful(soFar)
      } else {
        for {
          nextProcessResult <- ProcessRunner.run(remain.head, dir)
          newSoFar <- runSequence(dir, soFar :+ nextProcessResult, remain.tail)
        } yield {
          newSoFar
        }
      }
    } else {
      Future.successful(soFar)
    }
  }
}
