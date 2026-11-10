package aoc.y2015

import aoc.Input
import aoc.Solution
import util.chaining.*

/** https://adventofcode.com/2015/day/9
  *
  * To solve it, register the parts:
  *   part1 { in => in.lines.size }
  */
object Day09 extends Solution(2015, 9):
  def parse(s: String): ((String, String), Int) =
    s match
      case s"$from to $to = $dist" =>
        ((from, to), dist.toInt)

  def init(ss: Seq[String]): Map[(String, String), Int] =
    ss.foldLeft(Map.empty): (acc, s) =>
      val ((from, to), dist) = parse(s)
      acc + ((from, to) -> dist) + ((to, from) -> dist)

  def buildRoutes(mp: Map[(String, String), Int], ls: List[String]): Option[(String, Int)] =
    ls.zip(ls.tail)
      .foldLeft(Option(("", 0))):
        case (Some((s, d1)), (loc1, loc2)) =>
          mp
            .get((loc1, loc2))
            .map: d2 =>
              val s_ = if s.isEmpty then loc1 else s + " -> " + loc1
              (s_, d1 + d2)
        case _ => None

  def solve(input: Input)(f: Iterator[(String, Int)] => Int): Int =
    val locationsMap = init(input.lines)
    val locations = locationsMap.keys.map(_._1).toSet.toList
    val routes =
      locations.permutations
        .map(buildRoutes.curried(locationsMap))
        .filter(_.isDefined)
        .map(_.get)
    f(routes)

  part1 {
    _.pipe(solve)(_.minBy(_._2)._2)
  }

  part2 {
    _.pipe(solve)(_.maxBy(_._2)._2)
  }
end Day09
