package aoc

import aoc.util.Grid

/** O texto do puzzle, com as leituras mais comuns ja prontas.
  *
  * Tudo e `lazy`: so paga o parse que voce usar.
  */
final class Input(raw: String):

  /** Texto sem o whitespace final (o AoC sempre manda um `\n` no fim). */
  lazy val text: String = raw.stripTrailing

  /** Uma linha por elemento. */
  lazy val lines: Vector[String] = text.linesIterator.toVector

  /** Blocos separados por linha em branco (formato classico do AoC). */
  lazy val blocks: Vector[Input] =
    text.split("\\R\\s*\\R").toVector.map(b => Input(b))

  /** Todos os inteiros que aparecem no texto, sinal incluso. */
  lazy val ints: Vector[Int] = Input.IntPattern.findAllIn(text).map(_.toInt).toVector

  lazy val longs: Vector[Long] = Input.IntPattern.findAllIn(text).map(_.toLong).toVector

  /** Uma linha por inteiro (lista de numeros, um por linha). */
  lazy val intLines: Vector[Int] = lines.map(_.trim.toInt)

  /** Palavras separadas por qualquer whitespace. */
  lazy val words: Vector[String] = text.split("\\s+").toVector.filter(_.nonEmpty)

  /** O texto como grade de caracteres. */
  lazy val grid: Grid[Char] = Grid.fromLines(lines)

  /** Divide o texto por um separador arbitrario. */
  def splitOn(sep: String): Vector[String] = text.split(sep).toVector.map(_.trim)

  /** Aplica um regex linha a linha, devolvendo os grupos capturados. */
  def captures(pattern: String): Vector[Vector[String]] =
    val re = pattern.r
    lines.flatMap(l => re.findFirstMatchIn(l).map(m => (1 to m.groupCount).toVector.map(m.group)))

  def isEmpty: Boolean = text.isEmpty

  override def toString: String = text
end Input

object Input:
  private val IntPattern = raw"-?\d+".r

  def apply(raw: String): Input = new Input(raw)
