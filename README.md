Picnic Tables
=============

A Kotlin DSL and Java/Kotlin builder API for constructing HTML-like tables
which can be rendered to text.

Features:

 * Borders (with multiple styles)
 * Padding (left/right/top/bottom)
 * Per-table, row, and cell styling
 * Header/footer sections
 * Row/column spans
 * Text alignment

DSL:

```kotlin
table {
  row("Hello", "World")
  row("Hola", "Mundo")
}
```
```
HelloWorld
Hola Mundo
```

Very underwhelming! Picnic tables start completely unstyled.

### Border

```kotlin
table {
  cellStyle {
    // These options affect the style of all cells contained within the table.
    border = true
  }
  row("Hello", "World")
  row("Hola", "Mundo")
}
```
```
┌─────┬─────┐
│Hello│World│
├─────┼─────┤
│Hola │Mundo│
└─────┴─────┘
```

### Table Border Style

```kotlin
table {
  style {
    // Unlike cellStyle, these options affect the style of the table itself.
    borderStyle = Hidden
  }
  cellStyle {
    border = true
  }
  row("Hello", "World")
  row("Hola", "Mundo")
}
```
```
Hello│World
─────┼─────
Hola │Mundo
```

### Padding

```kotlin
table {
  cellStyle {
    border = true
    paddingLeft = 1
    paddingRight = 1
  }
  row("Hello", "World")
  row("Hola", "Mundo")
}
```
```
┌───────┬───────┐
│ Hello │ World │
├───────┼───────┤
│ Hola  │ Mundo │
└───────┴───────┘
```

### Table, Row, and Cell Style

```kotlin
table {
  cellStyle {
    border = true
    paddingLeft = 1
    paddingRight = 1
  }
  row {
    cellStyle {
      // These options affect only the cells contained within this row and override table options.
      paddingTop = 1
    }
    cell("Hello")
    cell("World")
  }
  row {
    cell("Hola")
    cell("Mundo") {
       // These options affect only this specific cell and override row and table options.
       border = false
     }
  }
}
```
```
┌───────┬───────┐
│       │       │
│ Hello │ World │
├───────┼───────┘
│ Hola  │ Mundo  
└───────┘        
```

### Header and Footer

```kotlin
table {
  header {
    // Rows in a header always come first no matter when they're added.
    row("Hello", "Header")
  }
  footer {
    // Rows in a footer always come last no matter when they're added.
    row("Hello", "Footer")
  }
  row("Hello", "World")
  cellStyle {
    border = true
  }
}
```
```
┌─────┬──────┐
│Hello│Header│
├─────┼──────┤
│Hello│World │
├─────┼──────┤
│Hello│Footer│
└─────┴──────┘
```

### Row and Column Span

```kotlin
table {
  cellStyle {
    border = true
  }
  row {
    cell("Hello") {
      rowSpan = 2
    }
    cell("World")
  }
  // This row has only one cell because "Hello" will carry over and push it to the right.
  row("Mars")

  // This row has only one cell because it spans two columns.
  row {
    cell("Hola Mundo") {
      columnSpan = 2
    }
  }
}
```
```
┌─────┬─────┐
│Hello│World│
│     ├─────┤
│     │Mars │
├─────┴─────┤
│Hola Mundo │
└───────────┘
```

### Text Alignment

```kotlin
table {
  cellStyle {
    border = true
    alignment = TopCenter
  }
  row {
    cell("Hello") {
      rowSpan = 4
      alignment = MiddleLeft
    }
    cell("Mercury")
  }
  row("Venus")
  row("Earth")
  row("Mars")
  row {
    cell("Hola") {
      rowSpan = 4
      alignment = MiddleLeft
    }
    cell("Jupiter")
  }
  row("Saturn")
  row("Uranus")
  row("Neptune")
  row("Adios", "Pluto")
}
```
```
┌─────┬───────┐
│     │Mercury│
│     ├───────┤
│     │ Venus │
│Hello├───────┤
│     │ Earth │
│     ├───────┤
│     │ Mars  │
├─────┼───────┤
│     │Jupiter│
│     ├───────┤
│     │Saturn │
│Hola ├───────┤
│     │Uranus │
│     ├───────┤
│     │Neptune│
├─────┼───────┤
│Adios│ Pluto │
└─────┴───────┘
```


Download
--------

Artifacts are available in Maven Central at `com.jakewharton.picnic:picnic:0.1.0`.

In-development snapshots are available from
[Sonatype's snapshot repository](https://oss.sonatype.org/content/repositories/snapshots/).



License
=======

    Copyright 2015 Jake Wharton

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
