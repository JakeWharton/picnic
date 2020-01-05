package com.jakewharton.picnic

internal class TextSurface(
  override val width: Int,
  override val height: Int
) : TextCanvas {
  private val buffer = IntArray(height * width) { _ ->
    ' '.toInt()
  }

  override operator fun set(row: Int, column: Int, char: Int) {
    require(row in 0 until height) { "Row $row not in range [0, $height)" }
    require(column in 0 until width) { "Column $column not in range [0, $width)" }
    buffer[row * width + column] = char
  }

  override fun get(row: Int, column: Int): Int {
    require(row in 0 until height) { "Row $row not in range [0, $height)" }
    require(column in 0 until width) { "Column $column not in range [0, $width)" }
    return buffer[row * width + column]
  }

  /**
   * buffer is array of codepoints, convert back to chars
   */
  override fun toString() = StringBuilder(buffer.size * 2).apply {
      buffer.forEachIndexed { index, codePoint ->
        append(Character.toChars(codePoint))

        if (index % width == width - 1) appendln()
      }
    }.toString()
}

interface TextCanvas {
  val width: Int
  val height: Int

  operator fun set(row: Int, column: Int, char: Char) = set(row, column, char.toInt())
  operator fun set(row: Int, column: Int, char: Int)
  operator fun get(row: Int, column: Int): Int

  @JvmDefault
  fun clip(left: Int, right: Int, top: Int, bottom: Int): TextCanvas {
    return ClippedTextCanvas(this, left, right, top, bottom)
  }
}

private class ClippedTextCanvas(
  private val canvas: TextCanvas,
  private val left: Int,
  right: Int,
  private val top: Int,
  bottom: Int
) : TextCanvas {
  override val width = right - left
  override val height = bottom - top

  override fun set(row: Int, column: Int, char: Int) {
    require(row in 0 until height) { "Row $row not in range [0, $height)" }
    require(column in 0 until width) { "Column $column not in range [0, $width)" }
    canvas[top + row, left + column] = char
  }

  override fun get(row: Int, column: Int): Int {
    require(row in 0 until height) { "Row $row not in range [0, $height)" }
    require(column in 0 until width) { "Column $column not in range [0, $width)" }
    return canvas[top + row, left + column]
  }
}
