package com.jakewharton.picnic;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static com.jakewharton.picnic.BorderStyle.Hidden;
import static com.jakewharton.picnic.TextAlignment.BottomCenter;
import static com.jakewharton.picnic.TextAlignment.BottomLeft;
import static com.jakewharton.picnic.TextAlignment.MiddleRight;

public final class RepresentativeTableBuilderTest {
  @Test public void test() {
    Table table = TableBuilder.create()
        .withTableStyle()
            .setBorderStyle(Hidden)
            .endTableStyle()
        .withCellStyle()
            .setAlignment(MiddleRight)
            .setPaddingLeft(1)
            .setPaddingRight(1)
            .setBorderLeft(true)
            .setBorderRight(true)
            .endCellStyle()
        .withHeader()
            .withCellStyle()
                .setBorder(true)
                .setAlignment(BottomLeft)
                .endCellStyle()
            .addRow()
                .addCell("APK")
                    .setRowSpan(2)
                    .endCell()
                .addCell("compressed")
                    .setColumnSpan(3)
                    .withCellStyle()
                        .setAlignment(BottomCenter)
                        .endCellStyle()
                    .endCell()
                .addCell("uncompressed")
                    .setColumnSpan(3)
                    .withCellStyle()
                        .setAlignment(BottomCenter)
                        .endCellStyle()
                    .endCell()
                .endRow()
            .addRow("old", "new", "diff", "old", "new", "diff")
            .endHeader()
        .withBody()
            .addRow("dex", "664.8 KiB", "664.8 KiB", "-25 B", "1.5 MiB", "1.5 MiB", "-112 B")
            .addRow("arsc", "201.7 KiB", "201.7 KiB", "0 B", "201.6 KiB", "201.6 KiB", "0 B")
            .addRow("manifest", "1.4 KiB", "1.4 KiB", "0 B", "4.2 KiB", "4.2 KiB", "0 B")
            .addRow("res", "418.2 KiB", "418.2 KiB", "-14 B", "488.3 KiB", "488.3 KiB", "0 B")
            .addRow("asset", "0 B", "0 B", "0 B", "0 B", "0 B", "0 B")
            .addRow("other", "37.1 KiB", "37.1 KiB", "0 B", "36.3 KiB", "36.3 KiB", "0 B")
            .endBody()
        .withFooter()
            .withCellStyle()
                .setBorder(true)
                .endCellStyle()
            .addRow("total", "1.3 MiB", "1.3 MiB", "-39 B", "2.2 MiB", "2.2 MiB", "-112 B")
            .endFooter()
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
        + "    total │   1.3 MiB │   1.3 MiB │ -39 B │   2.2 MiB │   2.2 MiB │ -112 B ");
  }
}
