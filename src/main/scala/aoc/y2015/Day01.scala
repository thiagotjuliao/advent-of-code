package aoc.y2015

import aoc.{Input, Solution}

/** https://adventofcode.com/2015/day/1 — Not Quite Lisp */
object Day01 extends Solution(2015, 1):

  private def steps(in: Input): Vector[Int] =
    in.text.toVector.collect { case '(' => 1; case ')' => -1 }

  override def part1(in: Input): Any = steps(in).sum

  override def part2(in: Input): Any =
    steps(in).scanLeft(0)(_ + _).indexOf(-1)
