package aoc.y2015

import aoc.Solution
import aoc.utils.Grid
import aoc.utils.Light

/** https://adventofcode.com/2015/day/6
  *
  * To solve it, register the parts:
  *   part1 { in => in.lines.size }
  */
object Day06 extends Solution(2015, 6):
  type Instruction[T] = Grid[T] => Grid[T]

  private val InstructionPattern =
    "(turn on|turn off|toggle) (\\d+),(\\d+) through (\\d+),(\\d+)".r

  def parse[T](s: String)(on: T => T, off: T => T, toggle: T => T): Instruction[T] =
    s match
      case InstructionPattern(action, x1, y1, x2, y2) =>
        val f = action match
          case "turn on" => on
          case "turn off" => off
          case "toggle" => toggle

        _.foldRegion(x1.toInt to x2.toInt, y1.toInt to y2.toInt)(f)

  part1 {
    val grid = Grid.make(Array(1000, 1000), Light.put)

    _.lines
      .map(s => parse[Light](s)(_.turnOn, _.turnOff, _.toggle))
      .foldLeft(grid)((g, i) => i(g))
      .count(_.isOn)
  }

  part2 {
    val grid = Grid.make(Array(1000, 1000), 0)

    _.lines
      .map(s => parse[Int](s)(_ + 1, b => (b - 1).max(0), _ + 2))
      .foldLeft(grid)((g, i) => i(g))
      .data
      .sum
  }
end Day06
