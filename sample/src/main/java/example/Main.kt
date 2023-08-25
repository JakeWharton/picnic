@file:JvmName("Main")

package example

import com.github.ajalt.mordant.TermColors
import com.jakewharton.picnic.TextAlignment.TopCenter
import com.jakewharton.picnic.TextBorder.Companion.ROUNDED
import com.jakewharton.picnic.renderText
import com.jakewharton.picnic.table

fun main() {
  println()
  println(
    table {
      with(TermColors()) {
        header {
          cellStyle {
            borderBottom = true
          }
          row {
            cell(brightBlue.bg(" Picnic Tables ")) {
              alignment = TopCenter
              columnSpan = 6
            }
          }
        }
        body {
          cellStyle {
            border = true
            paddingLeft = 1
            paddingRight = 1
            alignment = TopCenter
          }
          row {
            cell("borders${blue("?")}")
            cell("padding${red("?")}")
            cell("styling${yellow("?")}")
            cell("headers/footers${green("?")}")
            cell("row/col spans${magenta("?")}")
            cell("alignment${cyan("?")}")
          }
          row {
            repeat(6) {
              cell(brightGreen("Yes!"))
            }
          }
        }
      }
    }.renderText(border = ROUNDED),
  )
}
