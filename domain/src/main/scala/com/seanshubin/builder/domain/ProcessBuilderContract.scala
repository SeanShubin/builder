package com.seanshubin.builder.domain

import java.io.File
import java.util

trait ProcessBuilderContract {
  def command(command: String*): ProcessBuilderContract

  def directory(directory: File): ProcessBuilderContract

  def environment: util.Map[String, String]

  def start: ProcessContract
}
