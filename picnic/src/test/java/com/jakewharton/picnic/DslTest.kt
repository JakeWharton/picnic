package com.jakewharton.picnic

import com.jakewharton.picnic.TextAlignment.BottomCenter
import kotlin.test.Test
import kotlin.test.assertEquals

class DslTest {
  @Test fun cellsAppliesStyleToEachCell() {
    val table = table {
      row {
        cells("a", "b\nb", "c\nc\nc") {
          alignment = BottomCenter
        }
      }
    }

    assertEquals(
      """
      |  c
      | bc
      |abc
      """.trimMargin(),
      table.renderText(),
    )
  }
}
