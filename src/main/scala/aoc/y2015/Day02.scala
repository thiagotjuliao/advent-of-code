package aoc.y2015

import aoc.{Input, Solution}

/** https://adventofcode.com/2015/day/2 — I Was Told There Would Be No Math */
object Day02 extends Solution(2015, 2):

  /** Each "LxWxH" line becomes its three dimensions in ascending order. */
  private def boxes(in: Input): Vector[(Int, Int, Int)] =
    in.lines.map(_.split("x").map(_.trim.toInt).sorted).map(d => (d(0), d(1), d(2)))

  override def part1(in: Input): Any =
    boxes(in).map((a, b, c) => 2 * (a * b + b * c + c * a) + a * b).sum

  override def part2(in: Input): Any =
    boxes(in).map((a, b, c) => 2 * (a + b) + a * b * c).sum
