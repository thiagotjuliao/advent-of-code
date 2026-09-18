package aoc

/** Solution discovery.
  *
  * There is no registry to maintain: the runner looks up `aoc.yYYYY.DayDD`
  * by reflection, so creating the file is enough.
  */
object Solutions:

  /** Years whose event has been published (the AoC starts every December 1st). */
  val years: Vector[Int] = (2015 to 2025).toVector

  val days: Vector[Int] = (1 to 25).toVector

  def find(year: Int, day: Int): Option[Solution] =
    val className = f"aoc.y$year%04d.Day$day%02d$$"
    try
      val cls = Class.forName(className)
      Option(cls.getField("MODULE$").get(null)).collect { case s: Solution => s }
    catch case _: ReflectiveOperationException => None

  def ofYear(year: Int): Vector[Solution] = days.flatMap(find(year, _))

  def all: Vector[Solution] = years.flatMap(ofYear)

  def isImplemented(s: Solution): Boolean = s.solved(1) || s.solved(2)

  /** The year's days with at least one part implemented — stubs left out. */
  def implementedOfYear(year: Int): Vector[Solution] = ofYear(year).filter(isImplemented)

  def implemented: Vector[Solution] = years.flatMap(implementedOfYear)
