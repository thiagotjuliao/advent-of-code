package aoc.y2015

import aoc.Input
import munit.FunSuite

class Day01Suite extends FunSuite:

  test("parte 1: exemplos do enunciado") {
    assertEquals(Day01.part1(Input("(())")), 0)
    assertEquals(Day01.part1(Input("(()(()(")), 3)
    assertEquals(Day01.part1(Input(")())())")), -3)
  }

  test("parte 2: primeira posicao no porao") {
    assertEquals(Day01.part2(Input(")")), 1)
    assertEquals(Day01.part2(Input("()())")), 5)
  }
