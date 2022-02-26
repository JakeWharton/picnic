package com.jakewharton.picnic

import com.jakewharton.crossword.TextCanvas
import com.jakewharton.crossword.visualWidth
import com.jakewharton.picnic.Table.PositionedCell
import com.jakewharton.picnic.TextAlignment.BottomCenter
import com.jakewharton.picnic.TextAlignment.BottomLeft
import com.jakewharton.picnic.TextAlignment.BottomRight
import com.jakewharton.picnic.TextAlignment.MiddleCenter
import com.jakewharton.picnic.TextAlignment.MiddleLeft
import com.jakewharton.picnic.TextAlignment.MiddleRight
import com.jakewharton.picnic.TextAlignment.TopCenter
import com.jakewharton.picnic.TextAlignment.TopLeft
import com.jakewharton.picnic.TextAlignment.TopRight

interface TextLayout {
  /**
   * The width in columns that this cell will occupy.
   *
   * Consider that multi-character codepoints and emoji occupy a single column. Non-printable
   * characters and ANSI color escape sequences occupy zero.
   */
  fun measureWidth(): Int

  fun measureHeight(): Int

  fun draw(canvas: TextCanvas)
}

internal class SimpleLayout(private val cell: PositionedCell) : TextLayout {
  private val leftPadding = cell.canonicalStyle?.paddingLeft ?: 0
  private val topPadding = cell.canonicalStyle?.paddingTop ?: 0

  override fun measureWidth(): Int {
    return leftPadding +
      (cell.canonicalStyle?.paddingRight ?: 0) +
      cell.cell.content.split('\n').maxOf { it.visualWidth }
  }

  override fun measureHeight(): Int {
    return 1 +
      topPadding +
      (cell.canonicalStyle?.paddingBottom ?: 0) +
      cell.cell.content.count { it == '\n' }
  }

  override fun draw(canvas: TextCanvas) {
    val height = measureHeight()
    val alignment = cell.canonicalStyle?.alignment ?: TopLeft
    val top = when (alignment) {
      TopLeft, TopCenter, TopRight -> topPadding
      MiddleLeft, MiddleCenter, MiddleRight -> ((canvas.height - height) / 2) + topPadding
      BottomLeft, BottomCenter, BottomRight -> canvas.height - height + topPadding
    }

    cell.cell.content.split('\n').forEachIndexed { index, line ->
      val lineWidth = leftPadding +
        (cell.canonicalStyle?.paddingRight ?: 0) +
        line.visualWidth
      val left = when (alignment) {
        TopLeft, MiddleLeft, BottomLeft -> leftPadding
        TopCenter, MiddleCenter, BottomCenter -> ((canvas.width - lineWidth) / 2) + leftPadding
        TopRight, MiddleRight, BottomRight -> canvas.width - lineWidth + leftPadding
      }
      canvas.write(top + index, left, line)
    }
  }
}
