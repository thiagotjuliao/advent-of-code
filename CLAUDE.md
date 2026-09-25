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
| `sbt testFull` | every suite — the gate |
| `sbt "run 2015 1"` | one day; `sbt "run list"` shows what is implemented |
| `./scripts/new-day.sh 2024 7` | scaffold a day: solution, suite and input |

`testFull`, not `test`: under sbt 2 `test` is incremental and its greenest
possible run is the one that ran nothing.

## This project

- Inputs and puzzle texts are personal, and Advent of Code asks that they are
  not redistributed: they stay out of git (`inputs/**/*.txt`), and so does
  `.env`, which holds the session cookie.
- Each part prints its wall-clock time and the bytes it allocated. The bytes
  are exact and repeat run to run, so they are the signal that a solution has
  to stop being brute force.
