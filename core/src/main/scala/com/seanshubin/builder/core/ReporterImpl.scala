package com.seanshubin.builder.core

import java.nio.charset.Charset
import java.nio.file.Path

import com.seanshubin.devon.parserules.DevonMarshaller
import com.seanshubin.utility.filesystem.FileSystemIntegration

import scala.collection.JavaConversions

class ReporterImpl(reportDir: Path, fileSystem: FileSystemIntegration, devonMarshaller: DevonMarshaller, charset: Charset) extends Reporter {
  override def storeAllReports(reports: Seq[Report]): Unit = {
    val buildDir = reportDir.resolve("build")
    fileSystem.createDirectories(buildDir)
    def storeReportInBuildDir(report: Report): Unit = {
      storeReport(buildDir, report)
    }
    reports.foreach(storeReportInBuildDir)
  }

  override def storeUpgradeResults(upgradeResults: Seq[ExecutionResult]): Unit = {
    val upgradeDir = reportDir.resolve("upgrade")
    fileSystem.createDirectories(upgradeDir)
    val upgradeReportPath = upgradeDir.resolve("upgrade-results.txt")
    val lines = devonMarshaller.valueToPretty(upgradeResults)
    fileSystem.write(upgradeReportPath, JavaConversions.asJavaIterable(lines), charset)
  }

  private def storeReport(buildDir: Path, report: Report): Unit = {
    report match {
      case x: ExecutionReport =>
        val lines = devonMarshaller.valueToPretty(x)
        val path = buildDir.resolve(x.projectName + ".txt")
        fileSystem.write(path, JavaConversions.asJavaIterable(lines), charset)
      case ExceptionReport(projectName, exception) =>
        val exceptionValue = ExceptionValue.fromException(exception)
        val lines = devonMarshaller.valueToPretty(exceptionValue)
        val path = buildDir.resolve(projectName + ".txt")
        fileSystem.write(path, JavaConversions.asJavaIterable(lines), charset)
      case _ => //do nothing
    }
  }
}
