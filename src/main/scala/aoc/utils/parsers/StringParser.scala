package aoc.utils.parsers

object StringParser:
  def some(p: Parser[String]): Parser[String] =
    Parser.some(p).map(_.mkString)

  def many(p: Parser[String]): Parser[String] =
    Parser.many(p).map(_.mkString)

  def condP(p: Char => Boolean): Parser[String] = Parser: s =>
    Option.when(s.nonEmpty && p(s.head))((s.head.toString, s.drop(1)))

  def charP(c: Char): Parser[String] = condP(_ == c)

  val numbersP: Parser[String] =
    (charP('-') <|> Parser.pure("")) |+| some(condP(_.isDigit))

  def wordP(s: String): Parser[String] =
    s.map(charP).foldLeft(Parser.pure(""))(_ |+| _)

  val stringP: Parser[String] =
    charP('"') *> many(condP(_ != '"')) <* charP('"')

  val spaceP: Parser[String] =
    many(condP(_.isWhitespace))

  extension (self: Parser[String])
    def |+|(that: Parser[String]): Parser[String] = Parser: s0 =>
      for
        (a, s1) <- self.run(s0)
        (b, s2) <- that.run(s1)
      yield (a + b, s2)
end StringParser
