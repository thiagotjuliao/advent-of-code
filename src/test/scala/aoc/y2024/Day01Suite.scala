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

  test("parte 1: distancia total") {
    assertEquals(Day01.part1(sample), 11)
  }

  test("parte 2: score de similaridade") {
    assertEquals(Day01.part2(sample), 31)
  }
