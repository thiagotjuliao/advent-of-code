package aoc.y2015

import aoc.AOCFunSuite
import aoc.Input

class Day09Suite extends AOCFunSuite(Day09):
  val input = Input(
    """
        |London to Dublin = 464
        |London to Belfast = 518
        |Dublin to Belfast = 141
      """.stripMargin.trim
  )

  part1 {
    sample("sample", input, 605)
  }

  part2 {
    sample("sample", input, 982)
  }
