package aoc.y2015

import aoc.Solution
import util.chaining.*

/** https://adventofcode.com/2015/day/10
  *
  * To solve it, register the parts:
  *   part1 { in => in.lines.size }
  */
object Day10 extends Solution(2015, 10):
  def lookAndSay(s: String): String =
    val sb = new StringBuilder()

    @scala.annotation.tailrec
    def loop(i: Int): String =
      if i > s.length then sb.toString
      else
        val c = s.charAt(i)
        val j = s.indexWhere(_ != c, i)

        if j == -1 then
          sb += (s.length - i).toString.head
          sb += c
          sb.toString
        else
          sb += (j - i).toString.head
          sb += c
          loop(j)
    loop(0)

  def solve(s: String, n: Int): Int =
    Iterator
      .iterate(s, n + 1)(lookAndSay)
      .drop(n)
      .take(1)
      .toList
      .head
      .length

  part1 {
    _.text.pipe(s => solve(s, 40))
  }

  part2 {
    _.text.pipe(s => solve(s, 50))
  }
end Day10
