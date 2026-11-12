package aoc.utils

object CharOps:
  extension (c: Char) def isVowel: Boolean = vowels.contains(c)
