package aoc.y2015

import aoc.AOCFunSuite
import aoc.Input

class Day06Suite extends AOCFunSuite(Day06):
  part1 {
    sample("sample00", Input("turn on 0,0 through 999,999"), 1_000_000)
    sample("sample01", Input("toggle 0,0 through 999,0"), 1_000)
    sample("sample03", Input("turn off 499,499 through 500,500"), 0)
  }

  part2 {
    sample("sample00", Input("turn on 0,0 through 0,0"), 1)
    sample("sample01", Input("toggle 0,0 through 999,999"), 2_000_000)
  }
