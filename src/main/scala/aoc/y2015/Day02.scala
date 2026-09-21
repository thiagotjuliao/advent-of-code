package aoc.y2015

import aoc.Solution
import aoc.utils.RectangularPrism
import scala.util.chaining.*

/** https://adventofcode.com/2015/day/2
  *
  * To solve it, register the parts:
  *   part1 { in => in.lines.size }
  */
object Day02 extends Solution(2015, 2):
  private def totalWrappingPaper(box: RectangularPrism): Int =
    box.totalArea + box.smallestSideArea

  private def totalRibbon(box: RectangularPrism): Int =
    box.totalVolume + box.sidePerimeters.min

  part1 {
    _.lines
      .map(
        _.pipe(RectangularPrism.parse)
          .pipe(totalWrappingPaper)
      )
      .sum
  }

  part2 {
    _.lines
      .map(
        _.pipe(RectangularPrism.parse)
          .pipe(totalRibbon)
      )
      .sum
  }
