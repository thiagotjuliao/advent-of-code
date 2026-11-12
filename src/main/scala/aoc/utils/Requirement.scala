package aoc.utils

final case class Requirement[A] private (check: A => Boolean):
  def apply(a: A): Boolean = check(a)

object Requirement:
  def req[A](check: A => Boolean): Requirement[A] = Requirement(check)

  extension [A](self: Requirement[A])
    def &(that: Requirement[A]): Requirement[A] =
      req(a => self.check(a) && that.check(a))

    def |(that: Requirement[A]): Requirement[A] =
      req(a => self.check(a) || that.check(a))
