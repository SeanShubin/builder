package com.seanshubin.builder.core

trait ConfigurationFactory {
  def validate(args: Seq[String]): Either[Seq[String], Configuration]
}
