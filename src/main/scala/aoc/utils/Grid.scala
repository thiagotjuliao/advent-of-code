package aoc.utils

import scala.reflect.ClassTag

final class Grid[T: ClassTag] private (shape: Array[Int], cells: Array[T]):
  private val strides = shape.scanRight(1)(_ * _).tail

  private def idx(dims: Int*): Int =
    require(
      dims.size == shape.size,
      s"number of dimensions must be equal to the grid's size (${shape.size}). Got ${dims.size} instead"
    )
    dims.zip(strides).map(_ * _).sum

  private def unravel(idx: Int): Array[Int] =
    strides
      .scanLeft((0, idx)):
        case ((_, rem), s) => (rem / s, rem % s)
      .tail
      .map(_._1)

  def data: Seq[T] = cells.toSeq

  def get(dims: Int*): T =
    cells(idx(dims*))

  def set(value: T, dims: Int*): Grid[T] =
    Grid(shape, cells.updated(idx(dims*), value))

  def update(dims: Int*)(f: T => T): Grid[T] =
    Grid(shape, cells.updated(idx(dims*), f(get(dims*))))

  def fold(dims: Seq[Seq[Int]])(f: T => T): Grid[T] =
    val next = cells.clone()

    dims.foreach: d =>
      val i = idx(d*)
      next(i) = f(next(i))

    Grid(shape, next)

  def foldRegion(rows: Range, cols: Range)(f: T => T): Grid[T] =
    require(shape.length == 2, s"foldRegion expects a 2-D grid, got rank ${shape.length}")
    require(rows.isEmpty || (rows.min >= 0 && rows.max < shape(0)), s"rows $rows out of bounds")
    require(cols.isEmpty || (cols.min >= 0 && cols.max < shape(1)), s"cols $cols out of bounds")

    val next = cells.clone()

    for
      i <- rows
      j <- cols
    do
      val k = i * strides(0) + j * strides(1)
      next(k) = f(next(k))

    Grid(shape, next)

  def count(p: T => Boolean): Int = data.count(p)
end Grid

object Grid:
  def make[T: ClassTag](shape: Array[Int], init: T): Grid[T] =
    require(shape.size >= 2, "a valid grid must have at least 2 dimensions")

    val cells = Array.fill(shape.product)(init)
    Grid(shape, cells)
