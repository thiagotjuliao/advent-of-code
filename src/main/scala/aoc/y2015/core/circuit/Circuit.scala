package aoc.y2015.core.circuit

class Circuit private (booklet: Seq[Instruction]):
  def run(seed: Circuit.State = Circuit.emptyState): Circuit.State =
    booklet.foldLeft(seed)((s, i) => if seed.contains(i.outlet) then s else i(s).get)

object Circuit:
  type State = Map[String, Int]

  val emptyState: State = Map()

  def set(s: State, w: String, v: Int): State =
    s.updated(w, v & 0xffff)

  private def topo(
      q: Seq[Instruction],
      dg: Map[Instruction, Int],
      dp: Map[String, Seq[Instruction]],
      r: Vector[Instruction] = Vector()
  ): Seq[Instruction] =
    q match
      case Seq() => r
      case i +: rest =>
        val readers = dp.getOrElse(i.outlet, Seq())
        val dg_ = readers.foldLeft(dg)((acc, j) => acc.updatedWith(j)(_.map(_ - 1)))
        val ready = readers.filter(dg_(_) == 0)
        topo(rest ++ ready, dg_, dp, r :+ i)

  def build(instructions: Seq[Instruction]): Circuit =
    val q = instructions.filter(_.inlets.isEmpty)
    val dg = instructions.map(i => (i, i.inlets.size)).toMap

    val dp = instructions
      .flatMap(i => i.inlets.map(_ -> i))
      .groupMap(_._1)(_._2)

    val ordered = topo(q, dg, dp)
    require(
      ordered.size == instructions.size,
      s"${instructions.size - ordered.size} instructions unreachable"
    )
    Circuit(ordered)
end Circuit
