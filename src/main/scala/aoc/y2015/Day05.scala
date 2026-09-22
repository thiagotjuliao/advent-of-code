package aoc.y2015

import aoc.Solution
import aoc.utils.CharOps.*
import aoc.utils.BooleanOps.*

/** https://adventofcode.com/2015/day/5
  *
  * To solve it, register the parts:
  *   part1 { in => in.lines.size }
  */
object Day05 extends Solution(2015, 5):
  private def isNiceString(s: String): Boolean =
    val pairs = s.zip(s.drop(1))

    val invalidPairs = Set(
      ('a', 'b'),
      ('c', 'd'),
      ('p', 'q'),
      ('x', 'y')
    )

    @scala.annotation.tailrec
    def solve(i: Int = 0, v: Int = s.take(1).count(_.isVowel), p: Int = 0): Boolean =
      if i >= pairs.size then v >= 3 && p >= 1
      else
        val t @ (a, b) = pairs(i)

        if invalidPairs.contains(t) then false
        else solve(i + 1, v + b.isVowel.toInt, p + (a == b).toInt)
    solve()

  private def isNiceString2(s: String): Boolean =
    val triplets = s
      .zip(s.drop(1))
      .zip(s.drop(2))
      .map:
        case ((a, b), c) => (a, b, c)

    @scala.annotation.tailrec
    def solve(
        i: Int = 0,
        rule1: Boolean = false,
        rule2: Boolean = false,
        ps: Map[(Char, Char), Int] = Map.empty
    ): Boolean =
      if i >= triplets.size then false
      else
        val (a, b, c) = triplets(i)

        val rule1_ = rule1 || ps.get((a, b)).exists(_ <= i - 2) || ps.contains((b, c))
        val rule2_ = rule2 || a == c
        val ps_ = ps.updatedWith((a, b))(_.orElse(Some(i)))

        rule1_ && rule2_ || solve(i + 1, rule1_, rule2_, ps_)
    solve()
  end isNiceString2

  part1(_.lines.count(isNiceString))
  part2(_.lines.count(isNiceString2))
end Day05
