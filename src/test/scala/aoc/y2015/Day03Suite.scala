package aoc.y2015

import aoc.AOCFunSuite
import aoc.Input

class Day03Suite extends AOCFunSuite(Day03):
  part1 {
    sample("sample00", Input(">"), 2)
    sample("sample01", Input("^>v<"), 4)
    sample("sample02", Input("^v^v^v^v^v"), 2)
  }

  part2 {
    sample("sample00", Input("^>"), 3)
    sample("sample01", Input("^>v<"), 3)
    sample("sample02", Input("^v^v^v^v^v"), 11)
  }
