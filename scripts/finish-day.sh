#!/usr/bin/env bash
# Fecha um dia resolvido: verifica, commita e cria a tag anotada yYYYY-dDD.
#
#   uso: ./scripts/finish-day.sh <ano> <dia> [opcoes]
#
#   --no-verify      pula scalafmtCheckAll + test + run check (nao recomendado)
#   --no-push        commita e tagueia sem enviar para o remoto
#   --all            inclui no commit tudo que estiver modificado, nao so o dia
#   -t, --title TXT  titulo do puzzle (por padrao sai do scaladoc do arquivo)
#
# A tag so nasce depois que as duas partes rodam de verdade contra o seu input:
# dia meio resolvido nao vira tag.
set -euo pipefail
set -o pipefail

die() { echo "erro: $*" >&2; exit 1; }

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

YEAR="${1:-}"; DAY="${2:-}"
[[ -n "$YEAR" && -n "$DAY" ]] || die "uso: finish-day.sh <ano> <dia> [--no-verify] [--no-push] [--all] [-t titulo]"
shift 2

VERIFY=1; PUSH=1; ALL=0; TITLE=""
while [[ $# -gt 0 ]]; do
  case "$1" in
    --no-verify) VERIFY=0 ;;
    --no-push)   PUSH=0 ;;
    --all)       ALL=1 ;;
    -t|--title)  TITLE="${2:?-t precisa de um titulo}"; shift ;;
    *) die "flag desconhecida: $1" ;;
  esac
  shift
done

DD="$(printf '%02d' "$DAY")"
SRC="src/main/scala/aoc/y$YEAR/Day$DD.scala"
TEST="src/test/scala/aoc/y$YEAR/Day${DD}Suite.scala"
TAG="y$YEAR-d$DD"

[[ -f "$SRC" ]] || die "$SRC nao existe — comece com ./scripts/new-day.sh $YEAR $DAY"
git rev-parse -q --verify "refs/tags/$TAG" >/dev/null && die "a tag $TAG ja existe"

# Titulo: sai do scaladoc "/** https://adventofcode.com/2015/day/1 — Not Quite Lisp */"
if [[ -z "$TITLE" ]]; then
  TITLE="$(sed -n 's#.*adventofcode\.com/[0-9]*/day/[0-9]*[[:space:]]*—[[:space:]]*##p' "$SRC" \
    | head -1 | sed 's#[[:space:]]*\*/##' | sed 's#[[:space:]]*$##')"
fi
HEADLINE="$YEAR dia $DD"
[[ -n "$TITLE" ]] && HEADLINE="$HEADLINE — $TITLE"

if [[ $VERIFY -eq 1 ]]; then
  echo "verificando $HEADLINE ..."
  # O sed corta o stack trace interno do sbt quando o `run check` sai com erro:
  # o que importa e a linha "falhou: ..." logo acima dele.
  sbt -batch "scalafmtCheckAll; test; run check $YEAR $DAY" 2>&1 \
    | sed -e '/[[:space:]]at [A-Za-z_$][A-Za-z0-9_.$]*[.(]/d' \
          -e '/nonzero exit code returned from runner/d' \
          -e '/sbt server disconnected/d' \
    || die "verificacao falhou — nada foi commitado nem tagueado"
else
  echo "pulando a verificacao (--no-verify)"
fi

if [[ $ALL -eq 1 ]]; then
  git add -A
else
  git add -- "$SRC"
  [[ -f "$TEST" ]] && git add -- "$TEST"
  outros="$(git status --porcelain --untracked-files=all -- . ':!'"$SRC" ':!'"$TEST" | head -5)"
  if [[ -n "$outros" ]]; then
    echo
    echo "aviso: ficaram de fora do commit (use --all para incluir):"
    echo "$outros" | sed 's/^/  /'
    echo
  fi
fi

if git diff --cached --quiet; then
  echo "nada novo para commitar — vou apenas taguear o HEAD"
else
  git commit -q -m "$HEADLINE" -m "Partes 1 e 2 verificadas com: sbt \"run check $YEAR $DAY\""
  echo "commit: $(git log -1 --oneline)"
fi

git tag -a "$TAG" -m "AoC $HEADLINE"
echo "tag: $TAG"

if [[ $PUSH -eq 1 ]]; then
  if git remote get-url origin >/dev/null 2>&1; then
    branch="$(git branch --show-current)"
    git push --follow-tags origin "$branch"
    echo "push: origin/$branch (com a tag $TAG)"
  else
    echo "sem remoto configurado — commit e tag ficaram locais."
    echo "  git remote add origin git@github.com:<voce>/advent-of-code.git"
    echo "  git push -u --follow-tags origin $(git branch --show-current)"
  fi
fi
