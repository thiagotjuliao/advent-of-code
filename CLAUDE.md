# Working in this repository

Advent of Code solutions, 2015 through 2025, in Scala 3: one sbt module, one
object per day discovered by reflection, inputs downloaded on demand. The
README has the full tour.

How code and prose are written here is shared with every project built from
project-templates, in `CONVENTIONS.md`. Where it and this file disagree, this
file wins.

@CONVENTIONS.md

## Commands

| command | |
| --- | --- |
| `sbt verify` | formatting and every suite — the gate, and what CI runs |
| `sbt "run 2015 1"` | one day; `sbt "run list"` shows what is implemented |
| `./scripts/new-day.sh 2024 7` | scaffold a day: solution, suite and input |

`verify` runs `testFull`, not `test`: under sbt 2 `test` is incremental and its
greenest possible run is the one that ran nothing.

## This project

- Inputs and puzzle texts are personal, and Advent of Code asks that they are
  not redistributed: they stay out of git (`inputs/**/*.txt`), and so does
  `.env`, which holds the session cookie.
- Each part prints its wall-clock time and the bytes it allocated. The bytes
  are exact and repeat run to run, so they are the signal that a solution has
  to stop being brute force.

## Reviewing solutions

The solutions are the author's own; the point is to get better at solving
these problems. When a solution is finished or shown for review, assess it
candidly:

- **Say whether it was the best approach**, and if not, name the better one
  and why: the algorithm or data structure, its complexity against the
  solution's, and what about the puzzle makes it fit. If the solution is
  already the right one, say so and stop; do not invent an improvement.
- **Back it with measurements.** Run the day and use the time and bytes it
  prints; a claim that an alternative is faster or lighter is measured or
  labelled as an estimate.
- **Rank the tips**, what would have made the solution more viable first:
  approach, then idiomatic Scala, then style.
- **Describe, do not apply.** Leave the code as it is unless asked to change it.
