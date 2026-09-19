#Requires -Version 7.0
<#
.SYNOPSIS
  Creates the 25 stubs of a year (no tests, no input downloads).

.EXAMPLE
  ./scripts/scaffold-year.ps1 2026
#>
[CmdletBinding()]
param(
  [Parameter(Mandatory, Position = 0)][int]$Year
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$newDay = Join-Path $PSScriptRoot 'new-day.ps1'
foreach ($day in 1..25) {
  & $newDay $Year $day -NoInput -NoTest
}

Write-Host ''
Write-Host "remember to add $Year to Solutions.years"

exit 0
