package aoc.utils.parsers

import aoc.utils.parsers.StringParser.charP
import aoc.utils.parsers.StringParser.spaceP

final case class Parser[A] private[parsers] (run: String => Option[(A, String)]):
  def map[B](f: A => B): Parser[B] = Parser: s =>
    run(s).map: (a, s_) =>
      (f(a), s_)

  def flatMap[B](f: A => Parser[B]): Parser[B] = Parser: s =>
    run(s).flatMap: (a, s_) =>
      f(a).run(s_)

object Parser:
  def pure[A](a: A): Parser[A] =
    Parser(s => Some((a, s)))

  def opt[A](p: Parser[A]): Parser[Option[A]] =
    p.map(Some(_)) <|> pure(None)

  def many[A](p: Parser[A]): Parser[Vector[A]] =
    @annotation.tailrec
    def loop(s: String, acc: Vector[A]): Option[(Vector[A], String)] =
      p.run(s) match
        case None => Some((acc, s))
        case Some(a, s_) => loop(s_, acc :+ a)
    Parser(s => loop(s, Vector.empty))

  def some[A](p: Parser[A]): Parser[Vector[A]] =
    p.flatMap(a => many(p).map(a +: _))

  def sepBy[A](p: Parser[A], sep: Char): Parser[Vector[A]] =
    val sepBy1 = p.flatMap: a =>
      Parser.many(token(charP(sep)) *> p).map(a +: _)
    sepBy1 <|> pure(Vector.empty)

  def defer[A](p: => Parser[A]): Parser[A] =
    Parser(s => p.run(s))

  def token[A](p: Parser[A]): Parser[A] = p <* spaceP

  extension [A](self: Parser[A])
    def <|>(that: Parser[A]): Parser[A] = Parser: s =>
      self.run(s).orElse(that.run(s))

    def *>[B](that: Parser[B]): Parser[B] = Parser: s =>
      self.run(s).flatMap((_, s_) => that.run(s_))

    def <*[B](that: Parser[B]): Parser[A] = Parser: s =>
      for
        (a, s1) <- self.run(s)
        (_, s2) <- that.run(s1)
      yield (a, s2)
end Parser
