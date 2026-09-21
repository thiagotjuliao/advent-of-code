package aoc

import com.sun.management.ThreadMXBean
import java.lang.management.ManagementFactory
import java.util.Locale

/** What running a part cost: wall-clock time, and bytes allocated.
  *
  * The bytes are what the part allocated in total, not what it held at once. A
  * solution that churns through 2 GiB while never holding more than a kilobyte
  * reports 2 GiB, and that is the useful reading: it is the number the garbage
  * collector has to work for, and unlike the milliseconds it is exact and
  * repeats run to run.
  *
  * It has one blind spot. HotSpot charges every allocation to the thread that
  * made it, and that per-thread counter is what this reads — so whatever a
  * solution allocates on some other thread is invisible here.
  */
final case class Cost(millis: Double, bytes: Long):

  def +(other: Cost): Cost = Cost(millis + other.millis, bytes + other.bytes)

  /** "12.3 ms, 1.2 MiB", or just "12.3 ms" where the JVM cannot count bytes. */
  def render: String =
    val time = String.format(Locale.ROOT, "%.1f ms", millis)
    if Cost.countsBytes then s"$time, ${Cost.humanBytes(bytes)}" else time

object Cost:

  val zero: Cost = Cost(0.0, 0L)

  /** Runs `body` and reports what it cost. */
  def of[A](body: => A): (A, Cost) =
    val allocatedBefore = allocated
    val started = System.nanoTime()
    val value = body
    val millis = (System.nanoTime() - started) / 1e6
    (value, Cost(millis, allocated - allocatedBefore))

  /** False outside HotSpot, where there is no per-thread accounting to read.
    *
    * A `def`, not a `val`: `bean` is initialized below this line, and a `val`
    * reading it here would read it as null.
    */
  def countsBytes: Boolean = bean.isDefined

  private val bean: Option[ThreadMXBean] =
    ManagementFactory.getThreadMXBean match
      case b: ThreadMXBean if b.isThreadAllocatedMemorySupported =>
        b.setThreadAllocatedMemoryEnabled(true)
        Some(b)
      case _ => None

  private def allocated: Long =
    bean.fold(0L)(_.getThreadAllocatedBytes(Thread.currentThread().threadId()))

  private val Units = Vector("B", "KiB", "MiB", "GiB", "TiB")

  private def humanBytes(bytes: Long): String =
    val exp =
      if bytes < 1024 then 0
      else math.min((math.log(bytes.toDouble) / math.log(1024)).toInt, Units.size - 1)
    if exp == 0 then s"$bytes B"
    else String.format(Locale.ROOT, "%.1f %s", bytes / math.pow(1024, exp), Units(exp))
end Cost
