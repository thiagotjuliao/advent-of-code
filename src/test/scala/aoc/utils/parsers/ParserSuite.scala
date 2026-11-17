package aoc.utils.parsers

import munit.FunSuite

import Parser.*
import StringParser.*

class ParserSuite extends FunSuite:

  private val digit: Parser[Int] = condP(_.isDigit).map(_.toInt)

  test("pure: succeeds without consuming") {
    assertEquals(pure(1).run("abc"), Some((1, "abc")))
    assertEquals(pure(1).run(""), Some((1, "")))
  }

  test("opt: Some when the parser matches, None without consuming otherwise") {
    assertEquals(opt(charP('-')).run("-1"), Some((Some("-"), "1")))
    assertEquals(opt(charP('-')).run("1"), Some((None, "1")))
    assertEquals(opt(charP('-')).run(""), Some((None, "")))
  }

  test("many: zero or more, stops at the first failure") {
    assertEquals(Parser.many(digit).run(""), Some((Vector.empty, "")))
    assertEquals(Parser.many(digit).run("x1"), Some((Vector.empty, "x1")))
    assertEquals(Parser.many(digit).run("12x3"), Some((Vector(1, 2), "x3")))
  }

  test("some: one or more, fails on zero") {
    assertEquals(Parser.some(digit).run(""), None)
    assertEquals(Parser.some(digit).run("x"), None)
    assertEquals(Parser.some(digit).run("12x"), Some((Vector(1, 2), "x")))
  }

  test("sepBy: empty, one element, several") {
    assertEquals(sepBy(digit, ',').run(""), Some((Vector.empty, "")))
    assertEquals(sepBy(digit, ',').run("]"), Some((Vector.empty, "]")))
    assertEquals(sepBy(digit, ',').run("1]"), Some((Vector(1), "]")))
    assertEquals(sepBy(digit, ',').run("1,2,3]"), Some((Vector(1, 2, 3), "]")))
  }

  test("sepBy: a trailing separator is left unconsumed") {
    assertEquals(sepBy(digit, ',').run("1,2,]"), Some((Vector(1, 2), ",]")))
  }

  test("<|>: the second alternative starts from the original input") {
    val ab = charP('a') *> charP('b')
    assertEquals((ab <|> charP('a')).run("ac"), Some(("a", "c")))
    assertEquals((charP('x') <|> charP('y')).run("z"), None)
  }

  test("*> keeps the right result, <* keeps the left one") {
    assertEquals((charP('(') *> digit).run("(1)"), Some((1, ")")))
    assertEquals((digit <* charP(')')).run("1)x"), Some((1, "x")))
    assertEquals((digit <* charP(')')).run("1x"), None)
  }

  test("|+|: concatenates both results, fails if either fails") {
    assertEquals((charP('a') |+| charP('b')).run("abc"), Some(("ab", "c")))
    assertEquals((charP('a') |+| charP('b')).run("ac"), None)
  }

  test("numbersP: positive and negative integers") {
    assertEquals(numbersP.run("123,"), Some(("123", ",")))
    assertEquals(numbersP.run("-12]"), Some(("-12", "]")))
    assertEquals(numbersP.run("0"), Some(("0", "")))
  }

  test("numbersP: needs at least one digit") {
    assertEquals(numbersP.run(""), None)
    assertEquals(numbersP.run("-"), None)
    assertEquals(numbersP.run("-x"), None)
    assertEquals(numbersP.run("abc"), None)
  }

  test("stringP: empty, plain and unterminated strings") {
    assertEquals(stringP.run("\"\""), Some(("", "")))
    assertEquals(stringP.run("\"name\":1"), Some(("name", ":1")))
    assertEquals(stringP.run("\"a b\""), Some(("a b", "")))
    assertEquals(stringP.run("\"abc"), None)
    assertEquals(stringP.run("abc\""), None)
  }

  test("wordP: the empty word matches without consuming") {
    assertEquals(wordP("").run("x"), Some(("", "x")))
    assertEquals(wordP("null").run("null,"), Some(("null", ",")))
    assertEquals(wordP("null").run("nul"), None)
  }

  test("spaceP: spaces, tabs and newlines") {
    assertEquals(spaceP.run(" \t\n\r1"), Some((" \t\n\r", "1")))
    assertEquals(spaceP.run("1"), Some(("", "1")))
  }
end ParserSuite
