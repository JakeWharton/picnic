package com.jakewharton.picnic

import com.jakewharton.picnic.BorderStyle.Hidden
import kotlin.test.Test
import kotlin.test.assertEquals

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

    assertEquals(
      """
      |┌─┬─┬─┐
      |│A│B│C│
      |├─┼─┼─┤
      |│D│E│F│
      |├─┼─┼─┤
      |│G│H│I│
      |└─┴─┴─┘
      """.trimMargin(),
      table.renderText(),
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

    assertEquals(
      """
      |┌───┐
      |│ABC│
      |│DEF│
      |│GHI│
      |└───┘
      """.trimMargin(),
      table.renderText(),
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

    assertEquals(
      """
      |ABC
      |DEF
      |GHI
      """.trimMargin(),
      table.renderText(),
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

    assertEquals(
      """
      |┌─┬──┐
      |│A│BC│
      |├─┼──┤
      |│D│EF│
      |│G│HI│
      |├─┼──┤
      |│J│KL│
      |└─┴──┘
      """.trimMargin(),
      table.renderText(),
    )
  }
}
