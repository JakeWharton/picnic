@file:JvmName("-DslKt")

package com.jakewharton.picnic

import kotlin.DeprecationLevel.ERROR
import kotlin.jvm.JvmName
import kotlin.jvm.JvmSynthetic

@DslMarker
private annotation class PicnicDsl

fun table(content: TableDsl.() -> Unit) = TableDslImpl().apply(content).create()

@PicnicDsl
interface TableDsl : TableSectionDsl {
  fun header(content: TableSectionDsl.() -> Unit)
  fun body(content: TableSectionDsl.() -> Unit)
  fun footer(content: TableSectionDsl.() -> Unit)
  fun style(content: TableStyleDsl.() -> Unit)
}

@PicnicDsl
interface TableStyleDsl {
  var border: Boolean?
  var borderStyle: BorderStyle?
}

@PicnicDsl
interface TableSectionDsl {
  fun row(vararg cells: Any?) {
    row {
      cells.forEach { cell(it) }
    }
  }

  fun row(content: RowDsl.() -> Unit)

  fun cellStyle(content: CellStyleDsl.() -> Unit)
}

@PicnicDsl
interface RowDsl {
  fun cell(content: Any?, style: CellDsl.() -> Unit = {})

  fun cells(vararg content: Any?, style: CellDsl.() -> Unit = {}) {
    content.forEach { cell(it, style) }
  }

  fun cellStyle(content: CellStyleDsl.() -> Unit)
}

@PicnicDsl
interface CellDsl : CellStyleDsl {
  var columnSpan: Int
  var rowSpan: Int
}

@PicnicDsl
interface CellStyleDsl {
  var paddingLeft: Int?
  var paddingRight: Int?
  var paddingTop: Int?
  var paddingBottom: Int?

  var borderLeft: Boolean?
  var borderRight: Boolean?
  var borderTop: Boolean?
  var borderBottom: Boolean?

  var alignment: TextAlignment?

  var border: Boolean
    @JvmSynthetic
    @Deprecated("Use individual getters", level = ERROR)
    get() = throw UnsupportedOperationException()
    set(value) {
      borderLeft = value
      borderRight = value
      borderTop = value
      borderBottom = value
    }

  var padding: Int
    @JvmSynthetic
    @Deprecated("Use individual getters", level = ERROR)
    get() = throw UnsupportedOperationException()
    set(value) {
      paddingLeft = value
      paddingRight = value
      paddingTop = value
      paddingBottom = value
    }
}

private class TableDslImpl : TableDsl {
  private val headerImpl = TableSectionDslImpl()
  private val bodyImpl = TableSectionDslImpl()
  private val footerImpl = TableSectionDslImpl()
  private val cellStyleImpl = CellStyleDslImpl()
  private val tableStyleImpl = TableStyleDslImpl()

  override fun header(content: TableSectionDsl.() -> Unit) {
    headerImpl.apply(content)
  }

  override fun body(content: TableSectionDsl.() -> Unit) {
    bodyImpl.apply(content)
  }

  override fun footer(content: TableSectionDsl.() -> Unit) {
    footerImpl.apply(content)
  }

  override fun row(content: RowDsl.() -> Unit) {
    bodyImpl.row(content)
  }

  override fun cellStyle(content: CellStyleDsl.() -> Unit) {
    cellStyleImpl.apply(content)
  }

  override fun style(content: TableStyleDsl.() -> Unit) {
    tableStyleImpl.apply(content)
  }

  fun create() = Table {
    header = headerImpl.createOrNull()
    body = bodyImpl.create()
    footer = footerImpl.createOrNull()
    cellStyle = cellStyleImpl.createOrNull()
    tableStyle = tableStyleImpl.createOrNull()
  }
}

private class TableSectionDslImpl : TableSectionDsl {
  private val builder = TableSection.Builder()
  private val cellStyleImpl = CellStyleDslImpl()

  override fun row(content: RowDsl.() -> Unit) {
    builder.addRow(RowDslImpl().apply(content).create())
  }

  override fun cellStyle(content: CellStyleDsl.() -> Unit) {
    cellStyleImpl.apply(content)
  }

  fun createOrNull() = if (builder.rows.isEmpty()) null else create()
  fun create() = builder.setCellStyle(cellStyleImpl.createOrNull()).build()
}

private class RowDslImpl : RowDsl {
  private val builder = Row.Builder()
  private val cellStyleImpl = CellStyleDslImpl()

  override fun cell(content: Any?, style: CellDsl.() -> Unit) {
    builder.addCell(CellDslImpl(content).apply(style).create())
  }

  override fun cellStyle(content: CellStyleDsl.() -> Unit) {
    cellStyleImpl.apply(content)
  }

  fun create() = builder.setCellStyle(cellStyleImpl.createOrNull()).build()
}

private class CellDslImpl private constructor(
  private val content: Any?,
  private val cellStyleImpl: CellStyleDslImpl,
) : CellDsl, CellStyleDsl by cellStyleImpl {

  constructor(content: Any?) : this(content, CellStyleDslImpl())

  override var columnSpan: Int = 1
  override var rowSpan: Int = 1

  fun create() = Cell(content?.toString() ?: "") {
    columnSpan = this@CellDslImpl.columnSpan
    rowSpan = this@CellDslImpl.rowSpan
    style = cellStyleImpl.createOrNull()
  }
}

private class CellStyleDslImpl : CellStyleDsl {
  override var paddingLeft: Int? = null
  override var paddingRight: Int? = null
  override var paddingTop: Int? = null
  override var paddingBottom: Int? = null
  override var borderLeft: Boolean? = null
  override var borderRight: Boolean? = null
  override var borderTop: Boolean? = null
  override var borderBottom: Boolean? = null
  override var alignment: TextAlignment? = null

  fun createOrNull(): CellStyle? {
    if (paddingLeft != null ||
      paddingRight != null ||
      paddingTop != null ||
      paddingBottom != null ||
      borderLeft != null ||
      borderRight != null ||
      borderTop != null ||
      borderBottom != null ||
      alignment != null
    ) {
      return CellStyle {
        paddingLeft = this@CellStyleDslImpl.paddingLeft
        paddingRight = this@CellStyleDslImpl.paddingRight
        paddingTop = this@CellStyleDslImpl.paddingTop
        paddingBottom = this@CellStyleDslImpl.paddingBottom
        borderLeft = this@CellStyleDslImpl.borderLeft
        borderRight = this@CellStyleDslImpl.borderRight
        borderTop = this@CellStyleDslImpl.borderTop
        borderBottom = this@CellStyleDslImpl.borderBottom
        alignment = this@CellStyleDslImpl.alignment
      }
    }
    return null
  }
}

private class TableStyleDslImpl : TableStyleDsl {
  override var border: Boolean? = null
  override var borderStyle: BorderStyle? = null

  fun createOrNull(): TableStyle? {
    if (border != null || borderStyle != null) {
      return TableStyle {
        border = this@TableStyleDslImpl.border
        borderStyle = this@TableStyleDslImpl.borderStyle
      }
    }
    return null
  }
}
