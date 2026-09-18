#!/usr/bin/env bash
# Downloads one day's input into inputs/YYYY/dayDD.txt (never re-downloads).
#   usage: ./scripts/fetch-input.sh 2015 1
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
YEAR="${1:?usage: fetch-input.sh <year> <day>}"
DAY="${2:?usage: fetch-input.sh <year> <day>}"
DD="$(printf '%02d' "$DAY")"
OUT="$ROOT/inputs/$YEAR/day$DD.txt"

if [[ -s "$OUT" ]]; then
  echo "input already there: ${OUT#"$ROOT/"}"
  exit 0
fi

# .env loses to whatever is already in the environment
if [[ -f "$ROOT/.env" ]]; then
  set -a; # shellcheck disable=SC1091
  source "$ROOT/.env"; set +a
fi

if [[ -z "${AOC_SESSION:-}" ]]; then
  echo "AOC_SESSION is not set — copy .env.example to .env and fill it in." >&2
  exit 1
fi

mkdir -p "$(dirname "$OUT")"
# The contact in the User-Agent is what the AoC automation rules ask for.
status=$(curl -sS -w '%{http_code}' -o "$OUT.tmp" \
  -H "Cookie: session=$AOC_SESSION" \
  -H "User-Agent: github.com/advent-of-code scala runner (contact: ${AOC_CONTACT:-unknown})" \
  "https://adventofcode.com/$YEAR/day/$DAY/input")

if [[ "$status" != "200" ]]; then
  echo "HTTP $status while downloading $YEAR/$DAY:" >&2
  head -c 300 "$OUT.tmp" >&2; echo >&2
  rm -f "$OUT.tmp"
  exit 1
fi

mv "$OUT.tmp" "$OUT"
echo "saved to ${OUT#"$ROOT/"} ($(wc -l < "$OUT" | tr -d ' ') lines)"
