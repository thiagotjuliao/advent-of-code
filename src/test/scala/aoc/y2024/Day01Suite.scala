package aoc.y2024

import aoc.Input
import munit.FunSuite

class Day01Suite extends FunSuite:

  private val sample = Input("""
    |3   4
    |4   3
    |2   5
    |1   3
    |3   9
    |3   3
  """.stripMargin.trim)

  test("part 1: total distance") {
    assertEquals(Day01.part1(sample), 11)
  }

  test("part 2: similarity score") {
    assertEquals(Day01.part2(sample), 31)
  }
