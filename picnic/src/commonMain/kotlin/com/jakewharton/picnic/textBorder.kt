package com.jakewharton.picnic

import kotlin.jvm.JvmField

public class TextBorder(private val characters: String) {
  init {
    require(characters.length == 16) { "Border string must contain exactly 16 characters" }
  }

  public val empty: Char
    get() = characters[0]

  public val down: Char
    get() = characters[1]

  public val up: Char
    get() = characters[2]

  public val vertical: Char
    get() = characters[3]

  public val right: Char
    get() = characters[4]

  public val downAndRight: Char
    get() = characters[5]

  public val upAndRight: Char
    get() = characters[6]

  public val verticalAndRight: Char
    get() = characters[7]

  public val left: Char
    get() = characters[8]

  public val downAndLeft: Char
    get() = characters[9]

  public val upAndLeft: Char
    get() = characters[10]

  public val verticalAndLeft: Char
    get() = characters[11]

  public val horizontal: Char
    get() = characters[12]

  public val downAndHorizontal: Char
    get() = characters[13]

  public val upAndHorizontal: Char
    get() = characters[14]

  public val verticalAndHorizontal: Char
    get() = characters[15]

  public fun get(
    down: Boolean = false,
    up: Boolean = false,
    right: Boolean = false,
    left: Boolean = false,
  ): Char {
    return characters[
      (if (down) 1 else 0) or (if (up) 2 else 0) or (if (right) 4 else 0) or (if (left) 8 else 0)]
  }

  public companion object {
    @JvmField public val DEFAULT: TextBorder = TextBorder(" ╷╵│╶┌└├╴┐┘┤─┬┴┼")
    @JvmField public val ROUNDED: TextBorder = TextBorder(" ╷╵│╶╭╰├╴╮╯┤─┬┴┼")
    @JvmField public val ASCII: TextBorder = TextBorder("   | +++ +++-+++")
  }
}
