package aoc

import munit.FunSuite
import munit.Location
import munit.TestOptions

trait AOCFunSuite(solution: Solution) extends FunSuite:
  opaque type Part = Int

  def part1(tests: Part ?=> Unit): Unit = tests(using 1)
  def part2(tests: Part ?=> Unit): Unit = tests(using 2)

  def sample[A](options: TestOptions, input: Input, expected: A)(using
      part: Part,
      loc: Location
  ): Unit =
    test(options.withName(s"part $part - ${options.name}")):
      val obtained = solution.solve(part, input)
      assertEquals(obtained, expected)
