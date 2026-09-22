package aoc.utils

import scala.reflect.ClassTag

final class Grid[T] private (shape: Array[Int], cells: Array[T]):
  private val size = shape.product
  private val strides = (1 to shape.size).map(i => shape.drop(i).product)

  private def idx(dims: Int*): Int =
    require(
      dims.size == this.size,
      s"number of dimensions must be equal to the grid's size (${this.size}). Got ${dims.size} instead"
    )
    dims.zip(shape).map(_ * _).sum

  def get(dims: Int*): T =
    cells(idx(dims*))

  def set(value: T, dims: Int*): Unit =
    cells(idx(dims*)) = value

  def update(dims: Int*)(f: T => T): Unit =
    set(f(get(dims*)), dims*)

  def show: String = ???

object Grid:
  def make[T: ClassTag](shape: Array[Int], init: T): Grid[T] =
    require(shape.size > 0, "a valid grid must have at least 1 dimension")

    val cells = Array.fill(shape.product)(init)
    Grid(shape, cells)
