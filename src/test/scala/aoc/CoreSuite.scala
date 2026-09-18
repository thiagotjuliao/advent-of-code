package aoc

import munit.FunSuite

class CoreSuite extends FunSuite:

  test("Input: blocks, integers and characters") {
    val in = Input("1 2\n-3\n\n#.#\n..#\n")
    assertEquals(in.blocks.size, 2)
    assertEquals(in.ints, Vector(1, 2, -3))
    assertEquals(in.blocks(1).chars.flatten.count(_ == '#'), 3)
  }

  test("Input: lines, words and captures") {
    val in = Input("a 1\nb 2\n")
    assertEquals(in.lines, Vector("a 1", "b 2"))
    assertEquals(in.words, Vector("a", "1", "b", "2"))
    assertEquals(in.captures(raw"(\w) (\d)"), Vector(Vector("a", "1"), Vector("b", "2")))
  }

  test("Solution: a registered part keeps its own type and runs") {
    object Probe extends Solution(1999, 1):
      part1 { in => in.ints.sum } // Int
      part2 { in => in.words.mkString("-") } // String

    assert(Probe.solved(1) && Probe.solved(2))
    assertEquals(Probe.solve(1, Input("1 2 3")), 6)
    assertEquals(Probe.solve(2, Input("a b")), "a-b")
  }

  test("Solution: a part that was never registered is not solved") {
    object Half extends Solution(1999, 2):
      part1 { in => in.text.length }

    assert(Half.solved(1))
    assert(!Half.solved(2))
    intercept[IllegalStateException](Half.solve(2, Input("x")))
  }

  test("Solutions: discovery by reflection") {
    assert(Solutions.find(2015, 1).isDefined)
    assertEquals(Solutions.find(2015, 1).map(_.label), Some("2015 day 01"))
    assert(Solutions.find(2015, 1).exists(s => !s.solved(1)), "a stub must not count as solved")
    assertEquals(Solutions.implemented, Vector.empty)
  }
end CoreSuite
