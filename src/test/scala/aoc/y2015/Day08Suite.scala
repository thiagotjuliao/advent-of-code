package aoc.y2015

import aoc.AOCFunSuite
import aoc.Input

class Day08Suite extends AOCFunSuite(Day08):
  part1 {
    sample("sample00", Input("\"\""), 2)
    sample("sample01", Input("\"abc\""), 2)
    sample("sample02", Input("\"aaa\\\"aaa\""), 3)
    sample("sample03", Input("\"\\x27\""), 5)
  }

  part2 {
    sample("sample00", Input("\"\""), 4)
    sample("sample01", Input("\"abc\""), 4)
    sample("sample02", Input("\"aaa\\\"aaa\""), 6)
    sample("sample03", Input("\"\\x27\""), 5)
  }
