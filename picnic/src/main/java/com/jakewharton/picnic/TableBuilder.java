package com.jakewharton.picnic;

import java.util.function.Consumer;

/**
 * A Java-shim over Picnic's {@link Table.Builder} providing a improved API and
 * accessibility from Scala (and probably further JVM-based languages).
 * <p>
 * The Scala incompatibility of {@link Table.Builder} comes from the fact that
 * Scala does not seem to understand Kotlins {@code @set:JvmSynthetic}
 * annotation. And since this annotation is used in Picnic to hide Kotlin setter
 * methods from Java (and, in turn from Scala), the Scala compiler failes to
 * resolve a method reference:
 * </p>
 * <pre>
 * {@code
 * [error] Example.scala:37:14: ambiguous reference to overloaded definition,
 * [error] both method setColumnSpan in class TableBuilder of type (x$1: Int): com.jakewharton.picnic.Cell.Builder
 * [error] and  method setColumnSpan in class TableBuilder of type (x$1: Int): Unit
 * [error] match argument types (Int)
 * [error]             .setColumnSpan(4)
 * }
 * </pre>
 */
public final class TableBuilder implements CellStyleable {
    private final Table.Builder builder = new Table.Builder();

    public HeaderBuilder withHeader() {
        return new HeaderBuilder(this);
    }

    public BodyBuilder withBody() {
        return new BodyBuilder(this);
    }

    public FooterBuilder withFooter() {
        return new FooterBuilder(this);
    }

    @Override
    public void setCellStyle(CellStyle cellStyle) {
        builder.setCellStyle(cellStyle);
    }

    public CellStyleBuilder<TableBuilder> withCellStyle() {
        return new CellStyleBuilder<>(this);
    }

    public TableStyleBuilder withTableStyle() {
        return new TableStyleBuilder(this);
    }

    public Table build() {
        return builder.build();
    }

    public static TableBuilder create() {
        return new TableBuilder();
    }

    public static final class TableStyleBuilder {
        private final TableBuilder builder;
        private final TableStyle.Builder tableStyleBuilder = new TableStyle.Builder();

        private TableStyleBuilder(TableBuilder builder) {
            this.builder = builder;
        }

        public TableStyleBuilder setBorderStyle(BorderStyle borderStyle) {
            tableStyleBuilder.setBorderStyle(borderStyle);
            return this;
        }

        public TableStyleBuilder setBorder(boolean border) {
            tableStyleBuilder.setBorder(border);
            return this;
        }

        public TableStyleBuilder withBorder() {
            return setBorder(true);
        }

        public TableStyleBuilder withoutBorder() {
            return setBorder(false);
        }

        public TableBuilder endTableStyle() {
            builder.builder.setTableStyle(tableStyleBuilder.build());
            return builder;
        }
    }

    public static final class CellStyleBuilder<C extends CellStyleable> {
        private final C cellStyleable;
        private final CellStyle.Builder cellStyleBuilder = new CellStyle.Builder();

        private CellStyleBuilder(C cellStyleable) {
            this.cellStyleable = cellStyleable;
        }

        public CellStyleBuilder<C> setAlignment(TextAlignment textAlignment) {
            cellStyleBuilder.setAlignment(textAlignment);
            return this;
        }

        public CellStyleBuilder<C> setPaddingLeft(int paddingLeft) {
            cellStyleBuilder.setPaddingLeft(paddingLeft);
            return this;
        }

        public CellStyleBuilder<C> setPaddingRight(int paddingRight) {
            cellStyleBuilder.setPaddingRight(paddingRight);
            return this;
        }

        public CellStyleBuilder<C> setPaddingTop(int paddingTop) {
            cellStyleBuilder.setPaddingTop(paddingTop);
            return this;
        }

        public CellStyleBuilder<C> setPaddingBottom(int paddingBottom) {
            cellStyleBuilder.setPaddingBottom(paddingBottom);
            return this;
        }

        public CellStyleBuilder<C> setPadding(int padding) {
            cellStyleBuilder.setPadding(padding);
            return this;
        }

        public CellStyleBuilder<C> setBorderLeft(boolean borderLeft) {
            cellStyleBuilder.setBorderLeft(borderLeft);
            return this;
        }

        public CellStyleBuilder<C> setBorderRight(boolean borderRight) {
            cellStyleBuilder.setBorderRight(borderRight);
            return this;
        }

        public CellStyleBuilder<C> setBorderTop(boolean borderTop) {
            cellStyleBuilder.setBorderTop(borderTop);
            return this;
        }

        public CellStyleBuilder<C> setBorderBottom(boolean borderBottom) {
            cellStyleBuilder.setBorderBottom(borderBottom);
            return this;
        }

        public CellStyleBuilder<C> setBorder(boolean border) {
            cellStyleBuilder.setBorder(border);
            return this;
        }

        public C endCellStyle() {
            cellStyleable.setCellStyle(cellStyleBuilder.build());
            return cellStyleable;
        }
    }

    public static abstract class TableSectionBuilder<B extends TableSectionBuilder<B>> implements CellStyleable {
        protected final TableBuilder builder;

        protected final TableSection.Builder tableSectionBuilder = new TableSection.Builder();

        protected TableSectionBuilder(TableBuilder builder) {
            this.builder = builder;
        }

        public abstract B getThis();

        public B addRow(String... cells) {
            tableSectionBuilder.addRow(cells);
            return getThis();
        }

        public B addRow(Row row) {
            tableSectionBuilder.addRow(row);
            return getThis();
        }

        public RowBuilder<B> addRow() {
            return new RowBuilder<B>(getThis());
        }

        public B withBuilder(Consumer<B> lambda) {
            B b = getThis();
            lambda.accept(b);
            return b;
        }

        @Override
        public void setCellStyle(CellStyle cellStyle) {
            tableSectionBuilder.setCellStyle(cellStyle);
        }

        public CellStyleBuilder<B> withCellStyle() {
            return new CellStyleBuilder<B>(getThis());
        }
    }

    public static final class RowBuilder<B extends TableSectionBuilder<B>> {
        private final Row.Builder rowBuilder = new Row.Builder();
        private final TableSectionBuilder<B> tableSectionBuilder;

        private RowBuilder(TableSectionBuilder<B> tableSectionBuilder) {
            this.tableSectionBuilder = tableSectionBuilder;
        }

        public RowBuilder<B> addCell(Cell cell) {
            rowBuilder.addCell(cell);
            return this;
        }

        public CellBuilder<B> addCell(String cellContent) {
            return new CellBuilder<>(cellContent, this);
        }

        public TableSectionBuilder<B> endRow() {
            return tableSectionBuilder.addRow(rowBuilder.build());
        }
    }

	public static final class CellBuilder<B extends TableSectionBuilder<B>> implements CellStyleable {
		private final Cell.Builder cellBuilder;
		private final RowBuilder<B> rowBuilder;

		private CellBuilder(String cellContent, RowBuilder<B> rowBuilder) {
			this.cellBuilder = new Cell.Builder(cellContent);
			this.rowBuilder = rowBuilder;
		}

		public CellBuilder<B> setColumnSpan(int columnSpan) {
			cellBuilder.setColumnSpan(columnSpan);
			return this;
		}

		public CellBuilder<B> setRowSpan(int rowSpan) {
			cellBuilder.setRowSpan(rowSpan);
			return this;
		}

		public CellStyleBuilder<CellBuilder<B>> withCellStyle() {
			return new CellStyleBuilder<>(this);
		}

		public RowBuilder<B> endCell() {
			return rowBuilder.addCell(cellBuilder.build());
		}

		@Override
		public void setCellStyle(CellStyle cellStyle) {
			cellBuilder.setStyle(cellStyle);
		}
	}

	public static final class HeaderBuilder extends TableSectionBuilder<HeaderBuilder> {
		private HeaderBuilder(TableBuilder builder) {
			super(builder);
		}

		@Override
		public HeaderBuilder getThis() {
			return this;
		}

		public TableBuilder endHeader() {
			builder.builder.setHeader(tableSectionBuilder.build());
			return builder;
		}
	}

	public static final class BodyBuilder extends TableSectionBuilder<BodyBuilder> {
		private BodyBuilder(TableBuilder builder) {
			super(builder);
		}

		@Override
		public BodyBuilder getThis() {
			return this;
		}

		public TableBuilder endBody() {
			builder.builder.setBody(tableSectionBuilder.build());
			return builder;
		}
	}

	public static final class FooterBuilder extends TableSectionBuilder<FooterBuilder> {
		private FooterBuilder(TableBuilder builder) {
			super(builder);
		}

		@Override
		public FooterBuilder getThis() {
			return this;
		}

		public TableBuilder endFooter() {
			builder.builder.setFooter(tableSectionBuilder.build());
			return builder;
		}
	}

}
