package aoc.util

/** Grade retangular imutavel indexada por [[Point]] (`x` = coluna, `y` = linha). */
final case class Grid[A](cells: Vector[Vector[A]]):

  val height: Int = cells.length
  val width: Int  = if cells.isEmpty then 0 else cells.map(_.length).max

  def contains(p: Point): Boolean =
    p.y >= 0 && p.y < height && p.x >= 0 && p.x < cells(p.y).length

  def apply(p: Point): A         = cells(p.y)(p.x)
  def get(p: Point): Option[A]   = Option.when(contains(p))(apply(p))
  def getOrElse(p: Point, default: => A): A = get(p).getOrElse(default)

  def updated(p: Point, a: A): Grid[A] =
    Grid(cells.updated(p.y, cells(p.y).updated(p.x, a)))

  def points: Iterator[Point] =
    for
      y <- (0 until height).iterator
      x <- (0 until cells(y).length).iterator
    yield Point(x, y)

  def entries: Iterator[(Point, A)] = points.map(p => p -> apply(p))
  def values: Iterator[A]           = cells.iterator.flatten

  def find(a: A): Option[Point]                  = points.find(apply(_) == a)
  def findAll(a: A): Vector[Point]               = points.filter(apply(_) == a).toVector
  def where(pred: A => Boolean): Vector[Point]   = points.filter(p => pred(apply(p))).toVector
  def count(pred: A => Boolean): Int             = values.count(pred)

  /** Vizinhos ortogonais que caem dentro da grade. */
  def neighbors4(p: Point): Vector[Point] = p.neighbors4.filter(contains)
  def neighbors8(p: Point): Vector[Point] = p.neighbors8.filter(contains)

  def map[B](f: A => B): Grid[B]                 = Grid(cells.map(_.map(f)))
  def mapPoints[B](f: (Point, A) => B): Grid[B]  =
    Grid(cells.zipWithIndex.map((row, y) => row.zipWithIndex.map((a, x) => f(Point(x, y), a))))

  def rows: Vector[Vector[A]]    = cells
  def columns: Vector[Vector[A]] = cells.transpose
  def transpose: Grid[A]         = Grid(cells.transpose)

  def render(f: A => Char): String = cells.map(_.map(f).mkString).mkString("\n")

object Grid:

  def fromLines(lines: Seq[String]): Grid[Char] = Grid(lines.toVector.map(_.toVector))

  def fill[A](width: Int, height: Int)(a: => A): Grid[A] =
    Grid(Vector.fill(height)(Vector.fill(width)(a)))

  def tabulate[A](width: Int, height: Int)(f: Point => A): Grid[A] =
    Grid(Vector.tabulate(height, width)((y, x) => f(Point(x, y))))

  extension (g: Grid[Char]) def show: String = g.render(identity)
