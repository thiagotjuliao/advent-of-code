package aoc.y2015

import aoc.Solution

/** https://adventofcode.com/2015/day/8
  *
  * To solve it, register the parts:
  *   part1 { in => in.lines.size }
  */
object Day08 extends Solution(2015, 8):
  private def countChars(s: String): (Int, Int) =
    @scala.annotation.tailrec
    def loop(chars: List[Char], code: Int, memory: Int): (Int, Int) =
      chars match
        case Nil => (code, memory)
        case '"' :: rest => loop(rest, code + 1, memory)
        case '\\' :: 'x' :: _ :: _ :: rest => loop(rest, code + 4, memory + 1)
        case '\\' :: _ :: rest => loop(rest, code + 2, memory + 1)
        case _ :: rest => loop(rest, code + 1, memory + 1)
    loop(s.toList, 0, 0)

  private def countEncodedChars(s: String): (Int, Int) =
    @scala.annotation.tailrec
    def loop(chars: List[Char], original: Int, encoded: Int): (Int, Int) =
      chars match
        case Nil => (original, encoded)
        case '"' :: rest => loop(rest, original + 1, encoded + 3)
        case '\\' :: 'x' :: _ :: _ :: rest => loop(rest, original + 4, encoded + 5)
        case '\\' :: _ :: rest => loop(rest, original + 2, encoded + 4)
        case _ :: rest => loop(rest, original + 1, encoded + 1)
    loop(s.toList, 0, 0)

  part1 {
    _.lines.foldLeft(0): (acc, s) =>
      val (code, memory) = countChars(s)
      acc + (code - memory)
  }

  part2 {
    _.lines.foldLeft(0): (acc, s) =>
      val (original, encoded) = countEncodedChars(s)
      acc + (encoded - original)
  }
end Day08
