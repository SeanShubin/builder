package com.seanshubin.builder.core

import com.seanshubin.devon.core.devon._
import com.seanshubin.utility.reflection.{ReflectionImpl, SimpleTypeConversion}

import scala.reflect.runtime._

object CustomDevonMarshaller extends DevonMarshallerImpl(
  compactFormatter = new CompactDevonFormatterImpl,
  prettyFormatter = new PrettyDevonFormatterImpl,
  devonReflection = new DevonReflectionImpl(new ReflectionImpl(
    SimpleTypeConversion.defaultConversions +
      (universe.typeTag[CommandEnum].tpe.toString -> CommandDevonConversion)
  )),
  iteratorFactory = new DefaultDevonIteratorFactory)
