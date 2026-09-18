package aoc

import aoc.util.{Grid, Numbers, Point, Search}
import munit.FunSuite

class UtilSuite extends FunSuite:

  test("Point: rotacao horaria na convencao y-para-baixo") {
    assertEquals(Point.Up.rotateRight, Point.Right)
    assertEquals(Point.Right.rotateRight, Point.Down)
    assertEquals(Point.Up.rotateLeft, Point.Left)
  }

  test("Input: blocos, inteiros e grade") {
    val in = Input("1 2\n-3\n\n#.#\n..#\n")
    assertEquals(in.blocks.size, 2)
    assertEquals(in.ints, Vector(1, 2, -3))
    assertEquals(in.blocks(1).grid.count(_ == '#'), 3)
  }

  test("Grid: indexacao e vizinhos") {
    val grid = Grid.fromLines(Vector("abc", "def"))
    assertEquals(grid.width, 3)
    assertEquals(grid.height, 2)
    assertEquals(grid(Point(2, 1)), 'f')
    assertEquals(grid.get(Point(3, 0)), None)
    assertEquals(grid.find('d'), Some(Point(0, 1)))
    assertEquals(grid.neighbors4(Point(0, 0)).toSet, Set(Point(1, 0), Point(0, 1)))
  }

  test("Search: BFS num labirinto e Dijkstra com pesos") {
    val maze = Grid.fromLines(Vector("...", ".#.", "..."))
    val dist = Search.bfs(Point(0, 0))(p => maze.neighbors4(p).filter(maze(_) != '#'))
    assertEquals(dist(Point(2, 2)), 4)

    val custo = Search.dijkstraTo[Int](0, _ == 3)(n => if n < 3 then Seq(n + 1 -> 5L) else Nil)
    assertEquals(custo, Some(15L))
  }

  test("Numbers: lcm e crt") {
    assertEquals(Numbers.lcm(Seq(4L, 6L, 10L)), 60L)
    assertEquals(Numbers.crt(Seq(2L -> 3L, 3L -> 5L, 2L -> 7L)), 23L)
  }

  test("Solutions: descoberta por reflexao") {
    assert(Solutions.find(2015, 1).isDefined)
    assertEquals(Solutions.find(2015, 1).map(_.label), Some("2015 dia 01"))
    assert(Solutions.find(2015, 3).exists(s => !s.solved(1)), "stub nao pode contar como resolvido")
    assert(Solutions.implemented.sizeIs >= 3)
  }
end UtilSuite
