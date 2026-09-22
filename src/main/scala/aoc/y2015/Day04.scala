package aoc.y2015

import aoc.Solution
import aoc.utils.MD5
import scala.util.chaining.*

import java.util.stream.LongStream

/** https://adventofcode.com/2015/day/4
  *
  * To solve it, register the parts:
  *   part1 { in => in.lines.size }
  */
object Day04 extends Solution(2015, 4):
  // Claude's optimal suggestion
  private def claudeOptimized(prefix: String, zeros: Int, chunk: Long = 1_000_000L): Long =
    Iterator
      .iterate(0L)(_ + chunk)
      .map: start =>
        LongStream
          .range(start, start + chunk)
          .parallel()
          .filter(n => MD5.hasLeadingZeros(MD5.digest(prefix + n), zeros))
          .min()
      .find(_.isPresent)
      .get
      .getAsLong

  private val useClaudeOptimalSolution = true

  part1 { input =>
    @scala.annotation.tailrec
    def solve(s: String, n: Long = 0L): Long =
      if MD5.hash(s + n).startsWith("00000") then n
      else solve(s, n + 1)

    if !useClaudeOptimalSolution then input.text.pipe(s => solve(s))
    else claudeOptimized(input.text, 5)
  }

  part2 { input =>
    @scala.annotation.tailrec
    def solve(s: String, n: Long = 0L): Long =
      if MD5.hash(s + n).startsWith("000000") then n
      else solve(s, n + 1)

    if !useClaudeOptimalSolution then input.text.pipe(s => solve(s))
    else claudeOptimized(input.text, 6)
  }
end Day04
