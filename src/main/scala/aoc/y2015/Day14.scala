package aoc.y2015

import aoc.Solution
import aoc.utils.BooleanOps.toInt

/** https://adventofcode.com/2015/day/14
  *
  * To solve it, register the parts:
  *   part1 { in => in.lines.size }
  */
object Day14 extends Solution(2015, 14):
  final case class Reindeer(name: String, velocity: Int, stamina: Int, restTime: Int):
    def distanceTravelled(totalTime: Int): Int =
      @annotation.tailrec
      def loop(t: Int, r: Boolean, acc: Int): Int =
        if t > totalTime then acc
        else if r then loop(t + restTime, false, acc)
        else
          val delta = totalTime - t
          if stamina <= delta then loop(t + stamina, true, acc + (velocity * stamina))
          else loop(t + stamina, true, acc + (velocity * delta))
      loop(0, false, 0)

  object Reindeer:
    def from(s: String): Reindeer =
      s match
        case s"$n can fly $v km/s for $st seconds, but then must rest for $rt seconds." =>
          Reindeer(n, v.toInt, st.toInt, rt.toInt)

  def calculatePoints(racers: Seq[Reindeer], time: Int): Map[Reindeer, Int] =
    val dists = racers.map(_.distanceTravelled(time))
    val maxD = dists.max
    val points = dists.map(d => if d == maxD then 1 else 0)
    racers.zip(points).toMap

  part1 {
    _.lines.map(Reindeer.from).map(_.distanceTravelled(2503)).max
  }

  part2 { input =>
    val racers = input.lines.map(Reindeer.from)

    val results: Map[Reindeer, Int] = (1 to 2503).foldLeft(Map.empty): (acc, t) =>
      calculatePoints(racers, t).map:
        case (r, v) => (r, acc.getOrElse(r, 0) + v)

    results.maxBy(_._2)._2
  }
end Day14
