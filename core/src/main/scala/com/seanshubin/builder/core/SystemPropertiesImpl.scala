package com.seanshubin.builder.core

class SystemPropertiesImpl extends SystemProperties {
  override def getProperty(key: String): String = System.getProperty(key)
}
