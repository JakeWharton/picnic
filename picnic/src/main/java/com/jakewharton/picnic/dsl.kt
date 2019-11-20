@file:JvmName("-DslKt")

package com.jakewharton.picnic

import kotlin.DeprecationLevel.ERROR

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
  var borderStyle: BorderStyle?
}

@PicnicDsl
interface TableSectionDsl {
  @JvmDefault
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

  @JvmDefault
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

  @JvmDefault
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

  fun create() = Table(
      headerImpl.createOrNull(),
      bodyImpl.create(),
      footerImpl.createOrNull(),
      cellStyleImpl.createOrNull(),
      tableStyleImpl.createOrNull())
}

private class TableSectionDslImpl : TableSectionDsl {
  private val rows = mutableListOf<Row>()
  private val cellStyleImpl = CellStyleDslImpl()

  override fun row(content: RowDsl.() -> Unit) {
    rows += RowDslImpl().apply(content).create()
  }

  override fun cellStyle(content: CellStyleDsl.() -> Unit) {
    cellStyleImpl.apply(content)
  }

  fun createOrNull() = if (rows.isEmpty()) null else create()
  fun create() = TableSection(rows.toList(), cellStyleImpl.createOrNull())
}

private class RowDslImpl : RowDsl {
  private val cells = mutableListOf<Cell>()
  private val cellStyleImpl = CellStyleDslImpl()

  override fun cell(content: Any?, style: CellDsl.() -> Unit) {
    cells += CellDslImpl(content).apply(style).create()
  }

  override fun cellStyle(content: CellStyleDsl.() -> Unit) {
    cellStyleImpl.apply(content)
  }

  fun create() = Row(cells.toList(), cellStyleImpl.createOrNull())
}

private class CellDslImpl private constructor(
  private val content: Any?,
  private val cellStyleImpl: CellStyleDslImpl
) : CellDsl, CellStyleDsl by cellStyleImpl {

  constructor(content: Any?) : this(content, CellStyleDslImpl())

  override var columnSpan: Int = 1
  override var rowSpan: Int = 1

  fun create(): Cell {
    return Cell(
        content = content?.toString() ?: "",
        columnSpan = columnSpan,
        rowSpan = rowSpan,
        style = cellStyleImpl.createOrNull()
    )
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
      return CellStyle(
          paddingLeft = paddingLeft,
          paddingRight = paddingRight,
          paddingTop = paddingTop,
          paddingBottom = paddingBottom,
          borderLeft = borderLeft,
          borderRight = borderRight,
          borderTop = borderTop,
          borderBottom = borderBottom,
          alignment = alignment
      )
    }
    return null
  }
}

private class TableStyleDslImpl : TableStyleDsl {
  override var borderStyle: BorderStyle? = null

  fun createOrNull(): TableStyle? {
    if (borderStyle != null) {
      return TableStyle(borderStyle)
    }
    return null
  }
}
