#!/usr/bin/env bash
# Closes a solved day: verifies it, commits it and creates the yYYYY-dDD tag.
#
#   usage: ./scripts/finish-day.sh <year> <day> [options]
#
#   --no-verify      skips scalafmtCheckAll + test + run check (not advised)
#   --no-push        commits and tags without pushing
#   --all            commits everything modified, not just this day
#   -t, --title TXT  puzzle title (defaults to the one in the file's scaladoc)
#
# The tag is only born after both parts actually run against your input:
# a half-solved day does not get one.
set -euo pipefail
set -o pipefail

die() { echo "error: $*" >&2; exit 1; }

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

YEAR="${1:-}"; DAY="${2:-}"
[[ -n "$YEAR" && -n "$DAY" ]] || die "usage: finish-day.sh <year> <day> [--no-verify] [--no-push] [--all] [-t title]"
shift 2

VERIFY=1; PUSH=1; ALL=0; TITLE=""
while [[ $# -gt 0 ]]; do
  case "$1" in
    --no-verify) VERIFY=0 ;;
    --no-push)   PUSH=0 ;;
    --all)       ALL=1 ;;
    -t|--title)  TITLE="${2:?-t needs a title}"; shift ;;
    *) die "unknown flag: $1" ;;
  esac
  shift
done

DD="$(printf '%02d' "$DAY")"
SRC="src/main/scala/aoc/y$YEAR/Day$DD.scala"
TEST="src/test/scala/aoc/y$YEAR/Day${DD}Suite.scala"
TAG="y$YEAR-d$DD"

[[ -f "$SRC" ]] || die "$SRC does not exist — start with ./scripts/new-day.sh $YEAR $DAY"
git rev-parse -q --verify "refs/tags/$TAG" >/dev/null && die "tag $TAG already exists"

# The title comes from the scaladoc: "/** https://adventofcode.com/2015/day/1 — Not Quite Lisp */"
if [[ -z "$TITLE" ]]; then
  TITLE="$(sed -n 's#.*adventofcode\.com/[0-9]*/day/[0-9]*[[:space:]]*—[[:space:]]*##p' "$SRC" \
    | head -1 | sed 's#[[:space:]]*\*/##' | sed 's#[[:space:]]*$##')"
fi
HEADLINE="$YEAR day $DD"
[[ -n "$TITLE" ]] && HEADLINE="$HEADLINE — $TITLE"

# Where the staged tree gets built. Under target/, so it is git-ignored and
# `sbt clean` takes it away.
VERIFY_DIR="$ROOT/target/finish-day"

# Checks out the *index* into a scratch directory and runs the gate in there.
#
# Running the gate against the working tree instead passes on files that are
# not going into the commit: a source that was never `git add`ed is present on
# disk and absent from the tag, which is how a green run still publishes a tree
# that does not compile. This copy holds exactly what the commit will hold.
verify_staged_tree() {
  rm -rf "$VERIFY_DIR"
  mkdir -p "$VERIFY_DIR"
  git checkout-index --all --prefix="target/finish-day/" || return 1
  # inputs/ is git-ignored on purpose, and `run check` reads the day out of it
  [[ -d "$ROOT/inputs" ]] && cp -r "$ROOT/inputs/." "$VERIFY_DIR/inputs/"
  (
    cd "$VERIFY_DIR" || exit 1
    # The sed drops sbt's own stack trace when `run check` exits nonzero: what
    # matters is the "failed: ..." line just above it.
    sbt -batch "scalafmtCheckAll; test; run check $YEAR $DAY" 2>&1 \
      | sed -e '/[[:space:]]at [A-Za-z_$][A-Za-z0-9_.$]*[.(]/d' \
            -e '/nonzero exit code returned from runner/d' \
            -e '/sbt server disconnected/d'
  )
}

# Staging comes first, so that what gets verified is what gets committed. If the
# gate then fails, the index goes back to exactly how it was found.
INDEX_BEFORE="$(git write-tree)"

if [[ $ALL -eq 1 ]]; then
  git add -A
else
  git add -- "$SRC"
  [[ -f "$TEST" ]] && git add -- "$TEST"
  others="$(git status --porcelain --untracked-files=all -- . ':!'"$SRC" ':!'"$TEST" | head -5)"
  if [[ -n "$others" ]]; then
    echo
    echo "heads up, left out of the commit (use --all to include):"
    echo "$others" | sed 's/^/  /'
    echo
  fi
fi

if [[ $VERIFY -eq 1 ]]; then
  echo "verifying $HEADLINE as it will be committed ..."
  if verify_staged_tree; then
    rm -rf "$VERIFY_DIR"
  else
    git read-tree "$INDEX_BEFORE"
    echo "the tree that failed is still at ${VERIFY_DIR#"$ROOT/"}" >&2
    die "verification failed — nothing was committed or tagged"
  fi
else
  echo "skipping verification (--no-verify)"
fi

if git diff --cached --quiet; then
  echo "nothing new to commit — tagging HEAD instead"
else
  git commit -q -m "$HEADLINE" -m "Both parts verified with: sbt \"run check $YEAR $DAY\""
  echo "commit: $(git log -1 --oneline)"
fi

git tag -a "$TAG" -m "AoC $HEADLINE"
echo "tag: $TAG"

if [[ $PUSH -eq 1 ]]; then
  if git remote get-url origin >/dev/null 2>&1; then
    branch="$(git branch --show-current)"
    git push --follow-tags origin "$branch"
    echo "pushed: origin/$branch (with tag $TAG)"
  else
    echo "no remote configured — the commit and the tag stayed local."
    echo "  git remote add origin git@github.com:<you>/advent-of-code.git"
    echo "  git push -u --follow-tags origin $(git branch --show-current)"
  fi
fi
