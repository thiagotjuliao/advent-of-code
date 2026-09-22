package aoc.utils

object BooleanOps:
  extension (b: Boolean)
    def toInt: Int = b match
      case false => 0
      case true => 1
