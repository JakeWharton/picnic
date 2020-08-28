package com.jakewharton.picnic

import com.google.common.truth.Truth.assertThat
import com.jakewharton.picnic.TextAlignment.BottomCenter
import org.junit.Test

class DslTest {
  @Test fun cellsAppliesStyleToEachCell() {
    val table = table {
      row {
        cells("a", "b\nb", "c\nc\nc") {
          alignment = BottomCenter
        }
      }
    }

    assertThat(table.renderText()).isEqualTo(
      """
      |  c
      | bc
      |abc
      |""".trimMargin()
    )
  }
}
