#!/usr/bin/env bash
# Baixa o input de um dia para inputs/YYYY/dayDD.txt (nao re-baixa se ja existir).
#   uso: ./scripts/fetch-input.sh 2015 1
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
YEAR="${1:?uso: fetch-input.sh <ano> <dia>}"
DAY="${2:?uso: fetch-input.sh <ano> <dia>}"
DD="$(printf '%02d' "$DAY")"
OUT="$ROOT/inputs/$YEAR/day$DD.txt"

if [[ -s "$OUT" ]]; then
  echo "input ja existe: ${OUT#"$ROOT/"}"
  exit 0
fi

# .env tem prioridade menor que o ambiente
if [[ -f "$ROOT/.env" ]]; then
  set -a; # shellcheck disable=SC1091
  source "$ROOT/.env"; set +a
fi

if [[ -z "${AOC_SESSION:-}" ]]; then
  echo "AOC_SESSION nao configurado — copie .env.example para .env e preencha." >&2
  exit 1
fi

mkdir -p "$(dirname "$OUT")"
# O User-Agent com contato e pedido explicitamente pelas regras de automacao do AoC.
status=$(curl -sS -w '%{http_code}' -o "$OUT.tmp" \
  -H "Cookie: session=$AOC_SESSION" \
  -H "User-Agent: github.com/advent-of-code scala runner (contato: ${AOC_CONTACT:-desconhecido})" \
  "https://adventofcode.com/$YEAR/day/$DAY/input")

if [[ "$status" != "200" ]]; then
  echo "falha HTTP $status ao baixar $YEAR/$DAY:" >&2
  head -c 300 "$OUT.tmp" >&2; echo >&2
  rm -f "$OUT.tmp"
  exit 1
fi

mv "$OUT.tmp" "$OUT"
echo "salvo em ${OUT#"$ROOT/"} ($(wc -l < "$OUT" | tr -d ' ') linhas)"
