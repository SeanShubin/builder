package com.seanshubin.builder.domain

import java.io.{BufferedReader, InputStreamReader, OutputStreamWriter, PrintWriter}
import java.nio.charset.Charset
import java.nio.file.Path

import scala.concurrent.{ExecutionContext, Future}

class LinesBasedProcess(command: Seq[String],
                        workingDirectory: Path,
                        environment: Map[String, String],
                        standardOutputLine: String => Unit,
                        errorOutputLine: String => Unit,
                        redirectErrorToStandard: Boolean,
                        charset: Charset,
                        executionContext: ExecutionContext) {
  implicit val implicitExecutionContext = executionContext
  val file = workingDirectory.toFile
  val processBuilder = new ProcessBuilder(command: _*).directory(file).redirectErrorStream(redirectErrorToStandard)
  val processEnvironment = processBuilder.environment()
  for {
    (key, value) <- environment
  } {
    processEnvironment.put(key, value)
  }
  val process = processBuilder.start()
  val standardOutputFuture = Future[Unit] {
    val reader = new BufferedReader(new InputStreamReader(process.getInputStream, charset))
    var line = reader.readLine()
    while (line != null) {
      standardOutputLine(line)
      line = reader.readLine()
    }
  }
  val errorOutputFuture = Future[Unit] {
    val reader = new BufferedReader(new InputStreamReader(process.getErrorStream, charset))
    var line = reader.readLine()
    while (line != null) {
      errorOutputLine(line)
      line = reader.readLine()
    }
  }
  val processInput = new PrintWriter(new OutputStreamWriter(process.getOutputStream, charset))

  def sendLine(line: String): Unit = {
    processInput.println(line)
    processInput.flush()
  }

  def waitForExitCode(): Future[Int] = {
    val exitCodeFuture = Future[Int] {
      process.waitFor()
    }
    val compositeFuture = for {
      _ <- errorOutputFuture
      _ <- standardOutputFuture
      exitCode <- exitCodeFuture
    } yield {
      exitCode
    }
    compositeFuture
  }
}
