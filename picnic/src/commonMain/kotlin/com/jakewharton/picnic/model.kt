package com.jakewharton.picnic

import kotlin.jvm.JvmSynthetic

public class Table
private constructor(
  public val header: TableSection?,
  public val body: TableSection,
  public val footer: TableSection?,
  public val cellStyle: CellStyle?,
  public val tableStyle: TableStyle?,
) {
  override fun toString(): String = renderText()

  override fun hashCode(): Int = hash(header, body, footer, cellStyle, tableStyle)

  override fun equals(other: Any?): Boolean =
    other is Table &&
      header == other.header &&
      body == other.body &&
      footer == other.footer &&
      cellStyle == other.cellStyle &&
      tableStyle == other.tableStyle

  public val rowCount: Int = (header?.rows?.size ?: 0) + body.rows.size + (footer?.rows?.size ?: 0)
  public val columnCount: Int
  public val positionedCells: List<PositionedCell>

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

  public fun getOrNull(row: Int, column: Int): PositionedCell? =
    cellTable.getOrNull(row)?.getOrNull(column)

  public operator fun get(row: Int, column: Int): PositionedCell =
    requireNotNull(cellTable[row][column]) { "Cell was null" }

  public class PositionedCell(
    public val rowIndex: Int,
    public val columnIndex: Int,
    public val cell: Cell,
    public val canonicalStyle: CellStyle?,
  ) {
    override fun hashCode(): Int = hash(rowIndex, columnIndex, cell, canonicalStyle)

    override fun equals(other: Any?): Boolean =
      other is PositionedCell &&
        rowIndex == other.rowIndex &&
        columnIndex == other.columnIndex &&
        cell == other.cell &&
        canonicalStyle == other.canonicalStyle

    override fun toString(): String =
      "PositionedCell(rowIndex=$rowIndex, colIndex=$columnIndex, cell=$cell, " +
        "canonicalStyle=$canonicalStyle)"
  }

  public class Builder {
    @set:JvmSynthetic // Hide 'void' setter from Java.
    public var header: TableSection? = null

    public fun setHeader(header: TableSection?): Builder = apply { this.header = header }

    @set:JvmSynthetic // Hide 'void' setter from Java.
    public var body: TableSection? = null

    public fun setBody(body: TableSection?): Builder = apply { this.body = body }

    @set:JvmSynthetic // Hide 'void' setter from Java.
    public var footer: TableSection? = null

    public fun setFooter(footer: TableSection?): Builder = apply { this.footer = footer }

    @set:JvmSynthetic // Hide 'void' setter from Java.
    public var cellStyle: CellStyle? = null

    public fun setCellStyle(cellStyle: CellStyle?): Builder = apply { this.cellStyle = cellStyle }

    @set:JvmSynthetic // Hide 'void' setter from Java.
    public var tableStyle: TableStyle? = null

    public fun setTableStyle(tableStyle: TableStyle?): Builder = apply {
      this.tableStyle = tableStyle
    }

    public fun build(): Table =
      Table(
        header,
        checkNotNull(body) { "Body section is required" },
        footer,
        cellStyle,
        tableStyle,
      )
  }
}

@JvmSynthetic // Hide from Java callers who should use Builder.
public fun Table(initializer: Table.Builder.() -> Unit): Table {
  return Table.Builder().apply(initializer).build()
}

public class TableStyle
private constructor(
  public val border: Boolean?,
  public val borderStyle: BorderStyle?,
) {
  override fun toString(): String = "TableStyle(border=$border, borderStyle=$borderStyle)"

  override fun hashCode(): Int = border.hashCode() * 37 + borderStyle.hashCode()

  override fun equals(other: Any?): Boolean =
    other is TableStyle && border == other.border && borderStyle == other.borderStyle

  public class Builder {
    @set:JvmSynthetic // Hide 'void' setter from Java.
    public var border: Boolean? = null

    public fun setBorder(border: Boolean?): Builder = apply { this.border = border }

    @set:JvmSynthetic // Hide 'void' setter from Java.
    public var borderStyle: BorderStyle? = null

    public fun setBorderStyle(borderStyle: BorderStyle?): Builder = apply {
      this.borderStyle = borderStyle
    }

    public fun build(): TableStyle = TableStyle(border, borderStyle)
  }
}

@JvmSynthetic // Hide from Java callers who should use Builder.
public fun TableStyle(initializer: TableStyle.Builder.() -> Unit): TableStyle {
  return TableStyle.Builder().apply(initializer).build()
}

public enum class BorderStyle {
  Hidden,
  Solid,
}

public class TableSection
private constructor(
  public val rows: List<Row>,
  public val cellStyle: CellStyle?,
) {
  override fun toString(): String = "TableSection(rows=$rows, cellStyle=$cellStyle)"

  override fun hashCode(): Int = hash(rows, cellStyle)

  override fun equals(other: Any?): Boolean =
    other is TableSection && rows == other.rows && cellStyle == other.cellStyle

  public class Builder {
    @set:JvmSynthetic // Hide 'void' setter from Java.
    public var rows: MutableList<Row> = mutableListOf()

    public fun setRows(rows: List<Row>): Builder = apply { this.rows = rows.toMutableList() }

    public fun addRow(row: Row): Builder = apply { this.rows.add(row) }

    public fun addRow(vararg cells: Cell): Builder = addRow(Row { this.cells.addAll(cells) })

    public fun addRow(vararg cells: String): Builder =
      addRow(Row { this.cells.addAll(cells.map { Cell(it) }) })

    @set:JvmSynthetic // Hide 'void' setter from Java.
    public var cellStyle: CellStyle? = null

    public fun setCellStyle(cellStyle: CellStyle?): Builder = apply { this.cellStyle = cellStyle }

    public fun build(): TableSection = TableSection(rows.toList(), cellStyle)
  }
}

@JvmSynthetic // Hide from Java callers who should use Builder.
public fun TableSection(initializer: TableSection.Builder.() -> Unit): TableSection {
  return TableSection.Builder().apply(initializer).build()
}

public class Row
private constructor(
  public val cells: List<Cell>,
  public val cellStyle: CellStyle?,
) {
  override fun toString(): String = "Row(cells=$cells, cellStyle=$cellStyle)"

  override fun hashCode(): Int = hash(cells, cellStyle)

  override fun equals(other: Any?): Boolean =
    other is Row && cells == other.cells && cellStyle == other.cellStyle

  public class Builder {
    @set:JvmSynthetic // Hide 'void' setter from Java.
    public var cells: MutableList<Cell> = mutableListOf()

    public fun setCells(cells: List<Cell>): Builder = apply { this.cells = cells.toMutableList() }

    public fun addCell(cell: Cell): Builder = apply { cells.add(cell) }

    public fun addCell(cell: String): Builder = apply { cells.add(Cell(cell)) }

    @set:JvmSynthetic // Hide 'void' setter from Java.
    public var cellStyle: CellStyle? = null

    public fun setCellStyle(cellStyle: CellStyle?): Builder = apply { this.cellStyle = cellStyle }

    public fun build(): Row = Row(cells.toList(), cellStyle)
  }
}

@JvmSynthetic // Hide from Java callers who should use Builder.
public fun Row(initializer: Row.Builder.() -> Unit): Row {
  return Row.Builder().apply(initializer).build()
}

public class Cell
private constructor(
  public val content: String,
  public val columnSpan: Int,
  public val rowSpan: Int,
  public val style: CellStyle?,
) {
  override fun toString(): String =
    "Cell(content=$content, columnSpan=$columnSpan, rowSpan=$rowSpan, style=$style)"

  override fun hashCode(): Int = hash(content, columnSpan, rowSpan, style)

  override fun equals(other: Any?): Boolean =
    other is Cell &&
      content == other.content &&
      columnSpan == other.columnSpan &&
      rowSpan == other.rowSpan &&
      style == other.style

  public class Builder(public val content: Any?) {
    @set:JvmSynthetic // Hide 'void' setter from Java.
    public var columnSpan: Int = 1

    public fun setColumnSpan(columnSpan: Int): Builder = apply { this.columnSpan = columnSpan }

    @set:JvmSynthetic // Hide 'void' setter from Java.
    public var rowSpan: Int = 1

    public fun setRowSpan(rowSpan: Int): Builder = apply { this.rowSpan = rowSpan }

    @set:JvmSynthetic // Hide 'void' setter from Java.
    public var style: CellStyle? = null

    public fun setStyle(style: CellStyle?): Builder = apply { this.style = style }

    public fun build(): Cell = Cell(content.toString(), columnSpan, rowSpan, style)
  }
}

@JvmSynthetic // Hide from Java callers who should use Builder.
public fun Cell(content: Any?, initializer: Cell.Builder.() -> Unit = {}): Cell {
  return Cell.Builder(content).apply(initializer).build()
}

public class CellStyle
private constructor(
  public val paddingLeft: Int?,
  public val paddingRight: Int?,
  public val paddingTop: Int?,
  public val paddingBottom: Int?,
  public val borderLeft: Boolean?,
  public val borderRight: Boolean?,
  public val borderTop: Boolean?,
  public val borderBottom: Boolean?,
  public val alignment: TextAlignment?,
) {
  override fun toString(): String =
    "CellStyle(padding(l=$paddingLeft,r=$paddingRight,t=$paddingTop,b=$paddingBottom), " +
      "border(l=$borderLeft,r=$borderRight,t=$borderTop,b=$borderBottom), alignment=$alignment)"

  override fun hashCode(): Int =
    hash(
      paddingLeft,
      paddingRight,
      paddingTop,
      paddingBottom,
      borderLeft,
      borderRight,
      borderTop,
      borderBottom,
      alignment,
    )

  override fun equals(other: Any?): Boolean =
    other is CellStyle &&
      paddingLeft == other.paddingLeft &&
      paddingRight == other.paddingRight &&
      paddingTop == other.paddingTop &&
      paddingBottom == other.paddingBottom &&
      borderLeft == other.borderLeft &&
      borderRight == other.borderRight &&
      borderTop == other.borderTop &&
      borderBottom == other.borderBottom &&
      alignment == other.alignment

  public class Builder {
    @set:JvmSynthetic // Hide 'void' setter from Java.
    public var paddingLeft: Int? = null

    public fun setPaddingLeft(paddingLeft: Int?): Builder = apply { this.paddingLeft = paddingLeft }

    @set:JvmSynthetic // Hide 'void' setter from Java.
    public var paddingRight: Int? = null

    public fun setPaddingRight(paddingRight: Int?): Builder = apply {
      this.paddingRight = paddingRight
    }

    @set:JvmSynthetic // Hide 'void' setter from Java.
    public var paddingTop: Int? = null

    public fun setPaddingTop(paddingTop: Int?): Builder = apply { this.paddingTop = paddingTop }

    @set:JvmSynthetic // Hide 'void' setter from Java.
    public var paddingBottom: Int? = null

    public fun setPaddingBottom(paddingBottom: Int?): Builder = apply {
      this.paddingBottom = paddingBottom
    }

    public fun setPadding(padding: Int?): Builder = apply {
      paddingLeft = padding
      paddingRight = padding
      paddingTop = padding
      paddingBottom = padding
    }

    @set:JvmSynthetic // Hide 'void' setter from Java.
    public var borderLeft: Boolean? = null

    public fun setBorderLeft(borderLeft: Boolean?): Builder = apply { this.borderLeft = borderLeft }

    @set:JvmSynthetic // Hide 'void' setter from Java.
    public var borderRight: Boolean? = null

    public fun setBorderRight(borderRight: Boolean?): Builder = apply {
      this.borderRight = borderRight
    }

    @set:JvmSynthetic // Hide 'void' setter from Java.
    public var borderTop: Boolean? = null

    public fun setBorderTop(borderTop: Boolean?): Builder = apply { this.borderTop = borderTop }

    @set:JvmSynthetic // Hide 'void' setter from Java.
    public var borderBottom: Boolean? = null

    public fun setBorderBottom(borderBottom: Boolean?): Builder = apply {
      this.borderBottom = borderBottom
    }

    public fun setBorder(border: Boolean?): Builder = apply {
      borderLeft = border
      borderRight = border
      borderTop = border
      borderBottom = border
    }

    @set:JvmSynthetic // Hide 'void' setter from Java.
    public var alignment: TextAlignment? = null

    public fun setAlignment(alignment: TextAlignment?): Builder = apply {
      this.alignment = alignment
    }

    public fun build(): CellStyle =
      CellStyle(
        paddingLeft,
        paddingRight,
        paddingTop,
        paddingBottom,
        borderLeft,
        borderRight,
        borderTop,
        borderBottom,
        alignment,
      )
  }
}

@JvmSynthetic // Hide from Java callers who should use Builder.
public fun CellStyle(initializer: CellStyle.Builder.() -> Unit): CellStyle {
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

public enum class TextAlignment {
  TopLeft,
  TopCenter,
  TopRight,
  MiddleLeft,
  MiddleCenter,
  MiddleRight,
  BottomLeft,
  BottomCenter,
  BottomRight,
}

private fun hash(vararg args: Any?): Int = args.contentHashCode()
