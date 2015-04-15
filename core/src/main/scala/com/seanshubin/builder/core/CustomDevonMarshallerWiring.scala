package com.seanshubin.builder.core

import com.seanshubin.devon.core.devon.DevonMarshallerWiring
import com.seanshubin.utility.reflection.SimpleTypeConversion

import scala.reflect.runtime._

object CustomDevonMarshallerWiring extends DevonMarshallerWiring {
  override lazy val typeConversions: Map[String, SimpleTypeConversion] =
    SimpleTypeConversion.defaultConversions + (universe.typeTag[CommandEnum].tpe.toString -> CommandDevonConversion)
}
