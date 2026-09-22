package aoc.utils

import java.security.MessageDigest
import java.nio.charset.StandardCharsets

object MD5:
  def digest(s: String): Array[Byte] =
    MessageDigest
      .getInstance("MD5")
      .digest(s.getBytes(StandardCharsets.UTF_8))

  def hash(s: String): String =
    digest(s)
      .map("%02x".format(_))
      .mkString

  def hasLeadingZeros(b: Array[Byte], n: Int): Boolean =
    (0 until n).forall: i =>
      if i % 2 == 0 then (b(i / 2) & 0xf0) == 0
      else (b(i / 2) & 0x0f) == 0
