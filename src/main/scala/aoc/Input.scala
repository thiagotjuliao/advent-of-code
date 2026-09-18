package aoc

/** The puzzle text, with the most common readings already prepared.
  *
  * Everything is `lazy`: you only pay for the parsing you actually use.
  */
final class Input(raw: String):

  /** The text without trailing whitespace (the AoC always sends a final `\n`). */
  lazy val text: String = raw.stripTrailing

  /** One line per element. */
  lazy val lines: Vector[String] = text.linesIterator.toVector

  /** Blocks separated by a blank line — the classic AoC layout. */
  lazy val blocks: Vector[Input] =
    text.split("\\R\\s*\\R").toVector.map(b => Input(b))

  /** Every integer in the text, sign included. */
  lazy val ints: Vector[Int] = Input.IntPattern.findAllIn(text).map(_.toInt).toVector

  lazy val longs: Vector[Long] = Input.IntPattern.findAllIn(text).map(_.toLong).toVector

  /** One integer per line (a list of numbers, one per line). */
  lazy val intLines: Vector[Int] = lines.map(_.trim.toInt)

  /** Words split on any whitespace. */
  lazy val words: Vector[String] = text.split("\\s+").toVector.filter(_.nonEmpty)

  /** The text as rows of characters. */
  lazy val chars: Vector[Vector[Char]] = lines.map(_.toVector)

  /** Splits the text on an arbitrary separator. */
  def splitOn(sep: String): Vector[String] = text.split(sep).toVector.map(_.trim)

  /** Applies a regex line by line, returning the captured groups. */
  def captures(pattern: String): Vector[Vector[String]] =
    val re = pattern.r
    lines.flatMap(l => re.findFirstMatchIn(l).map(m => (1 to m.groupCount).toVector.map(m.group)))

  def isEmpty: Boolean = text.isEmpty

  override def toString: String = text
end Input

object Input:
  private val IntPattern = raw"-?\d+".r

  def apply(raw: String): Input = new Input(raw)
