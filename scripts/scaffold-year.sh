#!/usr/bin/env bash
# Creates the 25 stubs of a year (no tests, no input downloads).
#   usage: ./scripts/scaffold-year.sh 2026
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
YEAR="${1:?usage: scaffold-year.sh <year>}"
for day in $(seq 1 25); do
  "$ROOT/scripts/new-day.sh" "$YEAR" "$day" --no-input --no-test
done
echo
echo "remember to add $YEAR to Solutions.years"
