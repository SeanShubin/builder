package com.seanshubin.builder.core

import java.nio.charset.Charset
import java.nio.file.Path

import com.seanshubin.devon.core.devon.DevonMarshaller
import com.seanshubin.utility.filesystem.FileSystemIntegration

import scala.collection.JavaConversions

class ReporterImpl(reportDir: Path, fileSystem: FileSystemIntegration, devonMarshaller: DevonMarshaller, charset: Charset) extends Reporter {
  override def storeAllReports(reports: Seq[Report]): Unit = {
    fileSystem.createDirectories(reportDir)
    reports.foreach(storeReport)
  }

  private def storeReport(report: Report): Unit = {
    report match {
      case x: ExecutionReport =>
        val lines = devonMarshaller.valueToPretty(x)
        val path = reportDir.resolve(x.projectName + ".txt")
        fileSystem.write(path, JavaConversions.asJavaIterable(lines), charset)
      case _ => //do nothing
    }
  }
}
