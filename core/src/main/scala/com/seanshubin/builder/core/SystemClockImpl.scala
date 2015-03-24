package com.seanshubin.builder.core

class SystemClockImpl extends SystemClock {
  override def currentTimeMillis: Long = System.currentTimeMillis()
}
