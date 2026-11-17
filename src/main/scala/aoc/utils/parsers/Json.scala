package aoc.utils.parsers

import StringParser.*
import Parser.*

enum Json:
  case JNull
  case JNumber(n: Int)
  case JString(s: String)
  case JArray(arr: Vector[Json])
  case JObject(map: Map[String, Json])

object Json:
  // Every token consumes the whitespace after it, so each space has exactly one owner;
  // only the whitespace before the first token is left to `parse`.
  private val jnullP: Parser[Json] = token(wordP("null").map(_ => JNull))
  private val jnumberP: Parser[Json] = token(numbersP.map(s => JNumber(s.toInt)))
  private val jstringP: Parser[Json] = token(stringP.map(JString(_)))

  private val jarrayP: Parser[Json] =
    (token(charP('[')) *> sepBy(defer(parser), ',') <* token(charP(']'))).map(JArray(_))

  private val jobjectP: Parser[Json] =
    val kvP = (token(stringP) <* token(charP(':'))).flatMap(k => defer(parser).map(k -> _))
    (token(charP('{')) *> sepBy(kvP, ',') <* token(charP('}'))).map(kv => JObject(kv.toMap))

  private val parser: Parser[Json] =
    jnullP <|> jnumberP <|> jstringP <|> jarrayP <|> jobjectP

  /** `None` unless the whole of `s` is one JSON value, surrounding whitespace aside. */
  def parse(s: String): Option[Json] =
    (spaceP *> parser)
      .run(s)
      .collect:
        case (json, "") => json
end Json
