package aoc.y2015

import aoc.Solution
import aoc.utils.Point

/** https://adventofcode.com/2015/day/3
  *
  * To solve it, register the parts:
  *   part1 { in => in.lines.size }
  */
object Day03 extends Solution(2015, 3):
  private def move(c: Char): Point =
    c match
      case '^' => Point.origin.moveUp
      case '>' => Point.origin.moveRight
      case 'v' => Point.origin.moveDown
      case '<' => Point.origin.moveLeft

  part1 {
    _.text
      .foldLeft((Point.origin, Set[Point]())):
        case ((p, acc), c) =>
          val p_ = p + move(c)
          (p_, acc + p + p_)
      ._2
      .size
  }

  part2 {
    _.text.zipWithIndex
      .foldLeft((Point.origin, Point.origin, Set[Point]())):
        case ((ps, pr, acc), (c, i)) =>
          if i % 2 == 0 then
            val ps_ = ps + move(c)
            (ps_, pr, acc + ps + pr + ps_)
          else
            val pr_ = pr + move(c)
            (ps, pr_, acc + ps + pr + pr_)
      ._3
      .size
  }
end Day03
