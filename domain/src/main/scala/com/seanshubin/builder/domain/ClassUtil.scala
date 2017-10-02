package com.seanshubin.builder.domain

object ClassUtil {
  def getSimpleClassName[T](something: T): String = {
    getSimpleName(something.getClass)
  }

  def getSimpleName[T](klass: Class[T]): String = {
    val possiblyMessySimpleName = klass.getSimpleName
    val indexOfDollar = possiblyMessySimpleName.indexOf('$')
    val simpleName = if (indexOfDollar == -1) {
      possiblyMessySimpleName
    } else {
      possiblyMessySimpleName.substring(0, indexOfDollar)
    }
    simpleName
  }
}
