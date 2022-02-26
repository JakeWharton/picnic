package com.jakewharton.picnic

import kotlin.jvm.JvmField

class TextBorder(private val characters: String) {
  init {
    require(characters.length == 16) { "Border string must contain exactly 16 characters" }
  }

  val empty get() = characters[0]
  val down get() = characters[1]
  val up get() = characters[2]
  val vertical get() = characters[3]
  val right get() = characters[4]
  val downAndRight get() = characters[5]
  val upAndRight get() = characters[6]
  val verticalAndRight get() = characters[7]
  val left get() = characters[8]
  val downAndLeft get() = characters[9]
  val upAndLeft get() = characters[10]
  val verticalAndLeft get() = characters[11]
  val horizontal get() = characters[12]
  val downAndHorizontal get() = characters[13]
  val upAndHorizontal get() = characters[14]
  val verticalAndHorizontal get() = characters[15]

  fun get(
    down: Boolean = false,
    up: Boolean = false,
    right: Boolean = false,
    left: Boolean = false,
  ): Char {
    return characters[
      (if (down) 1 else 0) or
        (if (up) 2 else 0) or
        (if (right) 4 else 0) or
        (if (left) 8 else 0)
    ]
  }

  companion object {
    @JvmField val DEFAULT = TextBorder(" ╷╵│╶┌└├╴┐┘┤─┬┴┼")
    @JvmField val ROUNDED = TextBorder(" ╷╵│╶╭╰├╴╮╯┤─┬┴┼")
    @JvmField val ASCII = TextBorder("   | +++ +++-+++")
  }
}
