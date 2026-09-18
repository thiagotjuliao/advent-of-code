package aoc.util

/** Coordenada inteira 2D.
  *
  * Convencao usada no repo inteiro: `x` cresce para a direita (coluna) e
  * `y` cresce para BAIXO (linha), como em tela/grid — nao como no plano
  * cartesiano. As rotacoes seguem essa mesma convencao.
  */
final case class Point(x: Int, y: Int):
  def +(o: Point): Point       = Point(x + o.x, y + o.y)
  def -(o: Point): Point       = Point(x - o.x, y - o.y)
  def *(k: Int): Point         = Point(x * k, y * k)
  def unary_- : Point          = Point(-x, -y)

  def neighbors4: Vector[Point] = Point.Dirs4.map(this + _)
  def neighbors8: Vector[Point] = Point.Dirs8.map(this + _)

  def manhattan(o: Point): Int = math.abs(x - o.x) + math.abs(y - o.y)
  def chebyshev(o: Point): Int = math.max(math.abs(x - o.x), math.abs(y - o.y))

  /** Giro de 90 graus no sentido horario (na convencao y-para-baixo). */
  def rotateRight: Point = Point(-y, x)
  def rotateLeft: Point  = Point(y, -x)

  /** Linha reta (H, V ou diagonal) ate `o`, inclusive nas duas pontas. */
  def lineTo(o: Point): Vector[Point] =
    val steps = math.max(math.abs(o.x - x), math.abs(o.y - y))
    val step  = Point(math.signum(o.x - x), math.signum(o.y - y))
    (0 to steps).toVector.map(i => this + step * i)

  override def toString: String = s"($x,$y)"

object Point:
  val Origin: Point = Point(0, 0)

  val Up: Point    = Point(0, -1)
  val Down: Point  = Point(0, 1)
  val Left: Point  = Point(-1, 0)
  val Right: Point = Point(1, 0)

  /** Em sentido horario a partir de cima. */
  val Dirs4: Vector[Point] = Vector(Up, Right, Down, Left)

  val Dirs8: Vector[Point] =
    (for
      dy <- -1 to 1
      dx <- -1 to 1
      if dx != 0 || dy != 0
    yield Point(dx, dy)).toVector

  /** Aceita as letras usuais dos puzzles: U/D/L/R, N/S/W/E, ^v<>. */
  def direction(c: Char): Option[Point] = c.toUpper match
    case 'U' | 'N' | '^' => Some(Up)
    case 'D' | 'S' | 'v' | 'V' => Some(Down)
    case 'L' | 'W' | '<' => Some(Left)
    case 'R' | 'E' | '>' => Some(Right)
    case _               => None
