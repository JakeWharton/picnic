package com.jakewharton.picnic

import org.junit.Assert.assertEquals
import org.junit.Test

class TextTest {
  @Test fun visualIndexAscii() {
    assertEquals(0, "AAAAA".visualIndex(0))
    assertEquals(1, "AAAAA".visualIndex(1))
    assertEquals(2, "AAAAA".visualIndex(2))
    assertEquals(3, "AAAAA".visualIndex(3))
    assertEquals(4, "AAAAA".visualIndex(4))
  }

  @Test fun visualIndexUnicode() {
    // 1 UTF-8 bytes.
    assertEquals(1, "\u0031a".visualIndex(1))
    // 2 UTF-8 bytes.
    assertEquals(1, "\u00A3a".visualIndex(1))
    // 3 UTF-8 bytes.
    assertEquals(1, "\u20ACa".visualIndex(1))
    // 3 UTF-8 bytes, full-width.
    assertEquals(1, "\u5317a".visualIndex(1))
    // 4 UTF-8 bytes (2 * UTF-16), full-width.
    assertEquals(2, (String(Character.toChars(0x1F603)) + "a").visualIndex(1))
  }

  @Test fun visualIndexJumpsAnsiEscapes() {
    val singleAnsiEscape = "AAA\u001B[31;1;4mAA"
    assertEquals(0, singleAnsiEscape.visualIndex(0))
    assertEquals(1, singleAnsiEscape.visualIndex(1))
    assertEquals(2, singleAnsiEscape.visualIndex(2))
    assertEquals(12, singleAnsiEscape.visualIndex(3))
    assertEquals(13, singleAnsiEscape.visualIndex(4))

    val dualAdjacentAnsiEscapes = "AAA\u001B[31;1;4m\u001B[0mAA"
    assertEquals(0, dualAdjacentAnsiEscapes.visualIndex(0))
    assertEquals(1, dualAdjacentAnsiEscapes.visualIndex(1))
    assertEquals(2, dualAdjacentAnsiEscapes.visualIndex(2))
    assertEquals(16, dualAdjacentAnsiEscapes.visualIndex(3))
    assertEquals(17, dualAdjacentAnsiEscapes.visualIndex(4))

    val dualAnsiEscapes = "AAA\u001B[31;1;4mAA\u001B[31;1;4mAA"
    assertEquals(0, dualAnsiEscapes.visualIndex(0))
    assertEquals(1, dualAnsiEscapes.visualIndex(1))
    assertEquals(2, dualAnsiEscapes.visualIndex(2))
    assertEquals(12, dualAnsiEscapes.visualIndex(3))
    assertEquals(13, dualAnsiEscapes.visualIndex(4))
  }

  @Test fun visualCodePointCountAscii() {
    assertEquals(0, "".visualCodePointCount)
    assertEquals(1, "A".visualCodePointCount)
    assertEquals(2, "AA".visualCodePointCount)
    assertEquals(3, "AAA".visualCodePointCount)
  }

  @Test fun visualCodePointCountAnsiEscapes() {
    assertEquals(1, "\u001B[31;1;4mA\u001B[0m".visualCodePointCount)
    assertEquals(3, "A\u001B[31;1;4mA\u001B[0mA".visualCodePointCount)

    assertEquals(3, "\u001B[31;1;4mA\u001B[0m\u001B[31;1;4mA\u001B[0mA".visualCodePointCount)
  }

  @Test fun visualCodePointCountUnicode() {
    // 1 UTF-8 bytes.
    assertEquals(2, "\u0031a".visualCodePointCount)
    // 2 UTF-8 bytes.
    assertEquals(2, "\u00A3a".visualCodePointCount)
    // 3 UTF-8 bytes.
    assertEquals(2, "\u20ACa".visualCodePointCount)
    // 3 UTF-8 bytes, full-width.
    assertEquals(2, "\u5317a".visualCodePointCount)
    // 4 UTF-8 bytes (2 * UTF-16), full-width.
    assertEquals(2, (String(Character.toChars(0x1F603)) + "a").visualCodePointCount)
  }
}
