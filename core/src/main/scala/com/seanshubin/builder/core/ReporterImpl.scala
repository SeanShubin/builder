package com.seanshubin.builder.core

import java.nio.charset.Charset
import java.nio.file.Path

import com.seanshubin.devon.domain.DevonMarshaller

import scala.collection.JavaConverters._

class ReporterImpl(reportDir: Path, files: FilesContract, devonMarshaller: DevonMarshaller, charset: Charset) extends Reporter {
  override def storeAllReports(reports: Seq[Report]): Unit = {
    val buildDir = reportDir.resolve("build")
    files.createDirectories(buildDir)

    def storeReportInBuildDir(report: Report): Unit = {
      storeReport(buildDir, report)
    }

    reports.foreach(storeReportInBuildDir)
  }

  override def storeUpgradeResults(upgradeResults: Seq[ExecutionResult]): Unit = {
    val upgradeDir = reportDir.resolve("upgrade")
    files.createDirectories(upgradeDir)
    val upgradeReportPath = upgradeDir.resolve("upgrade-results.txt")
    val lines = devonMarshaller.valueToPretty(upgradeResults)
    files.write(upgradeReportPath, lines.asJava, charset)
  }

  private def storeReport(buildDir: Path, report: Report): Unit = {
    report match {
      case x: ExecutionReport =>
        val lines = devonMarshaller.valueToPretty(x)
        val path = buildDir.resolve(x.projectName + ".txt")
        files.write(path, lines.asJava, charset)
      case ExceptionReport(projectName, exception) =>
        val exceptionValue = ExceptionValue.fromException(exception)
        val lines = devonMarshaller.valueToPretty(exceptionValue)
        val path = buildDir.resolve(projectName + ".txt")
        files.write(path, lines.asJava, charset)
      case _ => //do nothing
    }
  }
}
