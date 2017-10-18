package com.seanshubin.builder.domain

object PathUtil {
  private val unsafeCharacters = ":"
  private val replacementCharacter = '-'

  def makeFileNameSafeForOperatingSystem(fileName: String): String = {
    def replace(s: String, oldCharacter: Char): String = {
      s.replace(oldCharacter, replacementCharacter)
    }

    unsafeCharacters.foldLeft(fileName)(replace)
  }
}
