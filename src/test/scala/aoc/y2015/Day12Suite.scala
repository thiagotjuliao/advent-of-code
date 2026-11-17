package aoc.y2015

import aoc.AOCFunSuite
import aoc.Input

class Day12Suite extends AOCFunSuite(Day12):
  part1 {
    sample("[1,2,3]", Input("[1,2,3]"), 6)
    sample("{\"a\":2,\"b\":4}", Input("{\"a\":2,\"b\":4}"), 6)
    sample("[[[3]]]", Input("[[[3]]]"), 3)
    sample("{\"a\":{\"b\":4},\"c\":-1}", Input("{\"a\":{\"b\":4},\"c\":-1}"), 3)
    sample("{\"a\":[-1,1]}", Input("{\"a\":[-1,1]}"), 0)
    sample("[-1,{\"a\":1}]", Input("[-1,{\"a\":1}]"), 0)
    sample("[]", Input("[]"), 0)
    sample("{}", Input("{}"), 0)
  }

  part2 {
    sample("[1,2,3]", Input("[1,2,3]"), 6)
    sample("[1,{\"c\":\"red\",\"b\":2},3]", Input("[1,{\"c\":\"red\",\"b\":2},3]"), 4)
    sample(
      "{\"d\":\"red\",\"e\":[1,2,3,4],\"f\":5}",
      Input("{\"d\":\"red\",\"e\":[1,2,3,4],\"f\":5}"),
      0
    )
    sample("[1,\"red\",5]", Input("[1,\"red\",5]"), 6)
  }
