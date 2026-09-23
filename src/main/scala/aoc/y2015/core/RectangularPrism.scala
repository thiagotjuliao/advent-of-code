package aoc.y2015.core

final case class RectangularPrism(l: Int, w: Int, h: Int):
  val totalArea = 2 * (l * w + w * h + h * l)

  val totalVolume = l * w * h

  val sidePerimeters = Seq(
    2 * (l + w),
    2 * (w + h),
    2 * (h + l)
  )

  val smallestSideArea = Seq(l * w, w * h, h * l).min
end RectangularPrism

object RectangularPrism:
  def parse(s: String): RectangularPrism =
    s match
      case s"${l}x${w}x${h}" =>
        RectangularPrism(l.toInt, w.toInt, h.toInt)
