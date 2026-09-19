#Requires -Version 7.0
<#
.SYNOPSIS
  Closes a solved day: verifies it, commits it and creates the yYYYY-dDD tag.

.DESCRIPTION
  The tag is only born after both parts actually run against your input:
  a half-solved day does not get one.

.PARAMETER NoVerify
  Skips scalafmtCheckAll + test + run check (not advised).

.PARAMETER NoPush
  Commits and tags without pushing.

.PARAMETER All
  Commits everything modified, not just this day.

.PARAMETER Title
  Puzzle title (defaults to the one in the file's scaladoc).

.EXAMPLE
  ./scripts/finish-day.ps1 2015 1

.EXAMPLE
  ./scripts/finish-day.ps1 2015 1 -NoPush -Title 'Not Quite Lisp'
#>
[CmdletBinding()]
param(
  [Parameter(Mandatory, Position = 0)][int]$Year,
  [Parameter(Mandatory, Position = 1)][int]$Day,
  [switch]$NoVerify,
  [switch]$NoPush,
  [switch]$All,
  [Alias('t')][string]$Title = ''
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
# git and sbt report through their exit code here; a nonzero one is not a throw.
$PSNativeCommandUseErrorActionPreference = $false

function Die([string]$Message) {
  [Console]::Error.WriteLine("error: $Message")
  exit 1
}

$root = Split-Path -Parent $PSScriptRoot
Push-Location $root
try {
  $dd = '{0:d2}' -f $Day
  $src = "src/main/scala/aoc/y$Year/Day$dd.scala"
  $test = "src/test/scala/aoc/y$Year/Day${dd}Suite.scala"
  $tag = "y$Year-d$dd"

  if (-not (Test-Path -LiteralPath $src)) {
    Die "$src does not exist — start with ./scripts/new-day.ps1 $Year $Day"
  }

  & git rev-parse -q --verify "refs/tags/$tag" *> $null
  if ($LASTEXITCODE -eq 0) { Die "tag $tag already exists" }

  # The title comes from the scaladoc: "/** https://adventofcode.com/2015/day/1 — Not Quite Lisp */"
  if ([string]::IsNullOrWhiteSpace($Title)) {
    $hit = Select-String -LiteralPath $src -Pattern 'adventofcode\.com/\d+/day/\d+\s*—\s*(.*)$' |
      Select-Object -First 1
    if ($hit) {
      $Title = ($hit.Matches[0].Groups[1].Value -replace '\s*\*/\s*$', '').Trim()
    }
  }

  $headline = "$Year day $dd"
  if (-not [string]::IsNullOrWhiteSpace($Title)) { $headline = "$headline — $Title" }

  if ($NoVerify) {
    Write-Host 'skipping verification (-NoVerify)'
  }
  else {
    Write-Host "verifying $headline ..."
    # The filter drops sbt's own stack trace when `run check` exits nonzero: what
    # matters is the "failed: ..." line just above it.
    $noise = '(\sat [A-Za-z_$][A-Za-z0-9_.$]*[.(])|' +
             '(nonzero exit code returned from runner)|' +
             '(sbt server disconnected)'
    & sbt -batch "scalafmtCheckAll; test; run check $Year $Day" 2>&1 |
      ForEach-Object { [string]$_ } |
      Where-Object { $_ -notmatch $noise } |
      ForEach-Object { Write-Host $_ }
    if ($LASTEXITCODE -ne 0) {
      Die 'verification failed — nothing was committed or tagged'
    }
  }

  if ($All) {
    & git add -A
  }
  else {
    & git add -- $src
    if (Test-Path -LiteralPath $test) { & git add -- $test }
    $others = @(
      & git status --porcelain --untracked-files=all -- . ":(exclude)$src" ":(exclude)$test" |
        Select-Object -First 5
    )
    if ($others.Count -gt 0) {
      Write-Host ''
      Write-Host 'heads up, left out of the commit (use -All to include):'
      $others | ForEach-Object { Write-Host "  $_" }
      Write-Host ''
    }
  }

  & git diff --cached --quiet
  if ($LASTEXITCODE -eq 0) {
    Write-Host 'nothing new to commit — tagging HEAD instead'
  }
  else {
    & git commit -q -m $headline -m "Both parts verified with: sbt `"run check $Year $Day`""
    if ($LASTEXITCODE -ne 0) { Die 'git commit failed' }
    Write-Host "commit: $(& git log -1 --oneline)"
  }

  & git tag -a $tag -m "AoC $headline"
  if ($LASTEXITCODE -ne 0) { Die "could not create tag $tag" }
  Write-Host "tag: $tag"

  if (-not $NoPush) {
    & git remote get-url origin *> $null
    if ($LASTEXITCODE -eq 0) {
      $branch = (& git branch --show-current).Trim()
      & git push --follow-tags origin $branch
      if ($LASTEXITCODE -ne 0) { Die 'git push failed — the commit and the tag stayed local' }
      Write-Host "pushed: origin/$branch (with tag $tag)"
    }
    else {
      $branch = (& git branch --show-current).Trim()
      Write-Host 'no remote configured — the commit and the tag stayed local.'
      Write-Host '  git remote add origin git@github.com:<you>/advent-of-code.git'
      Write-Host "  git push -u --follow-tags origin $branch"
    }
  }
}
finally {
  Pop-Location
}

exit 0
