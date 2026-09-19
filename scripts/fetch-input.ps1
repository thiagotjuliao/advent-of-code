#Requires -Version 7.0
<#
.SYNOPSIS
  Downloads one day's input into inputs/YYYY/dayDD.txt (never re-downloads).

.EXAMPLE
  ./scripts/fetch-input.ps1 2015 1
#>
[CmdletBinding()]
param(
  [Parameter(Mandatory, Position = 0)][int]$Year,
  [Parameter(Mandatory, Position = 1)][int]$Day
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
$ProgressPreference = 'SilentlyContinue'

function Die([string]$Message) {
  [Console]::Error.WriteLine($Message)
  exit 1
}

$root = Split-Path -Parent $PSScriptRoot
$dd = '{0:d2}' -f $Day
$out = Join-Path $root "inputs/$Year/day$dd.txt"
$rel = [IO.Path]::GetRelativePath($root, $out).Replace('\', '/')

if ((Test-Path -LiteralPath $out) -and (Get-Item -LiteralPath $out).Length -gt 0) {
  Write-Host "input already there: $rel"
  exit 0
}

# .env loses to whatever is already in the environment
$envFile = Join-Path $root '.env'
if (Test-Path -LiteralPath $envFile) {
  foreach ($line in Get-Content -LiteralPath $envFile) {
    if ($line -notmatch '^\s*(?:export\s+)?([A-Za-z_][A-Za-z0-9_]*)\s*=\s*(.*)$') { continue }
    $name = $Matches[1]
    $value = $Matches[2].Trim()
    if ($value.Length -ge 2 -and
        (($value.StartsWith('"') -and $value.EndsWith('"')) -or
         ($value.StartsWith("'") -and $value.EndsWith("'")))) {
      $value = $value.Substring(1, $value.Length - 2)
    }
    if ([string]::IsNullOrEmpty([Environment]::GetEnvironmentVariable($name))) {
      Set-Item -Path "Env:$name" -Value $value
    }
  }
}

if ([string]::IsNullOrEmpty($env:AOC_SESSION)) {
  Die 'AOC_SESSION is not set — copy .env.example to .env and fill it in.'
}

New-Item -ItemType Directory -Force -Path (Split-Path -Parent $out) | Out-Null

$contact = if ([string]::IsNullOrEmpty($env:AOC_CONTACT)) { 'unknown' } else { $env:AOC_CONTACT }

# The contact in the User-Agent is what the AoC automation rules ask for.
$response = Invoke-WebRequest `
  -Uri "https://adventofcode.com/$Year/day/$Day/input" `
  -Headers @{ Cookie = "session=$env:AOC_SESSION" } `
  -UserAgent "github.com/advent-of-code scala runner (contact: $contact)" `
  -MaximumRedirection 0 `
  -SkipHttpErrorCheck

$status = [int]$response.StatusCode
$body = [string]$response.Content

if ($status -ne 200) {
  [Console]::Error.WriteLine("HTTP $status while downloading $Year/$Day`:")
  [Console]::Error.WriteLine($body.Substring(0, [Math]::Min(300, $body.Length)))
  exit 1
}

# Inputs are LF on disk, like everything else the repo writes.
$body = $body -replace "`r`n", "`n"
[IO.File]::WriteAllText($out, $body, [Text.UTF8Encoding]::new($false))

$lines = [regex]::Matches($body, "`n").Count
Write-Host "saved to $rel ($lines lines)"

exit 0
