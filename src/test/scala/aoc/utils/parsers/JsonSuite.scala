package aoc.utils.parsers

import munit.FunSuite

import Json.*

class JsonSuite extends FunSuite:

  import Json.parse

  test("scalars: null, numbers and strings") {
    assertEquals(parse("null"), Some(JNull))
    assertEquals(parse("0"), Some(JNumber(0)))
    assertEquals(parse("123"), Some(JNumber(123)))
    assertEquals(parse("-12"), Some(JNumber(-12)))
    assertEquals(parse("\"\""), Some(JString("")))
    assertEquals(parse("\"red\""), Some(JString("red")))
  }

  test("not JSON: fails instead of throwing") {
    assertEquals(parse(""), None)
    assertEquals(parse("abc"), None)
    assertEquals(parse("-"), None)
    assertEquals(parse("nul"), None)
    assertEquals(parse("\"abc"), None)
  }

  test("the whole input must be consumed: trailing input fails") {
    assertEquals(parse("1abc"), None)
    assertEquals(parse("[1]]"), None)
    assertEquals(parse("null null"), None)
    assertEquals(parse("{}x"), None)
  }

  test("whitespace around the value is allowed") {
    assertEquals(parse(" \n[1]"), Some(JArray(Vector(JNumber(1)))))
    assertEquals(parse("[1] \n"), Some(JArray(Vector(JNumber(1)))))
    assertEquals(parse("   "), None)
  }

  test("arrays: empty, flat, mixed and nested") {
    assertEquals(parse("[]"), Some(JArray(Vector.empty)))
    assertEquals(parse("[1,2,3]"), Some(JArray(Vector(JNumber(1), JNumber(2), JNumber(3)))))
    assertEquals(
      parse("[1,\"red\",null]"),
      Some(JArray(Vector(JNumber(1), JString("red"), JNull)))
    )
    assertEquals(parse("[[[3]]]"), Some(JArray(Vector(JArray(Vector(JArray(Vector(JNumber(3)))))))))
  }

  test("objects: empty, flat and nested") {
    assertEquals(parse("{}"), Some(JObject(Map.empty)))
    assertEquals(
      parse("{\"a\":2,\"b\":4}"),
      Some(JObject(Map("a" -> JNumber(2), "b" -> JNumber(4))))
    )
    assertEquals(
      parse("{\"a\":{\"b\":4},\"c\":-1}"),
      Some(JObject(Map("a" -> JObject(Map("b" -> JNumber(4))), "c" -> JNumber(-1))))
    )
  }

  test("objects and arrays inside each other") {
    assertEquals(
      parse("[1,{\"c\":\"red\",\"b\":2},3]"),
      Some(
        JArray(
          Vector(
            JNumber(1),
            JObject(Map("c" -> JString("red"), "b" -> JNumber(2))),
            JNumber(3)
          )
        )
      )
    )
    assertEquals(
      parse("{\"e\":[1,{}],\"f\":[]}"),
      Some(
        JObject(
          Map("e" -> JArray(Vector(JNumber(1), JObject(Map.empty))), "f" -> JArray(Vector.empty))
        )
      )
    )
  }

  test("malformed containers fail") {
    assertEquals(parse("["), None)
    assertEquals(parse("[1,]"), None)
    assertEquals(parse("[1 2]"), None)
    assertEquals(parse("{\"a\"}"), None)
    assertEquals(parse("{\"a\":1,}"), None)
    assertEquals(parse("{1:2}"), None)
  }

  test("whitespace between tokens is allowed") {
    assertEquals(
      parse("{ \"a\" : [ 1 , 2 ] ,\n\"b\" : null }"),
      Some(JObject(Map("a" -> JArray(Vector(JNumber(1), JNumber(2))), "b" -> JNull)))
    )
  }

  test("a repeated key keeps the last value") {
    assertEquals(parse("{\"a\":1,\"a\":2}"), Some(JObject(Map("a" -> JNumber(2)))))
  }
end JsonSuite
