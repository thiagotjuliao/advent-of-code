package aoc.y2015.core.circuit

enum Operand:
  case Signal(value: Int)
  case Wire(name: String)

  def wires: Set[String] =
    this match
      case Wire(n) => Set(n)
      case Signal(_) => Set()

  def valueIn(s: Circuit.State): Option[Int] =
    this match
      case Operand.Signal(v) => Some(v)
      case Operand.Wire(w) => s.get(w)

object Operand:
  def parse(s: String): Operand =
    s.toIntOption.fold(Wire(s))(Signal.apply)

enum Instruction:
  case Direct(in: Operand, out: String)
  case And(a: Operand, b: Operand, out: String)
  case Or(a: Operand, b: Operand, out: String)
  case Neg(in: Operand, out: String)
  case LeftShift(in: Operand, n: Int, out: String)
  case RightShift(in: Operand, n: Int, out: String)

  def inlets: Set[String] =
    this match
      case Direct(i, _) => i.wires
      case And(a, b, _) => a.wires ++ b.wires
      case Or(a, b, _) => a.wires ++ b.wires
      case Neg(i, _) => i.wires
      case LeftShift(i, _, _) => i.wires
      case RightShift(i, _, _) => i.wires

  def outlet: String =
    this match
      case Direct(_, o) => o
      case And(_, _, o) => o
      case Or(_, _, o) => o
      case Neg(_, o) => o
      case LeftShift(_, _, o) => o
      case RightShift(_, _, o) => o

  def apply(s: Circuit.State): Option[Circuit.State] =
    val v = this match
      case Direct(in, _) =>
        in.valueIn(s)

      case And(a, b, _) =>
        a.valueIn(s).zip(b.valueIn(s)).map(_ & _)

      case Or(a, b, _) =>
        a.valueIn(s).zip(b.valueIn(s)).map(_ | _)

      case Neg(in, _) =>
        in.valueIn(s).map(~_)

      case LeftShift(in, n, _) =>
        in.valueIn(s).map(_ << n)

      case RightShift(in, n, _) =>
        in.valueIn(s).map(_ >> n)
    v.map(Circuit.set(s, outlet, _))
end Instruction

object Instruction:
  private val DirectSignal = "(\\w+) -> (\\w+)".r
  private val AndSignal = "(\\w+) AND (\\w+) -> (\\w+)".r
  private val OrSignal = "(\\w+) OR (\\w+) -> (\\w+)".r
  private val NegateSignal = "NOT (\\w+) -> (\\w+)".r
  private val LeftShiftSignal = "(\\w+) LSHIFT (\\d+) -> (\\w+)".r
  private val RightShiftSignal = "(\\w+) RSHIFT (\\d+) -> (\\w+)".r

  def parse(s: String): Instruction =
    s match
      case DirectSignal(in, out) => Direct(Operand.parse(in), out)
      case AndSignal(a, b, out) => And(Operand.parse(a), Operand.parse(b), out)
      case OrSignal(a, b, out) => Or(Operand.parse(a), Operand.parse(b), out)
      case NegateSignal(in, out) => Neg(Operand.parse(in), out)
      case LeftShiftSignal(in, n, out) => LeftShift(Operand.parse(in), n.toInt, out)
      case RightShiftSignal(in, n, out) => RightShift(Operand.parse(in), n.toInt, out)
