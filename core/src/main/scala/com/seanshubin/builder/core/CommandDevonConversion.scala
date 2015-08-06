package com.seanshubin.builder.core

import com.seanshubin.devon.reflection.SimpleTypeConversion

object CommandDevonConversion extends SimpleTypeConversion {
  override def toDynamic(x: Any): String = x.asInstanceOf[CommandEnum].name

  override def toStatic(x: String): Any = CommandEnum.fromName(x)
}
