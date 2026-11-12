package aoc.y2015

import aoc.Solution
import aoc.utils.letters
import aoc.utils.Requirement.*
import util.chaining.*

/** https://adventofcode.com/2015/day/11
  *
  * To solve it, register the parts:
  *   part1 { in => in.lines.size }
  */
object Day11 extends Solution(2015, 11):
  private val triplets = letters
    .lazyZip(letters.drop(1))
    .lazyZip(letters.drop(2))
    .map(_ + _ + _)

  /** Passwords must include one increasing straight of at least three letters,
    * like abc, bcd, cde, and so on, up to xyz. They cannot skip letters; abd doesn't count.
    */
  private val req1 = req[String]: s =>
    triplets.count(t => s.contains(t)) >= 1

  /** Passwords may not contain the letters i, o, or l,
    * as these letters can be mistaken for other characters and are therefore confusing.
    */
  private val req2 = req[String]: s =>
    !(s.contains("i") || s.contains("o") || s.contains("or") || s.contains("l"))

  /** Passwords must contain at least two different,
    * non-overlapping pairs of letters, like aa, bb, or zz.
    */
  private val req3 = req[String]: s =>
    "(\\w)\\1*".r.findAllIn(s).filter(_.length == 2).distinct.length >= 2

  private val isValid = req1 & req2 & req3

  private def increment(s: String): String =
    Iterator
      .unfold((s, s.length - 1)): (s, i) =>
        if i < 0 then None
        else
          val c = s.charAt(i)
          if c == 'z' then
            val s_ = s.updated(i, 'a')
            Some((s_, (s_, i - 1)))
          else
            val s_ = s.updated(i, (c + 1).toChar)
            Some(s_, (s_, -1))
      .toVector
      .last

  def nextPassword(pw: String, expired: Seq[String] = Seq()): String =
    require(pw.length == 8, "only 8 length passwords are acceptable")
    Iterator
      .iterate(pw)(increment)
      .dropWhile(pw => !isValid(pw) || expired.contains(pw))
      .take(1)
      .toList
      .head

  part1 {
    _.text.pipe(pw => nextPassword(pw))
  }

  part2 { input =>
    val pw = input.text
    val pw1 = nextPassword(pw)
    nextPassword(pw, Seq(pw1))
  }
end Day11
