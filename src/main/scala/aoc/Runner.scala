package aoc

import java.time.{LocalDate, ZoneId}
import scala.util.{Failure, Success, Try}

/** The repo's CLI.
  *
  * {{{
  * sbt run                 # in December, runs today's puzzle
  * sbt "run 2015 1"        # one day
  * sbt "run 2015"          # every implemented day of that year
  * sbt "run all"           # everything
  * sbt "run list"          # what is implemented so far
  * }}}
  */
object Runner:

  /** The AoC unlocks its puzzles at midnight EST (UTC-5). */
  private val AocZone = ZoneId.of("America/New_York")

  def main(args: Array[String]): Unit =
    args.toList match
      case Nil => runToday()
      case ("list" | "--list" | "-l") :: _ => list()
      case ("help" | "--help" | "-h") :: _ => usage()
      case "all" :: _ => runMany(Solutions.implemented)
      case "check" :: year :: day :: _ => withInt(year, day)(check)
      case year :: Nil => withInt(year)(y => runMany(Solutions.implementedOfYear(y)))
      case year :: day :: _ => withInt(year, day)((y, d) => runOne(y, d))

  private def runToday(): Unit =
    val today = LocalDate.now(AocZone)
    if today.getMonthValue == 12 && today.getDayOfMonth <= 25 then
      runOne(today.getYear, today.getDayOfMonth)
    else
      println("Off season (the AoC runs from December 1st to the 25th).")
      usage()
      list()

  private def runOne(year: Int, day: Int): Unit =
    Solutions.find(year, day) match
      case None =>
        println(
          s"no aoc.y$year.Day${f"$day%02d"} — create it with: ./scripts/new-day.sh $year $day"
        )
      case Some(solution) if !Solutions.isImplemented(solution) =>
        println(
          s"${solution.label}: still a stub — implement it in " +
            f"src/main/scala/aoc/y$year%04d/Day$day%02d.scala"
        )
      case Some(solution) => run(solution)

  private def runMany(solutions: Seq[Solution]): Unit =
    if solutions.isEmpty then println("Nothing implemented here yet.")
    else
      val total = solutions.map(run).sum
      println(f"%n= total: $total%.1f ms across ${solutions.size} day(s)")

  private def run(solution: Solution): Double =
    println(s"${Console.BOLD}── ${solution.label} ──${Console.RESET}")
    AocInput.load(solution.year, solution.day) match
      case Left(error) =>
        println(s"  ${Console.YELLOW}no input: $error${Console.RESET}")
        0.0
      case Right(input) =>
        Seq(1, 2).map(part => runPart(solution, part, input)).sum

  private def runPart(solution: Solution, part: Int, input: Input): Double =
    if !solution.solved(part) then
      println(s"  part $part: —")
      0.0
    else
      val started = System.nanoTime()
      val outcome = Try(solution.solve(part, input))
      val millis = (System.nanoTime() - started) / 1e6
      outcome match
        case Success(value) =>
          val padded = String.format("%-26s", String.valueOf(value))
          println(f"  part $part: ${Console.GREEN}$padded${Console.RESET} ($millis%.1f ms)")
        case Failure(error) =>
          val what = s"${error.getClass.getSimpleName}: ${error.getMessage}"
          println(s"  part $part: ${Console.RED}$what${Console.RESET}")
      millis

  /** What `finish-day.sh` calls: runs both parts and exits 1 if either one is
    * missing, blows up, or has no input. No tag for a half-solved day.
    */
  private def check(year: Int, day: Int): Unit =
    val verdict = Solutions.find(year, day) match
      case None =>
        Left(s"no aoc.y$year.Day${f"$day%02d"}")
      case Some(solution) if !solution.solved(1) || !solution.solved(2) =>
        Left(s"${solution.label}: part ${if solution.solved(1) then 2 else 1} is not implemented")
      case Some(solution) =>
        AocInput.load(year, day).flatMap { input =>
          val outcomes = Seq(1, 2).map(part => part -> Try(solution.solve(part, input)))
          outcomes
            .collectFirst { case (part, Failure(e)) =>
              s"${solution.label}: part $part blew up — ${e.getClass.getSimpleName}: ${e.getMessage}"
            }
            .toLeft(outcomes.collect { case (part, Success(v)) => part -> v })
        }

    verdict match
      case Right(answers) =>
        answers.foreach((part, value) => println(f"  part $part: $value"))
        println(s"${Console.GREEN}ok: $year day ${f"$day%02d"} is complete${Console.RESET}")
      case Left(reason) =>
        println(s"${Console.RED}failed: $reason${Console.RESET}")
        sys.exit(1)
  end check

  private def list(): Unit =
    val done = Solutions.implemented
    if done.isEmpty then println("No solutions implemented yet.")
    else
      println(s"${done.size} day(s) implemented:")
      done.groupBy(_.year).toVector.sortBy(_._1).foreach { (year, solutions) =>
        val marks = solutions.sortBy(_.day).map { s =>
          val stars = (if s.solved(1) then "*" else "") + (if s.solved(2) then "*" else "")
          f"${s.day}%02d$stars"
        }
        println(s"  $year: ${marks.mkString(" ")}")
      }

  private def usage(): Unit =
    println("""
      |usage: sbt "run [args]"
      |  (no args)          today's puzzle, if it is December
      |  <year> <day>       runs one day            e.g. sbt "run 2015 1"
      |  <year>             runs the whole year     e.g. sbt "run 2024"
      |  all                runs everything
      |  list               lists what is implemented
      |  check <year> <day> verifies both parts and exits with an error if anything is missing
      |""".stripMargin.trim)

  private def withInt(s: String)(f: Int => Unit): Unit =
    s.toIntOption.fold(println(s"'$s' is not a number"))(f)

  private def withInt(a: String, b: String)(f: (Int, Int) => Unit): Unit =
    (a.toIntOption, b.toIntOption) match
      case (Some(x), Some(y)) => f(x, y)
      case _ => println(s"invalid arguments: $a $b"); usage()
end Runner
