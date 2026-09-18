package aoc

/** Uma solucao de um dia do Advent of Code.
  *
  * Implemente sobrescrevendo `part1` / `part2`. O que nao for sobrescrito
  * continua marcado como [[Solution.Unsolved]] e o runner simplesmente pula.
  *
  * {{{
  * object Day01 extends Solution(2015, 1):
  *   override def part1(in: Input): Any = in.text.count(_ == '(')
  * }}}
  */
abstract class Solution(val year: Int, val day: Int):

  def part1(in: Input): Any = Solution.Unsolved
  def part2(in: Input): Any = Solution.Unsolved

  final def label: String = f"$year%04d dia $day%02d"

  /** Se a parte foi sobrescrita nesta solucao (checado sem executar nada). */
  final def solved(part: Int): Boolean =
    val name = if part == 1 then "part1" else "part2"
    try getClass.getDeclaredMethod(name, classOf[Input]) != null
    catch case _: NoSuchMethodException => false

object Solution:

  /** Marcador devolvido pelas partes ainda nao implementadas. */
  case object Unsolved:
    override def toString: String = "—"
