package com.jakewharton.picnic

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CellSizeTest {
  @Test fun height() {
    val table = table {
      row("1\n2\n3", "1\n2", "1")
      row("1\n2", "1", "1\n2\n3")
      row("1", "1\n2\n3", "1\n2")
    }

    assertThat(table.renderText()).isEqualTo(
      """
      |111
      |22 
      |3  
      |111
      |2 2
      |  3
      |111
      | 22
      | 3 
      |""".trimMargin()
    )
  }

  @Test fun heightWithVerticalPadding() {
    val table = table {
      row {
        cell(1)
        cell(2) {
          paddingTop = 1
        }
        cell(3) {
          paddingBottom = 1
        }
      }
      row {
        cell(1)
        cell(2) {
          paddingTop = 1
          paddingBottom = 1
        }
        cell(3) {
          paddingTop = 1
          paddingBottom = 1
        }
      }
    }

    assertThat(table.renderText()).isEqualTo(
      """
      |1 3
      | 2 
      |1  
      | 23
      |   
      |""".trimMargin()
    )
  }

  @Test fun width() {
    val table = table {
      row("123", "12", "1")
      row("12", "1", "123")
      row("1", "123", "12")
    }

    assertThat(table.renderText()).isEqualTo(
      """
      |12312 1  
      |12 1  123
      |1  12312 
      |""".trimMargin()
    )
  }

  @Test fun widthWithHorizontalPadding() {
    val table = table {
      row {
        cell(1)
        cell(2) {
          paddingLeft = 2
        }
        cell(3) {
          paddingRight = 2
        }
      }
      row {
        cell(1)
        cell(2) {
          paddingLeft = 1
          paddingRight = 1
        }
        cell(3) {
          paddingLeft = 1
          paddingRight = 1
        }
      }
    }

    assertThat(table.renderText()).isEqualTo(
      """
      |1  23  
      |1 2  3 
      |""".trimMargin()
    )
  }

  @Test fun widthAndHeight() {
    val table = table {
      row("123\n12\n1", "12\n1", "1")
      row("12\n1", "1", "123\n12\n1")
      row("1", "123\n12\n1", "12\n1")
    }

    assertThat(table.renderText()).isEqualTo(
      """
      |12312 1  
      |12 1     
      |1        
      |12 1  123
      |1     12 
      |      1  
      |1  12312 
      |   12 1  
      |   1     
      |""".trimMargin()
    )
  }

  @Test fun unicode() {
    val table = table {
      // 1 UTF-8 bytes.
      row('\u0031', 'a')
      // 2 UTF-8 bytes.
      row('\u00A3', 'a')
      // 3 UTF-8 bytes.
      row('\u20AC', 'a')
      // 3 UTF-8 bytes, full-width.
      row('\u5317', 'a')
      // 4 UTF-8 bytes (2 * UTF-16), full-width.
      row(String(Character.toChars(0x1F603)), 'a')
    }

    assertThat(table.renderText()).isEqualTo(
      """
      |1a
      |£a
      |€a
      |北a
      |😃a
      |""".trimMargin()
    )
  }

  @Test fun mixedWidth() {
    // Rows contain mixture of BMP and supplementary codepoints.
    val table = table {
      row("a", "a")
      // 2 UTF-8 bytes.
      row("😃.😃.😃", 'a')
      // 2 UTF-8 bytes.
      row(".😃.😃.", 'a')
    }

    assertThat(table.renderText()).isEqualTo(
      """
      |a    a
      |😃.😃.😃a
      |.😃.😃.a
      |""".trimMargin()
    )
  }

  @Test fun asniEscapeCodesAreNotMeasured() {
    val table = table {
      row("a")
      row("\u001B[31;1;4ma\u001B[0m")
    }
    assertThat(table.columnCount).isEqualTo(1)

    assertThat(table.renderText()).isEqualTo(
      """
      |a
      |$esc[31;1;4ma$esc[0m
      |""".trimMargin()
    )
  }
}

private const val esc = "\u001B"
