package com.seanshubin.builder.core

import scala.collection.mutable.ArrayBuffer

sealed abstract case class CommandEnum(name: String) {
  CommandEnum.valuesBuffer += this

  def execute(projectName: String, api: Api): Report
}

object CommandEnum {
  private val valuesBuffer = new ArrayBuffer[CommandEnum]
  lazy val values = valuesBuffer.toSeq
  val Deploy = new CommandEnum("deploy") {
    override def execute(projectName: String, api: Api): Report = {
      val executionResult = api.deploy(projectName)
      ExecutionReport(projectName, name, executionResult)
    }
  }
  val Verify = new CommandEnum("verify") {
    override def execute(projectName: String, api: Api): Report = {
      val executionResult = api.verify(projectName)
      ExecutionReport(projectName, name, executionResult)
    }
  }
  val Install = new CommandEnum("install") {
    override def execute(projectName: String, api: Api): Report = {
      val executionResult = api.install(projectName)
      ExecutionReport(projectName, name, executionResult)
    }
  }
  val Compile = new CommandEnum("compile") {
    override def execute(projectName: String, api: Api): Report = {
      val executionResult = api.compile(projectName)
      ExecutionReport(projectName, name, executionResult)
    }
  }
  val Ignore = new CommandEnum("ignore") {
    override def execute(projectName: String, api: Api): Report = {
      IgnoredReport(projectName)
    }
  }

  def fromName(name: String): CommandEnum = {
    values.find(x => x.name.equalsIgnoreCase(name)) match {
      case Some(command) => command
      case None => throw new RuntimeException(s"No command named '$name'")
    }
  }
}
