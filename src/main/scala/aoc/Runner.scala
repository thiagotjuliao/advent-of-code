package aoc

import java.time.{LocalDate, ZoneId}
import scala.util.{Failure, Success, Try}

/** CLI do repo.
  *
  * {{{
  * sbt run                 # em dezembro, roda o puzzle de hoje
  * sbt "run 2015 1"        # um dia
  * sbt "run 2015"          # todos os dias implementados do ano
  * sbt "run all"           # tudo
  * sbt "run list"          # o que ja esta implementado
  * }}}
  */
object Runner:

  /** O AoC libera os puzzles a meia-noite de EST (UTC-5). */
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
      println("Fora da temporada (o AoC roda de 1 a 25 de dezembro).")
      usage()
      list()

  private def runOne(year: Int, day: Int): Unit =
    Solutions.find(year, day) match
      case None =>
        println(
          s"Nao existe aoc.y$year.Day${f"$day%02d"} — crie com: ./scripts/new-day.sh $year $day"
        )
      case Some(solution) if !Solutions.isImplemented(solution) =>
        println(
          s"${solution.label}: ainda e um stub — implemente em " +
            f"src/main/scala/aoc/y$year%04d/Day$day%02d.scala"
        )
      case Some(solution) => run(solution)

  private def runMany(solutions: Seq[Solution]): Unit =
    if solutions.isEmpty then println("Nada implementado ainda por aqui.")
    else
      val total = solutions.map(run).sum
      println(f"%n= total: $total%.1f ms em ${solutions.size} dia(s)")

  private def run(solution: Solution): Double =
    println(s"${Console.BOLD}── ${solution.label} ──${Console.RESET}")
    AocInput.load(solution.year, solution.day) match
      case Left(error) =>
        println(s"  ${Console.YELLOW}sem input: $error${Console.RESET}")
        0.0
      case Right(input) =>
        val parts = Seq[(Int, Input => Any)](
          1 -> (in => solution.part1(in)),
          2 -> (in => solution.part2(in))
        )
        parts.map((part, compute) => runPart(solution, part, compute, input)).sum

  private def runPart(solution: Solution, part: Int, compute: Input => Any, input: Input): Double =
    if !solution.solved(part) then
      println(s"  parte $part: —")
      0.0
    else
      val started = System.nanoTime()
      val outcome = Try(compute(input))
      val millis = (System.nanoTime() - started) / 1e6
      outcome match
        case Success(value) =>
          val padded = String.format("%-26s", String.valueOf(value))
          println(f"  parte $part: ${Console.GREEN}$padded${Console.RESET} ($millis%.1f ms)")
        case Failure(error) =>
          val what = s"${error.getClass.getSimpleName}: ${error.getMessage}"
          println(s"  parte $part: ${Console.RED}$what${Console.RESET}")
      millis

  /** Modo usado pelo `finish-day.sh`: roda as duas partes e sai com codigo 1
    * se qualquer uma faltar, estourar ou ficar sem input. Nada de tag em dia
    * meio resolvido.
    */
  private def check(year: Int, day: Int): Unit =
    val verdict = Solutions.find(year, day) match
      case None =>
        Left(s"nao existe aoc.y$year.Day${f"$day%02d"}")
      case Some(solution) if !solution.solved(1) || !solution.solved(2) =>
        Left(s"${solution.label}: falta implementar a parte ${if solution.solved(1) then 2 else 1}")
      case Some(solution) =>
        AocInput.load(year, day).flatMap { input =>
          val parts = Seq[(Int, Input => Any)](
            1 -> (in => solution.part1(in)),
            2 -> (in => solution.part2(in))
          )
          val outcomes = parts.map((part, compute) => part -> Try(compute(input)))
          outcomes
            .collectFirst { case (part, Failure(e)) =>
              s"${solution.label}: parte $part estourou — ${e.getClass.getSimpleName}: ${e.getMessage}"
            }
            .toLeft(outcomes.collect { case (part, Success(v)) => part -> v })
        }

    verdict match
      case Right(answers) =>
        answers.foreach((part, value) => println(f"  parte $part: $value"))
        println(s"${Console.GREEN}ok: $year dia ${f"$day%02d"} completo${Console.RESET}")
      case Left(reason) =>
        println(s"${Console.RED}falhou: $reason${Console.RESET}")
        sys.exit(1)
  end check

  private def list(): Unit =
    val done = Solutions.implemented
    if done.isEmpty then println("Nenhuma solucao implementada ainda.")
    else
      println(s"${done.size} dia(s) implementado(s):")
      done.groupBy(_.year).toVector.sortBy(_._1).foreach { (year, solutions) =>
        val marks = solutions.sortBy(_.day).map { s =>
          val stars = (if s.solved(1) then "*" else "") + (if s.solved(2) then "*" else "")
          f"${s.day}%02d$stars"
        }
        println(s"  $year: ${marks.mkString(" ")}")
      }

  private def usage(): Unit =
    println("""
      |uso: sbt "run [args]"
      |  (sem args)     puzzle de hoje, se for dezembro
      |  <ano> <dia>    roda um dia            ex: sbt "run 2015 1"
      |  <ano>          roda o ano inteiro     ex: sbt "run 2024"
      |  all            roda tudo
      |  list           lista o que esta implementado
      |  check <ano> <dia>  verifica as duas partes e sai com erro se faltar algo
      |""".stripMargin.trim)

  private def withInt(s: String)(f: Int => Unit): Unit =
    s.toIntOption.fold(println(s"'$s' nao e um numero"))(f)

  private def withInt(a: String, b: String)(f: (Int, Int) => Unit): Unit =
    (a.toIntOption, b.toIntOption) match
      case (Some(x), Some(y)) => f(x, y)
      case _ => println(s"argumentos invalidos: $a $b"); usage()
end Runner
