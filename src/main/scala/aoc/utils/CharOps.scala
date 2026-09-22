package aoc.utils

object CharOps:
  private val vowels = Set('a', 'e', 'i', 'o', 'u')

  extension (c: Char) def isVowel: Boolean = vowels.contains(c)
