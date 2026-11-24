package aoc.y2015

import aoc.AOCFunSuite
import aoc.Input

class Day14Suite extends AOCFunSuite(Day14):
  val input = Input(
    """
      |Comet can fly 14 km/s for 10 seconds, but then must rest for 127 seconds.
      |Dancer can fly 16 km/s for 11 seconds, but then must rest for 162 seconds.
    """.stripMargin.trim
  )

  part1 {
    sample("sample00", input, 2660)
  }
