package com.jakewharton.picnic

import com.google.common.truth.Truth.assertThat
import com.jakewharton.picnic.BorderStyle.Hidden
import org.junit.Test

class TableBorderTest {
  @Test fun tableBorderAndCellBorderMerges() {
    val table = table {
      style {
        border = true
      }
      cellStyle {
        border = true
      }
      body {
        row("A", "B", "C")
        row("D", "E", "F")
        row("G", "H", "I")
      }
    }

    assertThat(table.renderText()).isEqualTo(
      """
      |┌─┬─┬─┐
      |│A│B│C│
      |├─┼─┼─┤
      |│D│E│F│
      |├─┼─┼─┤
      |│G│H│I│
      |└─┴─┴─┘
      |""".trimMargin()
    )
  }

  @Test fun tableBorderTakesPrecedenceOverCellBorder() {
    val table = table {
      style {
        border = true
      }
      cellStyle {
        border = false
      }
      body {
        row("A", "B", "C")
        row("D", "E", "F")
        row("G", "H", "I")
      }
    }

    assertThat(table.renderText()).isEqualTo(
      """
      |┌───┐
      |│ABC│
      |│DEF│
      |│GHI│
      |└───┘
      |""".trimMargin()
    )
  }

  @Test fun tableBorderHiddenByBorderStyle() {
    val table = table {
      style {
        border = true
        borderStyle = Hidden
      }
      body {
        row("A", "B", "C")
        row("D", "E", "F")
        row("G", "H", "I")
      }
    }

    assertThat(table.renderText()).isEqualTo(
      """
      |ABC
      |DEF
      |GHI
      |""".trimMargin()
    )
  }

  @Test fun tableBorderWithMiddleBorders() {
    val table = table {
      style {
        border = true
      }
      header {
        cellStyle {
          borderBottom = true
        }
        row {
          cell("A") {
            borderRight = true
          }
          cell("B")
          cell("C")
        }
      }
      body {
        row {
          cell("D") {
            borderRight = true
          }
          cell("E")
          cell("F")
        }
        row {
          cell("G") {
            borderRight = true
          }
          cell("H")
          cell("I")
        }
      }
      footer {
        cellStyle {
          borderTop = true
        }
        row {
          cell("J") {
            borderRight = true
          }
          cell("K")
          cell("L")
        }
      }
    }

    assertThat(table.renderText()).isEqualTo(
      """
      |┌─┬──┐
      |│A│BC│
      |├─┼──┤
      |│D│EF│
      |│G│HI│
      |├─┼──┤
      |│J│KL│
      |└─┴──┘
      |""".trimMargin()
    )
  }
}
