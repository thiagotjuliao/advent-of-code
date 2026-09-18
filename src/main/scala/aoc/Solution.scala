package aoc

import scala.collection.mutable

/** One day of the Advent of Code.
  *
  * A day registers its parts through the `part1` / `part2` builders. Each
  * closure is type-checked where it is written and its result type is
  * inferred, so no solution ever has to name `Any`:
  *
  * {{{
  * object Day01 extends Solution(2015, 1):
  *   part1 { in => in.text.count(_ == '(') }      // Int
  *   part2 { in => in.words.mkString(",") }       // String
  * }}}
  *
  * A day that registers nothing is a stub, and the runner skips it.
  */
abstract class Solution(val year: Int, val day: Int):

  private val parts = mutable.Map.empty[Int, Input => Any]

  protected final def part1[A](f: Input => A): Unit = register(1, f)
  protected final def part2[A](f: Input => A): Unit = register(2, f)

  private def register[A](part: Int, f: Input => A): Unit =
    require(!parts.contains(part), s"$label: part $part was registered twice")
    parts(part) = f

  /** Whether this part has been written yet. */
  final def solved(part: Int): Boolean = parts.contains(part)

  /** Runs a registered part.
    *
    * This is the one place where the type is lost, and it is the right place:
    * the runner does nothing with the answer but print it, and one day's part
    * returns an Int where the next returns a String. The typing that matters
    * already happened inside the closure.
    */
  final def solve(part: Int, in: Input): Any =
    parts.get(part) match
      case Some(f) => f(in)
      case None => throw IllegalStateException(s"$label: part $part is not implemented")

  final def label: String = f"$year%04d day $day%02d"
end Solution
