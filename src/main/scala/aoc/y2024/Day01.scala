package aoc.y2024

import aoc.{Input, Solution}

/** https://adventofcode.com/2024/day/1 — Historian Hysteria */
object Day01 extends Solution(2024, 1):

  private def columns(in: Input): (Vector[Int], Vector[Int]) =
    val pairs = in.lines.map(_.split("\\s+").map(_.toInt))
    (pairs.map(_(0)), pairs.map(_(1)))

  override def part1(in: Input): Any =
    val (left, right) = columns(in)
    left.sorted.zip(right.sorted).map((a, b) => math.abs(a - b)).sum

  override def part2(in: Input): Any =
    val (left, right) = columns(in)
    val counts = right.groupMapReduce(identity)(_ => 1)(_ + _)
    left.map(n => n * counts.getOrElse(n, 0)).sum
