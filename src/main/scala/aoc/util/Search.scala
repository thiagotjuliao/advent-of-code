package aoc.util

import scala.collection.mutable

/** Buscas em grafo que aparecem todo ano. O grafo e dado pela funcao de
  * sucessores, entao serve para grid, estado de maquina, o que for.
  */
object Search:

  /** PriorityQueue e max-heap; invertendo a ordem viramos min-heap por custo. */
  private def minHeap[S]: Ordering[(Long, S)] = Ordering.by[(Long, S), Long](_._1).reverse

  /** Distancias (em numero de passos) de `start` ate tudo que for alcancavel. */
  def bfs[S](start: S)(next: S => IterableOnce[S]): Map[S, Int] =
    bfsAll(Seq(start))(next)

  def bfsAll[S](starts: IterableOnce[S])(next: S => IterableOnce[S]): Map[S, Int] =
    val dist = mutable.HashMap.empty[S, Int]
    val queue = mutable.Queue.empty[S]
    starts.iterator.foreach { s =>
      if dist.put(s, 0).isEmpty then queue.enqueue(s)
    }
    while queue.nonEmpty do
      val cur = queue.dequeue()
      val d = dist(cur) + 1
      next(cur).iterator.foreach { n =>
        if dist.put(n, d).isEmpty then queue.enqueue(n)
      }
    dist.toMap

  /** Menor caminho (a sequencia de estados) de `start` ate o primeiro estado
    * que satisfaca `goal`; `None` se nao houver caminho.
    */
  def bfsPath[S](start: S, goal: S => Boolean)(next: S => IterableOnce[S]): Option[Vector[S]] =
    val parent = mutable.HashMap[S, Option[S]](start -> None)
    val queue = mutable.Queue(start)
    var found = Option.empty[S]
    while queue.nonEmpty && found.isEmpty do
      val cur = queue.dequeue()
      if goal(cur) then found = Some(cur)
      else
        next(cur).iterator.foreach { n =>
          if parent.put(n, Some(cur)).isEmpty then queue.enqueue(n)
        }
    found.map { end =>
      Iterator
        .iterate(Option(end))(_.flatMap(parent))
        .takeWhile(_.isDefined)
        .flatten
        .toVector
        .reverse
    }

  /** Dijkstra: custos minimos de `start`. `next` devolve (vizinho, custo do passo). */
  def dijkstra[S](start: S)(next: S => IterableOnce[(S, Long)]): Map[S, Long] =
    val best = mutable.HashMap(start -> 0L)
    val queue = mutable.PriorityQueue(0L -> start)(using minHeap[S])
    while queue.nonEmpty do
      val (cost, cur) = queue.dequeue()
      if cost <= best.getOrElse(cur, Long.MaxValue) then
        next(cur).iterator.foreach { (n, step) =>
          val candidate = cost + step
          if candidate < best.getOrElse(n, Long.MaxValue) then
            best(n) = candidate
            queue.enqueue(candidate -> n)
        }
    best.toMap

  /** Dijkstra que para assim que alcanca o objetivo. */
  def dijkstraTo[S](start: S, goal: S => Boolean)(
      next: S => IterableOnce[(S, Long)]
  ): Option[Long] =
    val best = mutable.HashMap(start -> 0L)
    val queue = mutable.PriorityQueue(0L -> start)(using minHeap[S])
    var answer = Option.empty[Long]
    while queue.nonEmpty && answer.isEmpty do
      val (cost, cur) = queue.dequeue()
      if goal(cur) then answer = Some(cost)
      else if cost <= best.getOrElse(cur, Long.MaxValue) then
        next(cur).iterator.foreach { (n, step) =>
          val candidate = cost + step
          if candidate < best.getOrElse(n, Long.MaxValue) then
            best(n) = candidate
            queue.enqueue(candidate -> n)
        }
    answer
end Search
