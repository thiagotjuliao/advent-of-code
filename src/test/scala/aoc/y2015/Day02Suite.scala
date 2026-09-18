package aoc.y2015

import aoc.Input
import munit.FunSuite

class Day02Suite extends FunSuite:

  test("parte 1: papel de embrulho") {
    assertEquals(Day02.part1(Input("2x3x4")), 58)
    assertEquals(Day02.part1(Input("1x1x10")), 43)
  }

  test("parte 2: fita") {
    assertEquals(Day02.part2(Input("2x3x4")), 34)
    assertEquals(Day02.part2(Input("1x1x10")), 14)
  }
