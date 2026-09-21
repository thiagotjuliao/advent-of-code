package aoc.utils

final case class Point(x: Int, y: Int):
  def moveUp: Point = this + Point(0, 1)
  def moveDown: Point = this + Point(0, -1)
  def moveLeft: Point = this + Point(-1, 0)
  def moveRight: Point = this + Point(1, 0)

object Point:
  val origin: Point = Point(0, 0)

  extension (p1: Point)
    def +(p2: Point): Point =
      Point(p1.x + p2.x, p1.y + p2.y)
