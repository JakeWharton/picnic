package com.jakewharton.picnic

import com.google.common.truth.Truth.assertThat
import com.jakewharton.picnic.BorderStyle.Hidden
import com.jakewharton.picnic.TextAlignment.BottomCenter
import com.jakewharton.picnic.TextAlignment.BottomLeft
import com.jakewharton.picnic.TextAlignment.MiddleRight
import org.junit.Test

class RepresentativeKotlinDslTest {
  @Test fun test() {
    val table = table {
      style {
        borderStyle = Hidden
      }
      cellStyle {
        alignment = MiddleRight
        paddingLeft = 1
        paddingRight = 1
        borderLeft = true
        borderRight = true
      }
      header {
        cellStyle {
          border = true
          alignment = BottomLeft
        }
        row {
          cell("APK") {
            rowSpan = 2
          }
          cell("compressed") {
            alignment = BottomCenter
            columnSpan = 3
          }
          cell("uncompressed") {
            alignment = BottomCenter
            columnSpan = 3
          }
        }
        row("old", "new", "diff", "old", "new", "diff")
      }
      body {
        row("dex", "664.8 KiB", "664.8 KiB", "-25 B", "1.5 MiB", "1.5 MiB", "-112 B")
        row("arsc", "201.7 KiB", "201.7 KiB", "0 B", "201.6 KiB", "201.6 KiB", "0 B")
        row("manifest", "1.4 KiB", "1.4 KiB", "0 B", "4.2 KiB", "4.2 KiB", "0 B")
        row("res", "418.2 KiB", "418.2 KiB", "-14 B", "488.3 KiB", "488.3 KiB", "0 B")
        row("asset", "0 B", "0 B", "0 B", "0 B", "0 B", "0 B")
        row("other", "37.1 KiB", "37.1 KiB", "0 B", "36.3 KiB", "36.3 KiB", "0 B")
      }
      footer {
        cellStyle {
          border = true
        }
        row("total", "1.3 MiB", "1.3 MiB", "-39 B", "2.2 MiB", "2.2 MiB", "-112 B")
      }
    }
    assertThat(table.toString()).isEqualTo(
      """
      |          │          compressed           │          uncompressed          
      |          ├───────────┬───────────┬───────┼───────────┬───────────┬────────
      | APK      │ old       │ new       │ diff  │ old       │ new       │ diff   
      |──────────┼───────────┼───────────┼───────┼───────────┼───────────┼────────
      |      dex │ 664.8 KiB │ 664.8 KiB │ -25 B │   1.5 MiB │   1.5 MiB │ -112 B 
      |     arsc │ 201.7 KiB │ 201.7 KiB │   0 B │ 201.6 KiB │ 201.6 KiB │    0 B 
      | manifest │   1.4 KiB │   1.4 KiB │   0 B │   4.2 KiB │   4.2 KiB │    0 B 
      |      res │ 418.2 KiB │ 418.2 KiB │ -14 B │ 488.3 KiB │ 488.3 KiB │    0 B 
      |    asset │       0 B │       0 B │   0 B │       0 B │       0 B │    0 B 
      |    other │  37.1 KiB │  37.1 KiB │   0 B │  36.3 KiB │  36.3 KiB │    0 B 
      |──────────┼───────────┼───────────┼───────┼───────────┼───────────┼────────
      |    total │   1.3 MiB │   1.3 MiB │ -39 B │   2.2 MiB │   2.2 MiB │ -112 B 
      |""".trimMargin()
    )
  }
}
