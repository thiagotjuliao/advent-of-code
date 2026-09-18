#!/usr/bin/env bash
# Cria o arquivo de solucao (e o de teste) de um dia e baixa o input.
#   uso: ./scripts/new-day.sh 2015 1 [--no-input] [--no-test]
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
YEAR="${1:?uso: new-day.sh <ano> <dia> [--no-input] [--no-test]}"
DAY="${2:?uso: new-day.sh <ano> <dia> [--no-input] [--no-test]}"
shift 2
WANT_INPUT=1; WANT_TEST=1
for flag in "$@"; do
  case "$flag" in
    --no-input) WANT_INPUT=0 ;;
    --no-test)  WANT_TEST=0 ;;
    *) echo "flag desconhecida: $flag" >&2; exit 2 ;;
  esac
done

DD="$(printf '%02d' "$DAY")"
SRC="$ROOT/src/main/scala/aoc/y$YEAR/Day$DD.scala"
TEST="$ROOT/src/test/scala/aoc/y$YEAR/Day${DD}Suite.scala"

mkdir -p "$(dirname "$SRC")"
if [[ -f "$SRC" ]]; then
  echo "ja existe: ${SRC#"$ROOT/"}"
else
  cat > "$SRC" <<EOF
package aoc.y$YEAR

import aoc.Solution

/** https://adventofcode.com/$YEAR/day/$DAY
  *
  * Sobrescreva as partes para resolver:
  *   override def part1(in: aoc.Input): Any = in.lines.size
  */
object Day$DD extends Solution($YEAR, $DAY)
EOF
  echo "criado: ${SRC#"$ROOT/"}"
fi

if [[ $WANT_TEST -eq 1 && ! -f "$TEST" ]]; then
  mkdir -p "$(dirname "$TEST")"
  cat > "$TEST" <<EOF
package aoc.y$YEAR

import aoc.Input
import munit.FunSuite

class Day${DD}Suite extends FunSuite:

  private val sample = Input("""
    |cole aqui o exemplo do enunciado
  """.stripMargin.trim)

  test("parte 1".ignore) {
    assertEquals(Day$DD.part1(sample), ???)
  }

  test("parte 2".ignore) {
    assertEquals(Day$DD.part2(sample), ???)
  }
EOF
  echo "criado: ${TEST#"$ROOT/"}"
fi

if [[ $WANT_INPUT -eq 1 ]]; then
  "$ROOT/scripts/fetch-input.sh" "$YEAR" "$DAY" || true
fi

echo
echo "para rodar:  sbt \"run $YEAR $DAY\""
