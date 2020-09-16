package com.jakewharton.picnic

private val ansiColorEscape = Regex("""\u001B\[\d+(;\d+)*m""")

internal fun CharSequence.visualIndex(index: Int): Int {
  var currentIndex = 0
  var remaining = index
  while (true) {
    val match = ansiColorEscape.find(this, startIndex = currentIndex) ?: break

    val jump = codePointCount(startIndex = currentIndex, endIndex = match.range.first)
    if (jump > remaining) break

    remaining -= jump
    currentIndex = match.range.last + 1
  }

  while (remaining > 0) {
    val codePoint = codePointAt(currentIndex)
    currentIndex += codePoint.charCount
    remaining--
  }

  return currentIndex
}

internal val CharSequence.visualCodePointCount: Int get() {
  // Fast path: no escapes.
  val firstEscape = indexOf('\u001B')
  if (firstEscape == -1) {
    return codePointCount()
  }

  var currentIndex = firstEscape
  var count = codePointCount(endIndex = firstEscape)
  while (true) {
    val match = ansiColorEscape.find(this, startIndex = currentIndex) ?: break
    count += codePointCount(startIndex = currentIndex, endIndex = match.range.first)
    currentIndex = match.range.last + 1
  }
  count += codePointCount(startIndex = currentIndex)
  return count
}

// TODO https://youtrack.jetbrains.com/issue/KT-22803
private fun CharSequence.codePointCount(startIndex: Int = 0, endIndex: Int = length): Int {
  return Character.codePointCount(this, startIndex, endIndex)
}

private fun CharSequence.codePointAt(index: Int): Int {
  return Character.codePointAt(this, index)
}

private val Int.charCount: Int get() {
  return Character.charCount(this)
}
