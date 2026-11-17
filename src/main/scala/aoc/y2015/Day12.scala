package aoc.y2015

import aoc.Solution
import util.chaining.*
import aoc.utils.parsers.Json

/** https://adventofcode.com/2015/day/12
  *
  * To solve it, register the parts:
  *   part1 { in => in.lines.size }
  */
object Day12 extends Solution(2015, 12):
  import Json.*

  def sumNumbers(json: String): Int =
    "(-?\\d+)".r.findAllIn(json).map(_.toInt).sum

  part1 {
    _.text.pipe(sumNumbers)
  }

  part2 { input =>
    val json = Json
      .parse(input.text)
      .getOrElse(throw IllegalArgumentException("input is not JSON"))

    def sum(j: Json): Int = j match
      case JNumber(n) => n
      case JArray(arr) => arr.map(sum).sum
      case JObject(map) if !map.values.exists(_ == JString("red")) => map.values.map(sum).sum
      case _ => 0

    sum(json)
  }
end Day12
