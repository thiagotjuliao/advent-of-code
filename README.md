# Advent of Code — Scala 3

[Advent of Code](https://adventofcode.com) solutions for **2015 through 2025**, in Scala 3.9.0.

One sbt module, one object per day, discovery by reflection (no registry to
maintain) and inputs downloaded on demand.

## Getting started

```bash
cp .env.example .env     # and paste your adventofcode.com session cookie
sbt test                 # compiles everything and runs the core suite
sbt "run list"           # shows what is implemented so far
```

## Running

| command | what it does |
| --- | --- |
| `sbt "run 2015 1"` | runs one day |
| `sbt "run 2024"` | runs every implemented day of a year |
| `sbt "run all"` | runs everything |
| `sbt run` | in December, runs today's puzzle |
| `sbt "run list"` | lists what is implemented |
| `sbt "run check 2015 1"` | runs both parts and exits with an error if either is missing |
| `sbt "testOnly aoc.y2015.Day01Suite"` | runs one day's tests |

Each part prints its runtime next to the answer — handy for knowing when the
part 2 solution has to stop being brute force.

## Starting a new day

```bash
./scripts/new-day.sh 2024 7
```

```powershell
./scripts/new-day.ps1 2024 7
```

Every script in `scripts/` comes in two flavours: `.sh` for bash (macOS, Linux,
Git Bash) and `.ps1` for PowerShell 7+ on Windows. They do the same thing and
take the same arguments; only the flags change shape — `--no-input` becomes
`-NoInput`, `-t "Title"` becomes `-Title "Title"`.

That creates `src/main/scala/aoc/y2024/Day07.scala`, its test at
`src/test/scala/aoc/y2024/Day07Suite.scala`, and downloads
`inputs/2024/day07.txt`.

The file starts as a stub (`object Day07 extends Solution(2024, 7)`); register
the parts to solve it:

```scala
object Day07 extends Solution(2024, 7):
  part1 { in => in.lines.size }          // Int
  part2 { in => in.words.mkString(",") } // String
```

Each closure is type-checked where it is written and its result type is
inferred — no solution ever names `Any`. The type is only erased at the very
edge, in `Solution.solve`, because all the runner does with an answer is print
it, and one day's part returns an `Int` where the next returns a `String`.

The runner picks the new day up on its own — there is no registry to update.
A day that registers nothing stays marked as unimplemented and is skipped by
`run <year>` and `run all`.

Once the AoC 2026 is out: `./scripts/scaffold-year.sh 2026` (or
`./scripts/scaffold-year.ps1 2026`), then add the year to `Solutions.years`.

## Closing a day

Once both parts have earned their stars:

```bash
./scripts/finish-day.sh 2015 1
```

```powershell
./scripts/finish-day.ps1 2015 1
```

The script refuses to close a half-solved day. Before touching git it runs
`scalafmtCheckAll`, the whole suite, and `run check <year> <day>` — which
executes both parts against your real input. If anything fails, there is no
commit and no tag.

Passing, it commits the solution (and the test) and creates an **annotated tag**:

| | |
| --- | --- |
| tag | `y2015-d01` |
| tag subject | `AoC 2015 day 01 — Not Quite Lisp` |
| commit | `2015 day 01 — Not Quite Lisp` |

The title comes from the file's own scaladoc
(`/** https://adventofcode.com/... — Title */`); `-t "Another title"` overrides
it. Other options: `--all` (also commits whatever else is modified),
`--no-push`, `--no-verify` — in PowerShell, `-Title`, `-All`, `-NoPush` and
`-NoVerify`.

Finally it runs `git push --follow-tags`, so the commit and the tag travel together.

For the scoreboard: `git tag -l 'y2015*'`.

## Layout

```
build.sbt                     sbt 2.0.8, Scala 3.9.0, munit
project/plugins.sbt           sbt-scalafmt
.scalafmt.conf                formatting contract (the same as the other Scala repos)
.vscode/                      settings + recommended extensions (Metals, Docs View)
scripts/
  new-day.{sh,ps1}            creates a day's solution + test + input
  fetch-input.{sh,ps1}        downloads just the input
  scaffold-year.{sh,ps1}      creates the 25 stubs of a new year
  finish-day.{sh,ps1}         verifies, commits, tags and pushes a day
inputs/YYYY/dayDD.txt         inputs (git-ignored on purpose)
src/main/scala/aoc/
  Solution.scala              base class: registers and runs a day's parts
  Input.scala                 the puzzle text, parsed N ways
  Solutions.scala             discovery by reflection
  AocInput.scala              disk reads + cached downloads
  Runner.scala                the CLI
  yYYYY/DayDD.scala           one solution per file
src/test/scala/aoc/           munit; the puzzle examples as tests
```

## What comes with it

**`Input`** — the puzzle text, parsed the usual ways: `text`, `lines`,
`blocks` (split on blank lines), `ints`, `longs`, `intLines`, `words`, `chars`,
`splitOn(sep)`, `captures(regex)`.

That is the whole toolbox on purpose. Grids, points, graph searches and modular
arithmetic get written when a day actually asks for them.

## Formatting and editor

`.scalafmt.conf` is the same contract the other Scala repos use: Scala 3
indentation syntax, `maxColumn = 100`, no vertical alignment (aligning produces
diffs on lines nobody touched) and an `end` marker from 25 lines up.

```bash
sbt scalafmtAll        # formats main + test
sbt scalafmtCheckAll   # only checks — the gate before committing
```

In VS Code, `.vscode/settings.json` turns on format-on-save through Metals
(scoped to `[scala]`, so it will not reflow the Markdown), imports the build
without a prompt (`metals.autoImportBuilds: "all"`), puts the ruler at 100 and
makes hovers sticky. `.vscode/extensions.json` recommends Metals, Docs View and
Better Comments.

## Tests

The examples from the puzzle text become tests, with
[munit](https://scalameta.org/munit/). `solve(part, input)` is how a test runs
a registered part:

```scala
class Day01Suite extends FunSuite:
  test("part 1: the examples from the puzzle text") {
    assertEquals(Day01.solve(1, Input("(())")), 0)
  }
```

The tests generated by the new-day script start out with `.ignore` — drop it once you
have pasted the day's example in.

## About inputs and puzzle texts

`inputs/` is in `.gitignore` because the AoC asks that inputs and puzzle texts
are not redistributed — only the solutions. Downloading uses your session
cookie (from `.env`, also git-ignored), keeps the file on disk and never
re-downloads the same day, following the event's
[automation rules](https://www.reddit.com/r/adventofcode/wiki/faqs/automation),
`User-Agent` with a contact included.
