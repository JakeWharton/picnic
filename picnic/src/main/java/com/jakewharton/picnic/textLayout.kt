package com.jakewharton.picnic

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
  fun measureWidth(): Int
  fun measureHeight(): Int

  fun draw(canvas: TextCanvas)
}

/**
 * Returns number of Unicode code points in this string. This may be different from [String.length]
 * if the string contains surrogate pairs.
 */
private val String.unicodeLength: Int get() = codePointCount(0, length)

internal class SimpleLayout(private val cell: PositionedCell) : TextLayout {
  private val leftPadding = cell.canonicalStyle?.paddingLeft ?: 0
  private val topPadding = cell.canonicalStyle?.paddingTop ?: 0

  override fun measureWidth(): Int {
    return leftPadding +
        (cell.canonicalStyle?.paddingRight ?: 0) +
        (cell.cell.content.split('\n').maxBy { it.unicodeLength }?.unicodeLength ?: 0)
  }

  override fun measureHeight(): Int {
    return 1 +
        topPadding +
        (cell.canonicalStyle?.paddingBottom ?: 0) +
        cell.cell.content.count { it == '\n' }
  }

  override fun draw(canvas: TextCanvas) {
    val width = measureWidth()
    val height = measureHeight()

    val alignment = cell.canonicalStyle?.alignment ?: TopLeft
    val left = when (alignment) {
      TopLeft, MiddleLeft, BottomLeft -> leftPadding
      TopCenter, MiddleCenter, BottomCenter -> ((canvas.width - width) / 2) + leftPadding
      TopRight, MiddleRight, BottomRight -> canvas.width - width + leftPadding
    }
    val top = when (alignment) {
      TopLeft, TopCenter, TopRight -> topPadding
      MiddleLeft, MiddleCenter, MiddleRight -> ((canvas.height - height) / 2) + topPadding
      BottomLeft, BottomCenter, BottomRight -> canvas.height - height + topPadding
    }

    var x = left
    var y = top
    for (codePoint in cell.cell.content.codePoints) {
      // TODO invisible chars, graphemes, etc.
      if (codePoint != '\n'.toInt()) {
        canvas[y, x++] = codePoint
      } else {
        y++
        x = left
      }
    }
  }
}

private val String.codePoints get() = sequence {
  var i = 0
  while (i < length) {
    val codePoint = codePointAt(i)
    yield(codePoint)
    i += Character.charCount(codePoint)
  }
}
