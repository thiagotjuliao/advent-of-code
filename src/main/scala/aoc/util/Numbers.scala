package aoc.util

/** Aritmetica que o AoC cobra nos dias de ciclo/periodo. */
object Numbers:

  def gcd(a: Long, b: Long): Long = if b == 0 then math.abs(a) else gcd(b, a % b)

  def lcm(a: Long, b: Long): Long = if a == 0 || b == 0 then 0 else math.abs(a / gcd(a, b) * b)

  def lcm(xs: IterableOnce[Long]): Long = xs.iterator.foldLeft(1L)(lcm)

  /** Teorema chines do resto: menor `x >= 0` com `x % mod_i == rem_i`.
    * Assume modulos coprimos dois a dois.
    */
  def crt(congruences: Seq[(Long, Long)]): Long =
    val product = congruences.map(_._2).product
    val total = congruences.foldLeft(BigInt(0)) { case (acc, (rem, mod)) =>
      val p = BigInt(product / mod)
      acc + BigInt(rem) * p.modInverse(BigInt(mod)) * p
    }
    total.mod(BigInt(product)).toLong

  /** Detecta ciclo em `f` a partir de `start`: (indice do inicio, tamanho do ciclo). */
  def findCycle[S](start: S)(f: S => S): (Int, Int) =
    val seen = scala.collection.mutable.HashMap(start -> 0)
    var cur = start
    var i = 0
    var result = Option.empty[(Int, Int)]
    while result.isEmpty do
      cur = f(cur)
      i += 1
      seen.get(cur) match
        case Some(first) => result = Some(first -> (i - first))
        case None => seen(cur) = i
    result.get
end Numbers
