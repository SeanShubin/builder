package com.seanshubin.builder.core

import org.scalatest.FunSuite
import org.scalatest.mock.EasyMockSugar

import scala.collection.mutable.ArrayBuffer

class RunnerTest extends FunSuite with EasyMockSugar {
  test("application flow") {
    val lines = new ArrayBuffer[String]()
    val emitLine: String => Unit = line => lines.append(line)
    val runner: Runner = new RunnerImpl("world", emitLine)
    runner.run()
    assert(lines.size === 1)
    assert(lines(0) === "Hello, world!")
  }
}
