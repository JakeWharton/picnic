package com.jakewharton.picnic

import java.util.Objects.hash

class Table private constructor(
  val header: TableSection?,
  val body: TableSection,
  val footer: TableSection?,
  val cellStyle: CellStyle?,
  val tableStyle: TableStyle?
) {
  override fun toString() = renderText()
  override fun hashCode() = hash(header, body, footer, cellStyle, tableStyle)
  override fun equals(other: Any?) = other is Table &&
      header == other.header &&
      body == other.body &&
      footer == other.footer &&
      cellStyle == other.cellStyle &&
      tableStyle == other.tableStyle

  val rowCount: Int = (header?.rows?.size ?: 0) + body.rows.size + (footer?.rows?.size ?: 0)
  val columnCount: Int
  val positionedCells: List<PositionedCell>

  private val cellTable: List<List<PositionedCell?>>

  init {
    val rowSpanCarries = IntCounts()
    val positionedCells = mutableListOf<PositionedCell>()
    val cellTable = mutableListOf<MutableList<PositionedCell?>>()
    var rowIndex = 0
    listOfNotNull(header, body, footer).forEach { section ->
      val sectionStyle = cellStyle + section.cellStyle

      section.rows.forEach { row ->
        val rowStyle = sectionStyle + row.cellStyle

        val cellRow = mutableListOf<PositionedCell?>()
        cellTable += cellRow

        var columnIndex = 0
        row.cells.forEachIndexed { rawColumnIndex, cell ->
          // Check for any previous rows' cells whose >1 rowSpan carries them into this row.
          // When found, add them to the current row, pushing remaining cells to the right.
          while (columnIndex < rowSpanCarries.size && rowSpanCarries[columnIndex] > 0) {
            cellRow += cellTable[rowIndex - 1][columnIndex]
            rowSpanCarries[columnIndex]--
            columnIndex++
          }

          val canonicalStyle = rowStyle + cell.style
          val positionedCell = PositionedCell(rowIndex, columnIndex, cell, canonicalStyle)
          positionedCells += positionedCell

          val rowSpan = cell.rowSpan
          require(rowIndex + rowSpan <= rowCount) {
            "Cell $rawColumnIndex in row $rowIndex has rowSpan=$rowSpan but table rowCount=$rowCount"
          }

          val rowSpanCarry = rowSpan - 1
          repeat(cell.columnSpan) {
            cellRow += positionedCell
            rowSpanCarries[columnIndex] = rowSpanCarry
            columnIndex++
          }
        }

        // Check for any previous rows' cells whose >1 rowSpan carries them into this row.
        // When found, add them to the current row, filling any gaps with null.
        while (columnIndex < rowSpanCarries.size) {
          if (rowSpanCarries[columnIndex] > 0) {
            cellRow += cellTable[rowIndex - 1][columnIndex]
            rowSpanCarries[columnIndex]--
          } else {
            cellRow.add(null)
          }
          columnIndex++
        }

        rowIndex++
      }
    }

    columnCount = rowSpanCarries.size
    this.positionedCells = positionedCells
    this.cellTable = cellTable
  }

  fun getOrNull(row: Int, column: Int) = cellTable.getOrNull(row)?.getOrNull(column)

  operator fun get(row: Int, column: Int) = requireNotNull(cellTable[row][column]) {
    "Cell was null"
  }

  class PositionedCell(
    val rowIndex: Int,
    val columnIndex: Int,
    val cell: Cell,
    val canonicalStyle: CellStyle?
  ) {
    override fun hashCode() = hash(rowIndex, columnIndex, cell, canonicalStyle)
    override fun equals(other: Any?) = other is PositionedCell &&
        rowIndex == other.rowIndex &&
        columnIndex == other.columnIndex &&
        cell == other.cell &&
        canonicalStyle == other.canonicalStyle

    override fun toString() =
      "PositionedCell(rowIndex=$rowIndex, colIndex=$columnIndex, cell=$cell, " +
          "canonicalStyle=$canonicalStyle)"
  }

  class Builder {
    @set:JvmSynthetic // Hide 'void' setter from Java.
    var header: TableSection? = null

    fun setHeader(header: TableSection?) = apply {
      this.header = header
    }

    @set:JvmSynthetic // Hide 'void' setter from Java.
    var body: TableSection? = null

    fun setBody(body: TableSection?) = apply {
      this.body = body
    }

    @set:JvmSynthetic // Hide 'void' setter from Java.
    var footer: TableSection? = null

    fun setFooter(footer: TableSection?) = apply {
      this.footer = footer
    }

    @set:JvmSynthetic // Hide 'void' setter from Java.
    var cellStyle: CellStyle? = null

    fun setCellStyle(cellStyle: CellStyle?) = apply {
      this.cellStyle = cellStyle
    }

    @set:JvmSynthetic // Hide 'void' setter from Java.
    var tableStyle: TableStyle? = null

    fun setTableStyle(tableStyle: TableStyle?) = apply {
      this.tableStyle = tableStyle
    }

    fun build() = Table(
        header,
        checkNotNull(body) { "Body section is required" },
        footer,
        cellStyle,
        tableStyle
    )
  }
}

@JvmSynthetic // Hide from Java callers who should use Builder.
fun Table(initializer: Table.Builder.() -> Unit): Table {
  return Table.Builder().apply(initializer).build()
}

class TableStyle private constructor(
  val borderStyle: BorderStyle?
) {
  override fun toString() = "TableStyle(borderStyle=$borderStyle)"
  override fun hashCode() = borderStyle.hashCode()
  override fun equals(other: Any?) = other is TableStyle &&
      borderStyle == other.borderStyle

  class Builder {
    @set:JvmSynthetic // Hide 'void' setter from Java.
    var borderStyle: BorderStyle? = null

    fun setBorderStyle(borderStyle: BorderStyle?) = apply {
      this.borderStyle = borderStyle
    }

    fun build() = TableStyle(borderStyle)
  }
}

@JvmSynthetic // Hide from Java callers who should use Builder.
fun TableStyle(initializer: TableStyle.Builder.() -> Unit): TableStyle {
  return TableStyle.Builder().apply(initializer).build()
}

enum class BorderStyle {
  Hidden, Solid
}

class TableSection private constructor(
  val rows: List<Row>,
  val cellStyle: CellStyle?
) {
  override fun toString() = "TableSection(rows=$rows, cellStyle=$cellStyle)"
  override fun hashCode() = hash(rows, cellStyle)
  override fun equals(other: Any?) = other is TableSection &&
      rows == other.rows &&
      cellStyle == other.cellStyle

  class Builder {
    @set:JvmSynthetic // Hide 'void' setter from Java.
    var rows: MutableList<Row> = mutableListOf()

    fun setRows(rows: List<Row>) = apply {
      this.rows = rows.toMutableList()
    }

    fun addRow(row: Row) = apply {
      this.rows.add(row)
    }

    fun addRow(vararg cells: Cell) = addRow(Row { this.cells.addAll(cells) })

    fun addRow(vararg cells: String) = addRow(Row { this.cells.addAll(cells.map { Cell(it) }) })

    @set:JvmSynthetic // Hide 'void' setter from Java.
    var cellStyle: CellStyle? = null

    fun setCellStyle(cellStyle: CellStyle?) = apply {
      this.cellStyle = cellStyle
    }

    fun build() = TableSection(rows.toList(), cellStyle)
  }
}

@JvmSynthetic // Hide from Java callers who should use Builder.
fun TableSection(initializer: TableSection.Builder.() -> Unit): TableSection {
  return TableSection.Builder().apply(initializer).build()
}

class Row private constructor(
  val cells: List<Cell>,
  val cellStyle: CellStyle?
) {
  override fun toString() = "Row(cells=$cells, cellStyle=$cellStyle)"
  override fun hashCode() = hash(cells, cellStyle)
  override fun equals(other: Any?) = other is Row &&
      cells == other.cells &&
      cellStyle == other.cellStyle

  class Builder {
    @set:JvmSynthetic // Hide 'void' setter from Java.
    var cells: MutableList<Cell> = mutableListOf()

    fun setCells(cells: List<Cell>) = apply {
      this.cells = cells.toMutableList()
    }

    fun addCell(cell: Cell) = apply {
      cells.add(cell)
    }

    fun addCell(cell: String) = apply {
      cells.add(Cell(cell))
    }

    @set:JvmSynthetic // Hide 'void' setter from Java.
    var cellStyle: CellStyle? = null

    fun setCellStyle(cellStyle: CellStyle?) = apply {
      this.cellStyle = cellStyle
    }

    fun build() = Row(cells.toList(), cellStyle)
  }
}

@JvmSynthetic // Hide from Java callers who should use Builder.
fun Row(initializer: Row.Builder.() -> Unit): Row {
  return Row.Builder().apply(initializer).build()
}

class Cell private constructor(
  val content: String,
  val columnSpan: Int,
  val rowSpan: Int,
  val style: CellStyle?
) {
  override fun toString() =
    "Cell(content=$content, columnSpan=$columnSpan, rowSpan=$rowSpan, style=$style)"

  override fun hashCode() = hash(content, columnSpan, rowSpan, style)
  override fun equals(other: Any?) = other is Cell &&
      content == other.content &&
      columnSpan == other.columnSpan &&
      rowSpan == other.rowSpan &&
      style == other.style

  class Builder(val content: Any?) {
    @set:JvmSynthetic // Hide 'void' setter from Java.
    var columnSpan: Int = 1

    fun setColumnSpan(columnSpan: Int) = apply {
      this.columnSpan = columnSpan
    }

    @set:JvmSynthetic // Hide 'void' setter from Java.
    var rowSpan: Int = 1

    fun setRowSpan(rowSpan: Int) = apply {
      this.rowSpan = rowSpan
    }

    @set:JvmSynthetic // Hide 'void' setter from Java.
    var style: CellStyle? = null

    fun setStyle(style: CellStyle?) = apply {
      this.style = style
    }

    fun build() = Cell(content.toString(), columnSpan, rowSpan, style)
  }
}

@JvmSynthetic // Hide from Java callers who should use Builder.
fun Cell(content: Any?, initializer: Cell.Builder.() -> Unit = {}): Cell {
  return Cell.Builder(content).apply(initializer).build()
}

class CellStyle private constructor(
  val paddingLeft: Int?,
  val paddingRight: Int?,
  val paddingTop: Int?,
  val paddingBottom: Int?,
  val borderLeft: Boolean?,
  val borderRight: Boolean?,
  val borderTop: Boolean?,
  val borderBottom: Boolean?,
  val alignment: TextAlignment?
) {
  override fun toString() =
    "CellStyle(padding(l=$paddingLeft,r=$paddingRight,t=$paddingTop,b=$paddingBottom), " +
        "border(l=$borderLeft,r=$borderRight,t=$borderTop,b=$borderBottom), alignment=$alignment)"

  override fun hashCode() = hash(
      paddingLeft, paddingRight, paddingTop, paddingBottom,
      borderLeft, borderRight, borderTop, borderBottom,
      alignment
  )

  override fun equals(other: Any?) = other is CellStyle &&
      paddingLeft == other.paddingLeft &&
      paddingRight == other.paddingRight &&
      paddingTop == other.paddingTop &&
      paddingBottom == other.paddingBottom &&
      borderLeft == other.borderLeft &&
      borderRight == other.borderRight &&
      borderTop == other.borderTop &&
      borderBottom == other.borderBottom &&
      alignment == other.alignment

  class Builder {
    @set:JvmSynthetic // Hide 'void' setter from Java.
    var paddingLeft: Int? = null

    fun setPaddingLeft(paddingLeft: Int?) = apply {
      this.paddingLeft = paddingLeft
    }

    @set:JvmSynthetic // Hide 'void' setter from Java.
    var paddingRight: Int? = null

    fun setPaddingRight(paddingRight: Int?) = apply {
      this.paddingRight = paddingRight
    }

    @set:JvmSynthetic // Hide 'void' setter from Java.
    var paddingTop: Int? = null

    fun setPaddingTop(paddingTop: Int?) = apply {
      this.paddingTop = paddingTop
    }

    @set:JvmSynthetic // Hide 'void' setter from Java.
    var paddingBottom: Int? = null

    fun setPaddingBottom(paddingBottom: Int?) = apply {
      this.paddingBottom = paddingBottom
    }

    fun setPadding(padding: Int?) = apply {
      paddingLeft = padding
      paddingRight = padding
      paddingTop = padding
      paddingBottom = padding
    }

    @set:JvmSynthetic // Hide 'void' setter from Java.
    var borderLeft: Boolean? = null

    fun setBorderLeft(borderLeft: Boolean?) = apply {
      this.borderLeft = borderLeft
    }

    @set:JvmSynthetic // Hide 'void' setter from Java.
    var borderRight: Boolean? = null

    fun setBorderRight(borderRight: Boolean?) = apply {
      this.borderRight = borderRight
    }

    @set:JvmSynthetic // Hide 'void' setter from Java.
    var borderTop: Boolean? = null

    fun setBorderTop(borderTop: Boolean?) = apply {
      this.borderTop = borderTop
    }

    @set:JvmSynthetic // Hide 'void' setter from Java.
    var borderBottom: Boolean? = null

    fun setBorderBottom(borderBottom: Boolean?) = apply {
      this.borderBottom = borderBottom
    }

    fun setBorder(border: Boolean?) = apply {
      borderLeft = border
      borderRight = border
      borderTop = border
      borderBottom = border
    }

    @set:JvmSynthetic // Hide 'void' setter from Java.
    var alignment: TextAlignment? = null

    fun setAlignment(alignment: TextAlignment?) = apply {
      this.alignment = alignment
    }

    fun build() = CellStyle(
        paddingLeft, paddingRight, paddingTop, paddingBottom,
        borderLeft, borderRight, borderTop, borderBottom,
        alignment
    )
  }
}

@JvmSynthetic // Hide from Java callers who should use Builder.
fun CellStyle(initializer: CellStyle.Builder.() -> Unit): CellStyle {
  return CellStyle.Builder().apply(initializer).build()
}

private operator fun CellStyle?.plus(override: CellStyle?): CellStyle? {
  if (this == null) {
    return override
  }
  if (override == null) {
    return this
  }
  return CellStyle {
    paddingLeft = override.paddingLeft ?: this@plus.paddingLeft
    paddingRight = override.paddingRight ?: this@plus.paddingRight
    paddingTop = override.paddingTop ?: this@plus.paddingTop
    paddingBottom = override.paddingBottom ?: this@plus.paddingBottom
    borderLeft = override.borderLeft ?: this@plus.borderLeft
    borderRight = override.borderRight ?: this@plus.borderRight
    borderTop = override.borderTop ?: this@plus.borderTop
    borderBottom = override.borderBottom ?: this@plus.borderBottom
    alignment = override.alignment ?: this@plus.alignment
  }
}

enum class TextAlignment {
  TopLeft, TopCenter, TopRight,
  MiddleLeft, MiddleCenter, MiddleRight,
  BottomLeft, BottomCenter, BottomRight
}
