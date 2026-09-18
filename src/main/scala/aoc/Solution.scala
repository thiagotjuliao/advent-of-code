package aoc

/** One day of the Advent of Code.
  *
  * Solve it by overriding `part1` / `part2`. Whatever is left alone stays
  * marked as [[Solution.Unsolved]] and the runner simply skips it.
  *
  * {{{
  * object Day01 extends Solution(2015, 1):
  *   override def part1(in: Input): Any = in.text.count(_ == '(')
  * }}}
  */
abstract class Solution(val year: Int, val day: Int):

  def part1(in: Input): Any = Solution.Unsolved
  def part2(in: Input): Any = Solution.Unsolved

  final def label: String = f"$year%04d day $day%02d"

  /** Whether this part was overridden here — checked without running anything. */
  final def solved(part: Int): Boolean =
    val name = if part == 1 then "part1" else "part2"
    try getClass.getDeclaredMethod(name, classOf[Input]) != null
    catch case _: NoSuchMethodException => false

object Solution:

  /** What the parts that are not implemented yet return. */
  case object Unsolved:
    override def toString: String = "—"
