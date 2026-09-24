package aoc.y2015

import aoc.Solution
import scala.util.chaining.*
import aoc.y2015.core.circuit.Instruction
import aoc.y2015.core.circuit.Circuit
import aoc.AocInput

/** https://adventofcode.com/2015/day/7
  *
  * To solve it, register the parts:
  *   part1 { in => in.lines.size }
  */
object Day07 extends Solution(2015, 7):
  val circuit = AocInput
    .load(2015, 7)
    .map: input =>
      input.lines
        .map(Instruction.parse)
        .pipe(Circuit.build)
    .toOption
    .get

  part1(_ => circuit.run()("a"))
  part2(_ => circuit.run(Map("b" -> 46065))("a"))
