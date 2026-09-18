package aoc

/** Descoberta das solucoes.
  *
  * Nao existe registro manual: o runner procura pela classe
  * `aoc.yYYYY.DayDD` via reflexao, entao basta criar o arquivo.
  */
object Solutions:

  /** Anos com evento publicado (o AoC comeca todo 1 de dezembro). */
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

  /** Do ano, somente o que tem ao menos uma parte implementada (ignora os stubs). */
  def implementedOfYear(year: Int): Vector[Solution] = ofYear(year).filter(isImplemented)

  def implemented: Vector[Solution] = years.flatMap(implementedOfYear)
