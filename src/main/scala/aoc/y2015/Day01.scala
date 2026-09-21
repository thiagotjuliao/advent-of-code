package aoc.y2015

import aoc.Solution

/** https://adventofcode.com/2015/day/1
  *
  * To solve it, register the parts:
  *   part1 { in => in.lines.size }
  */
object Day01 extends Solution(2015, 1):
  private def parse(c: Char): Int =
    c match
      case '(' => 1
      case ')' => -1

  part1 {
    _.text.map(parse).sum
  }

  part2 {
    @scala.annotation.tailrec
    def solve(s: String, n: Int = 0, f: Int = 0): Int =
      if n == s.size then -1
      else
        val f_ = f + parse(s(n))
        if f_ == -1 then n + 1
        else solve(s, n + 1, f_)

    input => solve(input.text)
  }
