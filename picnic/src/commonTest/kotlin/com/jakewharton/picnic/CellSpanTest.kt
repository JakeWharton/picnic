package com.jakewharton.picnic

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CellSpanTest {
  @Test fun columnSpans() {
    val table = table {
      row {
        cell("88888888") {
          columnSpan = 8
        }
        cell("1")
      }
      row {
        repeat(2) {
          cell("4444") {
            columnSpan = 4
          }
        }
        cell("1")
      }
      row {
        repeat(4) {
          cell("22") {
            columnSpan = 2
          }
        }
        cell("1")
      }
      row {
        repeat(9) {
          cell("1")
        }
      }
    }

    assertEquals(
      """
      |888888881
      |444444441
      |222222221
      |111111111
      """.trimMargin(),
      table.renderText(),
    )
  }

  @Test fun columnSpanAcrossDifferentSizedColumnsDoesNotExpand() {
    val table = table {
      row("1", "22", "333")
      row {
        cell("666666") {
          columnSpan = 3
        }
      }
    }

    assertEquals(
      """
      |122333
      |666666
      """.trimMargin(),
      table.renderText(),
    )
  }

  @Test fun columnSpanAcrossBorderDoesNotExpand() {
    val table = table {
      row {
        cell("11") {
          borderRight = true
        }
        cell("22")
      }
      row {
        cell("33333") {
          columnSpan = 2
        }
      }
    }

    assertEquals(
      """
      |11│22
      |33333
      """.trimMargin(),
      table.renderText(),
    )
  }

  @Test fun rowSpans() {
    val table = table {
      row {
        cell("8\n8\n8\n8\n8\n8\n8\n8") {
          rowSpan = 8
        }
        cell("4\n4\n4\n4") {
          rowSpan = 4
        }
        cell("2\n2") {
          rowSpan = 2
        }
        cell("1")
      }
      row("1")
      row {
        cell("2\n2") {
          rowSpan = 2
        }
        cell("1")
      }
      row("1")
      row {
        cell("4\n4\n4\n4") {
          rowSpan = 4
        }
        cell("2\n2") {
          rowSpan = 2
        }
        cell("1")
      }
      row("1")
      row {
        cell("2\n2") {
          rowSpan = 2
        }
        cell("1")
      }
      row("1")
      row {
        repeat(4) {
          cell("1")
        }
      }
    }

    assertEquals(
      """
      |8421
      |8421
      |8421
      |8421
      |8421
      |8421
      |8421
      |8421
      |1111
      """.trimMargin(),
      table.renderText(),
    )
  }

  @Test fun rowSpanAcrossDifferentSizedRowsDoesNotExpand() {
    val table = table {
      row {
        cell("6\n6\n6\n6\n6\n6") {
          rowSpan = 3
        }
        cell("1")
      }
      row("2\n2")
      row("3\n3\n3")
    }

    assertEquals(
      """
      |61
      |62
      |62
      |63
      |63
      |63
      """.trimMargin(),
      table.renderText(),
    )
  }

  @Test fun rowSpanAcrossBorderDoesNotExpand() {
    val table = table {
      row {
        cell("1\n1\n1") {
          rowSpan = 2
        }
        cell("2") {
          borderBottom = true
        }
      }
      row("3")
    }

    assertEquals(
      """
      |12
      |1─
      |13
      """.trimMargin(),
      table.renderText(),
    )
  }

  @Test fun rowAndColumnSpans() {
    val table = table {
      row {
        cell("333\n333\n333") {
          rowSpan = 3
          columnSpan = 3
        }
        cell("1")
        cell("1")
        cell("1")
      }
      row {
        cell("22\n22") {
          rowSpan = 2
          columnSpan = 2
        }
        cell("1")
      }
      row("1")
      row {
        cell("22\n22") {
          rowSpan = 2
          columnSpan = 2
        }
        cell("333\n333\n333") {
          rowSpan = 3
          columnSpan = 3
        }
        cell("1")
      }
      row("1")
      row("1", "1", "1")
      row("1", "1", "1", "1", "1", "1")
    }

    assertEquals(
      """
      |333111
      |333221
      |333221
      |223331
      |223331
      |113331
      |111111
      """.trimMargin(),
      table.renderText(),
    )
  }

  @Test fun rowSpanAtEndOfRow() {
    // This test ensures that row span carries at the end of a row are decremented properly
    // even when there are no remaining cells.

    val table = table {
      row {
        cell("1")
        cell("2\n2") {
          rowSpan = 2
        }
      }
      row("1")
      row("1", "1")
    }

    assertEquals(
      """
      |12
      |12
      |11
      """.trimMargin(),
      table.renderText(),
    )
  }

  @Test fun rowSpanLeaveHole() {
    val table = table {
      row {
        cell("1")
        cell("2\n2") {
          rowSpan = 2
        }
        cell("1")
        cell("2\n2") {
          rowSpan = 2
        }
      }
      row("1")
      row("1", "1", "1", "1")
    }

    assertEquals(
      """
      |1212
      |12 2
      |1111
      """.trimMargin(),
      table.renderText(),
    )
    assertNull(table.getOrNull(1, 2))
    assertNotNull(table.getOrNull(1, 3))
  }

  @Test fun rowSpanPartialOverlaps() {
    val table = table {
      row {
        cell("1")
        cell("2\n2") {
          rowSpan = 2
        }
      }
      row {
        cell("2\n2") {
          rowSpan = 2
        }
      }
      row("1")
      row("1", "1")
    }

    assertEquals(
      """
      |12
      |22
      |21
      |11
      """.trimMargin(),
      table.renderText(),
    )
  }
}
