package com.jakewharton.picnic

import kotlin.test.Test
import kotlin.test.assertEquals

class CellBorderTest {
  @Test fun allCorners() {
    val table = table {
      row {
        cell(" ") {
          borderTop = true
          borderLeft = true
          borderRight = true
        }
        cell(" ") {
          borderTop = true
          borderLeft = true
          borderRight = true
        }
        cell(" ") {
          borderLeft = true
          borderRight = true
        }
      }
      row {
        cell(" ") {
          borderTop = true
          borderLeft = true
          borderRight = true
          borderBottom = true
        }
        cell(" ") {
          borderLeft = true
          borderBottom = true
        }
        cell(" ") {
          borderRight = true
          borderBottom = true
        }
      }
      row {
        cell(" ") {
          borderTop = true
          borderRight = true
          borderBottom = true
        }
        cell(" ") {
          borderTop = true
          borderLeft = true
          borderBottom = true
        }
        cell(" ") {
          borderTop = true
        }
      }
    }

    assertEquals(
      """
      |┌─┬─┐ ╷
      |│ │ │ │
      |├─┤ ╵ │
      |│ │   │
      |└─┼───┘
      |  │    
      |╶─┴─╴  
      """.trimMargin(),
      table.renderText(),
    )

    assertEquals(
      """
      |╭─┬─╮ ╷
      |│ │ │ │
      |├─┤ ╵ │
      |│ │   │
      |╰─┼───╯
      |  │    
      |╶─┴─╴  
      """.trimMargin(),
      table.renderText(border = TextBorder.ROUNDED),
    )

    assertEquals(
      """
      |+-+-+  
      || | | |
      |+-+   |
      || |   |
      |+-+---+
      |  |    
      | -+-   
      """.trimMargin(),
      table.renderText(border = TextBorder.ASCII),
    )
  }

  @Test fun adjacentRowBordersWithoutCorners() {
    val table = table {
      row {
        cell(1) {
          borderBottom = true
        }
        cell(2) {
          borderBottom = true
        }
      }
      row(3, 4)
    }

    assertEquals(
      """
      |12
      |──
      |34
      """.trimMargin(),
      table.renderText(),
    )
  }

  @Test fun adjacentColumnBordersWithoutCorners() {
    val table = table {
      row {
        cell(1) {
          borderRight = true
        }
        cell(2)
      }
      row {
        cell(3) {
          borderRight = true
        }
        cell(4)
      }
    }

    assertEquals(
      """
      |1│2
      |3│4
      """.trimMargin(),
      table.renderText(),
    )
  }

  @Test fun rowSpanPushesBordersToTheRight() {
    val table = table {
      row {
        cell("A") {
          rowSpan = 2
          borderBottom = true
        }
        cell("B") {
          borderBottom = true
        }
      }
      row {
        cell("C") {
          borderBottom = true
        }
      }
    }

    assertEquals(
      """
      |AB
      | ─
      | C
      |──
      """.trimMargin(),
      table.renderText(),
    )
  }

  @Test fun stylePropagation() {
    val table = table {
      cellStyle {
        border = true
      }
      body {
        cellStyle {
          borderTop = false
        }
        row {
          cellStyle {
            borderLeft = false
          }
          cell("A") {
            borderRight = false
          }
        }
      }
    }

    assertEquals(
      """
      |A
      |─
      """.trimMargin(),
      table.renderText(),
    )
  }

  @Test fun tableStyleTakesPrecedenceOverCell() {
    val table = table {
      style {
        borderStyle = BorderStyle.Hidden
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
      |A│B│C
      |─┼─┼─
      |D│E│F
      |─┼─┼─
      |G│H│I
      """.trimMargin(),
      table.renderText(),
    )
  }

  @Test fun tableStyleTakesPrecedenceOverCellWithRowAndColumnSpans() {
    val table = table {
      style {
        borderStyle = BorderStyle.Hidden
      }
      cellStyle {
        border = true
      }
      row {
        cell("1")
        cell("1")
        cell("2") {
          rowSpan = 2
          columnSpan = 2
        }
      }
      row("1", "1")
      row {
        cell("2") {
          rowSpan = 2
          columnSpan = 2
        }
        cell("1")
        cell("1")
      }
      row("1", "1")
    }

    assertEquals(
      """
      |1│1│2  
      |─┼─┤   
      |1│1│   
      |─┴─┼─┬─
      |2  │1│1
      |   ├─┼─
      |   │1│1
      """.trimMargin(),
      table.renderText(),
    )
  }

  @Test fun borderLeftCalculationWithTableBorderHidden() {
    val table = table {
      style {
        borderStyle = BorderStyle.Hidden
      }
      cellStyle {
        borderLeft = true
      }
      row("1", "2", "3")
      row {
        cell("4") {
          columnSpan = 2
        }
        cell("5")
      }
      row {
        cell("6")
        cell("7") {
          columnSpan = 2
        }
      }
      row {
        cell("8") {
          columnSpan = 3
        }
      }
    }

    assertEquals(
      """
      |1│2│3
      |4  │5
      |6│7  
      |8    
      """.trimMargin(),
      table.renderText(),
    )
  }

  @Test fun borderRightCalculationWithTableBorderHidden() {
    val table = table {
      style {
        borderStyle = BorderStyle.Hidden
      }
      cellStyle {
        borderRight = true
      }
      row("1", "2", "3")
      row {
        cell("4") {
          columnSpan = 2
        }
        cell("5")
      }
      row {
        cell("6")
        cell("7") {
          columnSpan = 2
        }
      }
      row {
        cell("8") {
          columnSpan = 3
        }
      }
    }

    assertEquals(
      """
      |1│2│3
      |4  │5
      |6│7  
      |8    
      """.trimMargin(),
      table.renderText(),
    )
  }

  @Test fun borderTopCalculationWithTableBorderHidden() {
    val table = table {
      style {
        borderStyle = BorderStyle.Hidden
      }
      cellStyle {
        borderTop = true
      }
      row {
        cell("1")
        cell("2") {
          rowSpan = 2
        }
        cell("3")
        cell("4") {
          rowSpan = 3
        }
      }
      row {
        cell("5")
        cell("6") {
          rowSpan = 2
        }
      }
      row("7", "8")
    }

    assertEquals(
      """
      |1234
      |─ ─ 
      |5 6 
      |──  
      |78  
      """.trimMargin(),
      table.renderText(),
    )
  }

  @Test fun borderBottomCalculationWithTableBorderHidden() {
    val table = table {
      style {
        borderStyle = BorderStyle.Hidden
      }
      cellStyle {
        borderBottom = true
      }
      row {
        cell("1")
        cell("2") {
          rowSpan = 2
        }
        cell("3")
        cell("4") {
          rowSpan = 3
        }
      }
      row {
        cell("5")
        cell("6") {
          rowSpan = 2
        }
      }
      row("7", "8")
    }

    assertEquals(
      """
      |1234
      |─ ─ 
      |5 6 
      |──  
      |78  
      """.trimMargin(),
      table.renderText(),
    )
  }
}
