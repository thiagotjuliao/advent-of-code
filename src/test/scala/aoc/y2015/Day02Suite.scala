package aoc.y2015

import aoc.AOCFunSuite
import aoc.Input

class Day02Suite extends AOCFunSuite(Day02):
  part1 {
    sample("sample00", Input("2x3x4"), 58)
    sample("sample01", Input("1x1x10"), 43)
  }

  part2 {
    sample("sample00", Input("2x3x4"), 34)
    sample("sample01", Input("1x1x10"), 14)
  }
