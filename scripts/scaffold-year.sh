#!/usr/bin/env bash
# Cria os 25 stubs de um ano (sem testes e sem baixar inputs).
#   uso: ./scripts/scaffold-year.sh 2026
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
YEAR="${1:?uso: scaffold-year.sh <ano>}"
for day in $(seq 1 25); do
  "$ROOT/scripts/new-day.sh" "$YEAR" "$day" --no-input --no-test
done
echo
echo "lembre de adicionar $YEAR em Solutions.years"
