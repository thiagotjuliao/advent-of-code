#!/usr/bin/env bash
# Creates the solution file (and its test) for a day and downloads the input.
#   usage: ./scripts/new-day.sh 2015 1 [--no-input] [--no-test]
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
YEAR="${1:?usage: new-day.sh <year> <day> [--no-input] [--no-test]}"
DAY="${2:?usage: new-day.sh <year> <day> [--no-input] [--no-test]}"
shift 2
WANT_INPUT=1; WANT_TEST=1
for flag in "$@"; do
  case "$flag" in
    --no-input) WANT_INPUT=0 ;;
    --no-test)  WANT_TEST=0 ;;
    *) echo "unknown flag: $flag" >&2; exit 2 ;;
  esac
done

DD="$(printf '%02d' "$DAY")"
SRC="$ROOT/src/main/scala/aoc/y$YEAR/Day$DD.scala"
TEST="$ROOT/src/test/scala/aoc/y$YEAR/Day${DD}Suite.scala"

mkdir -p "$(dirname "$SRC")"
if [[ -f "$SRC" ]]; then
  echo "already there: ${SRC#"$ROOT/"}"
else
  cat > "$SRC" <<EOF
package aoc.y$YEAR

import aoc.Solution

/** https://adventofcode.com/$YEAR/day/$DAY
  *
  * To solve it, override the parts:
  *   override def part1(in: aoc.Input): Any = in.lines.size
  */
object Day$DD extends Solution($YEAR, $DAY)
EOF
  echo "created: ${SRC#"$ROOT/"}"
fi

if [[ $WANT_TEST -eq 1 && ! -f "$TEST" ]]; then
  mkdir -p "$(dirname "$TEST")"
  cat > "$TEST" <<EOF
package aoc.y$YEAR

import aoc.Input
import munit.FunSuite

class Day${DD}Suite extends FunSuite:

  private val sample = Input("""
    |paste the example from the puzzle text here
  """.stripMargin.trim)

  test("part 1".ignore) {
    assertEquals(Day$DD.part1(sample), ???)
  }

  test("part 2".ignore) {
    assertEquals(Day$DD.part2(sample), ???)
  }
EOF
  echo "created: ${TEST#"$ROOT/"}"
fi

if [[ $WANT_INPUT -eq 1 ]]; then
  "$ROOT/scripts/fetch-input.sh" "$YEAR" "$DAY" || true
fi

echo
echo "to run it:  sbt \"run $YEAR $DAY\""
