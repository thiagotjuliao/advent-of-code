#Requires -Version 7.0
<#
.SYNOPSIS
  Creates the solution file (and its test) for a day and downloads the input.

.EXAMPLE
  ./scripts/new-day.ps1 2015 1 -NoInput -NoTest
#>
[CmdletBinding()]
param(
  [Parameter(Mandatory, Position = 0)][int]$Year,
  [Parameter(Mandatory, Position = 1)][int]$Day,
  [switch]$NoInput,
  [switch]$NoTest
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$utf8 = [Text.UTF8Encoding]::new($false)
$root = Split-Path -Parent $PSScriptRoot
$dd = '{0:d2}' -f $Day
$src = Join-Path $root "src/main/scala/aoc/y$Year/Day$dd.scala"
$test = Join-Path $root "src/test/scala/aoc/y$Year/Day${dd}Suite.scala"

# Everything this writes is LF, per .gitattributes.
function Relative([string]$Path) {
  return [IO.Path]::GetRelativePath($root, $Path).Replace('\', '/')
}

function Write-Scala([string]$Path, [string]$Content) {
  New-Item -ItemType Directory -Force -Path (Split-Path -Parent $Path) | Out-Null
  $text = ($Content -replace "`r`n", "`n").TrimEnd("`n") + "`n"
  [IO.File]::WriteAllText($Path, $text, $utf8)
  Write-Host "created: $(Relative $Path)"
}

if (Test-Path -LiteralPath $src) {
  Write-Host "already there: $(Relative $src)"
}
else {
  Write-Scala $src @"
package aoc.y$Year

import aoc.Solution

/** https://adventofcode.com/$Year/day/$Day
  *
  * To solve it, register the parts:
  *   part1 { in => in.lines.size }
  */
object Day$dd extends Solution($Year, $Day)
"@
}

if (-not $NoTest -and -not (Test-Path -LiteralPath $test)) {
  Write-Scala $test @"
package aoc.y$Year

import aoc.Input
import munit.FunSuite

class Day${dd}Suite extends FunSuite:

  private val sample = Input("""
    |paste the example from the puzzle text here
  """.stripMargin.trim)

  test("part 1".ignore) {
    assertEquals(Day${dd}.solve(1, sample), ???)
  }

  test("part 2".ignore) {
    assertEquals(Day${dd}.solve(2, sample), ???)
  }
"@
}

if (-not $NoInput) {
  # A failed download must not take the day's files down with it.
  try { & (Join-Path $PSScriptRoot 'fetch-input.ps1') $Year $Day } catch { }
}

Write-Host ''
Write-Host "to run it:  sbt `"run $Year $Day`""

exit 0
