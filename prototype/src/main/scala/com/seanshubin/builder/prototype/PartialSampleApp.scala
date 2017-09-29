package com.seanshubin.builder.prototype

object PartialSampleApp extends App {

  trait Foo {
    def handle(x: Int): String = handleToOverride.orElse(handleDefault)(x)

    val handleDefault: PartialFunction[Int, String] = {
      case x => s"value = $x"
    }

    def handleToOverride: PartialFunction[Int, String]
  }

  class Bar extends Foo {
    override def handleToOverride: PartialFunction[Int, String] = {
      case 1 => "one"
      case 2 => "two"
      case 3 => "three"
    }
  }

  val a = new Bar()
  println(a.handle(2))
  println(a.handle(4))
}
