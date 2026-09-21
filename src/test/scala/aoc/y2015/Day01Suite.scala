package aoc.y2015

import aoc.AOCFunSuite
import aoc.Input

class Day01Suite extends AOCFunSuite(Day01):
  part1 {
    sample("sample00", Input("(())"), 0)
    sample("sample01", Input("()()"), 0)
    sample("sample02", Input("(()(()("), 3)
    sample("sample03", Input("))((((("), 3)
    sample("sample04", Input("((("), 3)
    sample("sample05", Input("())"), -1)
    sample("sample06", Input("))("), -1)
    sample("sample07", Input(")))"), -3)
    sample("sample08", Input(")())())"), -3)
  }

  part2 {
    sample("sample00", Input(")"), 1)
    sample("sample01", Input("()())"), 5)
  }
