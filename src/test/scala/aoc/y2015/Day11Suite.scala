package aoc.y2015

import aoc.AOCFunSuite
import aoc.Input

class Day11Suite extends AOCFunSuite(Day11):
  part1 {
    sample("abcdefgh", Input("abcdefgh"), "abcdffaa")
    sample("ghijklmn", Input("ghijklmn"), "ghjaabcc")
  }
