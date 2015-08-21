package com.seanshubin.builder.core

import com.seanshubin.devon.reflection.SimpleTypeConversion

import scala.reflect.runtime._

object CommandDevonConversion extends SimpleTypeConversion {
  override def className: String = universe.typeTag[CommandEnum].tpe.toString

  override def toDynamic(x: Any): String = x.asInstanceOf[CommandEnum].name

  override def toStatic(x: String): Any = CommandEnum.fromName(x)
}
