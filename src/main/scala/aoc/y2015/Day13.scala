package aoc.y2015

import aoc.Solution

/** https://adventofcode.com/2015/day/13
  *
  * To solve it, register the parts:
  *   part1 { in => in.lines.size }
  */
object Day13 extends Solution(2015, 13):
  def parse(s: String): ((String, String), Int) =
    s match
      case s"$p1 would gain $v happiness units by sitting next to $p2." =>
        (p1, p2) -> v.toInt
      case s"$p1 would lose $v happiness units by sitting next to $p2." =>
        (p1, p2) -> -v.toInt

  def setRoundTable(
      arrangements: Seq[String],
      includesMe: Boolean = false
  ): Map[(String, String), Int] =
    arrangements.foldLeft(Map.empty): (acc, s) =>
      val ((p1, p2), h) = parse(s)
      if !includesMe then acc + ((p1, p2) -> h)
      else
        acc + ((p1, p2) -> h) +
          (("Me", p1) -> 0) +
          ((p1, "Me") -> 0) +
          (("Me", p2) -> 0) +
          ((p2, "Me") -> 0)

  def familyMembers(state: Map[(String, String), Int]): Set[String] =
    state.keys.foldLeft(Set.empty):
      case (acc, (p1, p2)) => acc ++ Set(p1, p2)

  def generatePairs(arrangement: Vector[String]): Vector[(String, String)] =
    arrangement.indices
      .flatMap: j =>
        val i = if j - 1 < 0 then arrangement.length - 1 else j - 1
        val k = if j + 1 > arrangement.length - 1 then 0 else j + 1
        Seq(
          (arrangement(j), arrangement(k)),
          (arrangement(j), arrangement(i))
        )
      .toVector

  part1 { input =>
    val arrangements = input.lines
    val state = setRoundTable(arrangements)
    val members = familyMembers(state).toList

    members.permutations.map(arr => generatePairs(arr.toVector).map(state).sum).max
  }

  part2 { input =>
    val arrangements = input.lines
    val state = setRoundTable(arrangements, includesMe = true)
    val members = familyMembers(state).toList

    members.permutations.map(arr => generatePairs(arr.toVector).map(state).sum).max
  }
end Day13
