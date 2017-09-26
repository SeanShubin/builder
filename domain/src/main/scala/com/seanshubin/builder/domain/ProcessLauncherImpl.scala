package com.seanshubin.builder.domain

import java.io._
import java.nio.charset.Charset
import java.time.Clock

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{ExecutionContext, Future}

class ProcessLauncherImpl(createProcessBuilder: () => ProcessBuilderContract,
                          futureRunner: FutureRunner,
                          clock: Clock,
                          charset: Charset)(implicit executionContext: ExecutionContext) extends ProcessLauncher {
  override def launch(input: ProcessInput): Future[ProcessOutput] = {
    val processBuilder = createProcessBuilder()
    updateEnvironment(processBuilder, input.environment)
    val started = clock.instant()
    val process = processBuilder.
      command(input.command: _*).
      directory(input.directory.toFile).start
    val standardOutputFuture = captureLines(process.getInputStream)
    val standardErrorFuture = captureLines(process.getErrorStream)
    val exitCodeFuture = captureExitCode(process)
    val compositeFuture = for {
      outputLines <- standardOutputFuture
      errorLines <- standardErrorFuture
      exitCode <- exitCodeFuture
    } yield {
      val ended = clock.instant()
      ProcessOutput(input, exitCode, outputLines, errorLines, started, ended)
    }
    compositeFuture
  }

  private def updateEnvironment(processBuilder: ProcessBuilderContract, environment: Map[String, String]) = {
    for {
      (key, value) <- environment
    } {
      processBuilder.environment.put(key, value)
    }
  }

  private def captureLines(inputStream: InputStream): Future[Seq[String]] = {
    futureRunner.runInFuture {
      val linesBuffer = new ArrayBuffer[String]
      val inputStreamReader = new InputStreamReader(inputStream, charset)
      val bufferedReader = new BufferedReader(inputStreamReader)
      var line = bufferedReader.readLine()
      while (line != null) {
        linesBuffer.append(line)
        line = bufferedReader.readLine()
      }
      linesBuffer
    }
  }

  private def captureExitCode(process: ProcessContract): Future[Int] = {
    futureRunner.runInFuture {
      process.waitFor
    }
  }
}
