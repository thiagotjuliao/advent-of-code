package aoc.y2015

import aoc.AOCFunSuite
import aoc.Input

class Day05Suite extends AOCFunSuite(Day05):
  part1 {
    sample("sample00", Input("ugknbfddgicrmopn"), 1)
    sample("sample01", Input("aaa"), 1)
    sample("sample02", Input("jchzalrnumimnmhp"), 0)
    sample("sample03", Input("haegwjzuvuyypxyu"), 0)
    sample("sample04", Input("dvszwmarrgswjxmb"), 0)
    sample("sample05", Input("hello"), 0)
  }

  part2 {
    sample("sample00", Input("qjhvhtzxzqqjkmpb"), 1)
    sample("sample01", Input("xxyxx"), 1)
    sample("sample02", Input("uurcxstgmygtbstg"), 0)
    sample("sample03", Input("ieodomkazucvgmuy"), 0)
    sample("sample04", Input("aaab"), 0)
    sample("sample05", Input("aaaa"), 1)
  }
