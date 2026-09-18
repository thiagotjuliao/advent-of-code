package aoc

import java.net.URI
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import java.nio.file.{Files, Path}
import scala.jdk.CollectionConverters.*

/** Reads the puzzle inputs, downloading them on demand.
  *
  * Files live in `inputs/YYYY/dayDD.txt` and are git-ignored on purpose: the
  * AoC asks that inputs and puzzle texts are not redistributed. Once
  * downloaded a file is reused — no hitting the server on every run.
  */
object AocInput:

  private val root: Path = Path.of(sys.env.getOrElse("AOC_INPUT_DIR", "inputs"))

  def path(year: Int, day: Int): Path =
    root.resolve(f"$year%04d").resolve(f"day$day%02d.txt")

  /** Reads from disk; if it is missing and `AOC_SESSION` is set, downloads and saves it. */
  def load(year: Int, day: Int): Either[String, Input] =
    val file = path(year, day)
    if Files.exists(file) && Files.size(file) > 0 then Right(Input(Files.readString(file)))
    else
      download(year, day).map { body =>
        Files.createDirectories(file.getParent)
        Files.writeString(file, body)
        Input(body)
      }

  def download(year: Int, day: Int): Either[String, String] =
    config("AOC_SESSION").filter(_.nonEmpty) match
      case None =>
        Left(
          s"no input at ${path(year, day)} and AOC_SESSION is not set " +
            "(copy .env.example to .env and fill it in, or download the file by hand)"
        )
      case Some(session) =>
        val contact = config("AOC_CONTACT").getOrElse("unknown contact")
        val request = HttpRequest
          .newBuilder(URI.create(s"https://adventofcode.com/$year/day/$day/input"))
          .header("Cookie", s"session=$session")
          .header("User-Agent", s"scala-aoc-runner (contact: $contact)")
          .GET()
          .build()
        try
          val response = client.send(request, HttpResponse.BodyHandlers.ofString)
          response.statusCode match
            case 200 => Right(response.body)
            case 400 => Left("HTTP 400: session cookie invalid or expired — refresh AOC_SESSION")
            case 404 => Left(s"HTTP 404: puzzle $year/$day has not unlocked yet")
            case c => Left(s"HTTP $c while downloading the input for $year/$day")
        catch case e: Exception => Left(s"network failure: ${e.getMessage}")

  private lazy val client: HttpClient =
    HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build()

  /** Environment variable, falling back to the `.env` file at the repo root. */
  private def config(key: String): Option[String] =
    sys.env.get(key).orElse(dotenv.get(key))

  private lazy val dotenv: Map[String, String] =
    val file = Path.of(".env")
    if !Files.exists(file) then Map.empty
    else
      Files
        .readAllLines(file)
        .asScala
        .map(_.trim)
        .filter(l => l.nonEmpty && !l.startsWith("#") && l.contains("="))
        .map { line =>
          val Array(k, v) = line.split("=", 2)
          k.trim -> v.trim.stripPrefix("\"").stripSuffix("\"")
        }
        .toMap
end AocInput
