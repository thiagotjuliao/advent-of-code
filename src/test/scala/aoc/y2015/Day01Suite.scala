package aoc.y2015

import aoc.Input
import munit.FunSuite

class Day01Suite extends FunSuite:

  test("part 1: the examples from the puzzle text") {
    assertEquals(Day01.part1(Input("(())")), 0)
    assertEquals(Day01.part1(Input("(()(()(")), 3)
    assertEquals(Day01.part1(Input(")())())")), -3)
  }

  test("part 2: first position in the basement") {
    assertEquals(Day01.part2(Input(")")), 1)
    assertEquals(Day01.part2(Input("()())")), 5)
  }
