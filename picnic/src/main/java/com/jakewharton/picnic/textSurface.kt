package com.jakewharton.picnic

internal class TextSurface(
  override val width: Int,
  override val height: Int,
) : TextCanvas {
  private val buffer = IntArray(height * width) { ' '.toInt() }

  override operator fun set(row: Int, column: Int, codePoint: Int) {
    require(row in 0 until height) { "Row $row not in range [0, $height)" }
    require(column in 0 until width) { "Column $column not in range [0, $width)" }
    buffer[row * width + column] = codePoint
  }

  override fun get(row: Int, column: Int): Int {
    require(row in 0 until height) { "Row $row not in range [0, $height)" }
    require(column in 0 until width) { "Column $column not in range [0, $width)" }
    return buffer[row * width + column]
  }

  override fun toString(): String {
    val capacity = buffer.size +
      /* newlines: */ height +
      /* multi-char codepoint estimate: */ width
    return buildString(capacity) {
      buffer.forEachIndexed { index, codePoint ->
        appendCodePoint(codePoint)

        if (index % width == width - 1) appendLine()
      }
    }
  }
}

interface TextCanvas {
  val width: Int
  val height: Int

  operator fun set(row: Int, column: Int, char: Char) = set(row, column, char.toInt())
  operator fun set(row: Int, column: Int, codePoint: Int)
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
  bottom: Int,
) : TextCanvas {
  override val width = right - left
  override val height = bottom - top

  override fun set(row: Int, column: Int, codePoint: Int) {
    require(row in 0 until height) { "Row $row not in range [0, $height)" }
    require(column in 0 until width) { "Column $column not in range [0, $width)" }
    canvas[top + row, left + column] = codePoint
  }

  override fun get(row: Int, column: Int): Int {
    require(row in 0 until height) { "Row $row not in range [0, $height)" }
    require(column in 0 until width) { "Column $column not in range [0, $width)" }
    return canvas[top + row, left + column]
  }
}
