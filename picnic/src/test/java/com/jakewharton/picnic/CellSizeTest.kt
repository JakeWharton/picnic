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

    assertThat(table.renderText()).isEqualTo("""
      |111
      |22 
      |3  
      |111
      |2 2
      |  3
      |111
      | 22
      | 3 
      |""".trimMargin())
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

    assertThat(table.renderText()).isEqualTo("""
      |1 3
      | 2 
      |1  
      | 23
      |   
      |""".trimMargin())
  }

  @Test fun width() {
    val table = table {
      row("123", "12", "1")
      row("12", "1", "123")
      row("1", "123", "12")
    }

    assertThat(table.renderText()).isEqualTo("""
      |12312 1  
      |12 1  123
      |1  12312 
      |""".trimMargin())
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

    assertThat(table.renderText()).isEqualTo("""
      |1  23  
      |1 2  3 
      |""".trimMargin())
  }

  @Test fun widthAndHeight() {
    val table = table {
      row("123\n12\n1", "12\n1", "1")
      row("12\n1", "1", "123\n12\n1")
      row("1", "123\n12\n1", "12\n1")
    }

    assertThat(table.renderText()).isEqualTo("""
      |12312 1  
      |12 1     
      |1        
      |12 1  123
      |1     12 
      |      1  
      |1  12312 
      |   12 1  
      |   1     
      |""".trimMargin())
  }

  @Test fun unicode() {
    val table = table {
      row('\u0031') //1 utf-8 bytes
      row('\u00A3') //2 utf-8 bytes
      row('\u20AC') //3 utf-8 bytes
      row('\u5317') //3 utf-8 bytes, full-width
      row(String(Character.toChars(0x1F603))) //4 utf-8 bytes (2*utf-16), full-width
    }

    assertThat(table.renderText()).isEqualTo("""
      |1
      |£
      |€
      |北
      |😃
      |""".trimMargin())
  }

  //Rows contain mixture of BMP and supplementary codepoints. This exercises a bug in the initial
  //codepoint implementation
  @Test fun mixedWidth() {
    val table = table {
      row() //Should be 5 spaces
      row("😃.😃.😃") //2 utf-8 bytes
      row(".😃.😃.") //2 utf-8 bytes
    }

    assertThat(table.renderText()).isEqualTo("""
      |     
      |😃.😃.😃
      |.😃.😃.
      |""".trimMargin())

  }
}
