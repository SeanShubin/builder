package com.seanshubin.builder.prototype

object PartialSampleApp2 extends App {

  trait Foo {
    def handle(x: Int, y: Int, z: Int): String = handleToOverride(x, y).orElse(handleDefault(x, y))(z)

    def handleDefault(x: Int, y: Int): PartialFunction[Int, String] = {
      case z => s"value = $x $y $z"
    }

    def handleToOverride(x: Int, y: Int): PartialFunction[Int, String]
  }

  class Bar extends Foo {
    override def handleToOverride(x: Int, y: Int): PartialFunction[Int, String] = {
      case 1 => s"$x $y one"
      case 2 => s"$x $y two"
      case 3 => s"$x $y three"
    }
  }

  val a = new Bar()
  println(a.handle(1, 2, 3))
  println(a.handle(4, 5, 6))
}
