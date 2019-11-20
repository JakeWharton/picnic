package com.jakewharton.picnic;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static com.jakewharton.picnic.BorderStyle.Hidden;
import static com.jakewharton.picnic.TextAlignment.BottomCenter;
import static com.jakewharton.picnic.TextAlignment.BottomLeft;
import static com.jakewharton.picnic.TextAlignment.MiddleRight;

public final class RepresentativeJavaBuilderTest {
  @Test public void test() {
    Table table = new Table.Builder()
        .setTableStyle(new TableStyle.Builder()
            .setBorderStyle(Hidden)
            .build())
        .setCellStyle(new CellStyle.Builder()
            .setAlignment(MiddleRight)
            .setPaddingLeft(1)
            .setPaddingRight(1)
            .setBorderLeft(true)
            .setBorderRight(true)
            .build())
        .setHeader(new TableSection.Builder()
            .setCellStyle(new CellStyle.Builder()
                .setBorder(true)
                .setAlignment(BottomLeft)
                .build())
            .addRow(new Row.Builder()
                .addCell(new Cell.Builder("APK")
                    .setRowSpan(2)
                    .build())
                .addCell(new Cell.Builder("compressed")
                    .setColumnSpan(3)
                    .setStyle(new CellStyle.Builder()
                        .setAlignment(BottomCenter)
                        .build())
                    .build())
                .addCell(new Cell.Builder("uncompressed")
                    .setColumnSpan(3)
                    .setStyle(new CellStyle.Builder()
                        .setAlignment(BottomCenter)
                        .build())
                    .build())
                .build())
            .addRow("old", "new", "diff", "old", "new", "diff")
            .build())
        .setBody(new TableSection.Builder()
            .addRow("dex", "664.8 KiB", "664.8 KiB", "-25 B", "1.5 MiB", "1.5 MiB", "-112 B")
            .addRow("arsc", "201.7 KiB", "201.7 KiB", "0 B", "201.6 KiB", "201.6 KiB", "0 B")
            .addRow("manifest", "1.4 KiB", "1.4 KiB", "0 B", "4.2 KiB", "4.2 KiB", "0 B")
            .addRow("res", "418.2 KiB", "418.2 KiB", "-14 B", "488.3 KiB", "488.3 KiB", "0 B")
            .addRow("asset", "0 B", "0 B", "0 B", "0 B", "0 B", "0 B")
            .addRow("other", "37.1 KiB", "37.1 KiB", "0 B", "36.3 KiB", "36.3 KiB", "0 B")
            .build())
        .setFooter(new TableSection.Builder()
            .setCellStyle(new CellStyle.Builder()
                .setBorder(true)
                .build())
            .addRow("total", "1.3 MiB", "1.3 MiB", "-39 B", "2.2 MiB", "2.2 MiB", "-112 B")
            .build())
        .build();
    assertThat(table.toString()).isEqualTo(""
        + "          │          compressed           │          uncompressed          \n"
        + "          ├───────────┬───────────┬───────┼───────────┬───────────┬────────\n"
        + " APK      │ old       │ new       │ diff  │ old       │ new       │ diff   \n"
        + "──────────┼───────────┼───────────┼───────┼───────────┼───────────┼────────\n"
        + "      dex │ 664.8 KiB │ 664.8 KiB │ -25 B │   1.5 MiB │   1.5 MiB │ -112 B \n"
        + "     arsc │ 201.7 KiB │ 201.7 KiB │   0 B │ 201.6 KiB │ 201.6 KiB │    0 B \n"
        + " manifest │   1.4 KiB │   1.4 KiB │   0 B │   4.2 KiB │   4.2 KiB │    0 B \n"
        + "      res │ 418.2 KiB │ 418.2 KiB │ -14 B │ 488.3 KiB │ 488.3 KiB │    0 B \n"
        + "    asset │       0 B │       0 B │   0 B │       0 B │       0 B │    0 B \n"
        + "    other │  37.1 KiB │  37.1 KiB │   0 B │  36.3 KiB │  36.3 KiB │    0 B \n"
        + "──────────┼───────────┼───────────┼───────┼───────────┼───────────┼────────\n"
        + "    total │   1.3 MiB │   1.3 MiB │ -39 B │   2.2 MiB │   2.2 MiB │ -112 B \n");
  }
}
