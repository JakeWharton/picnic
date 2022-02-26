# Change Log

## 0.6.0 - 2022-02-25

 * Library is now fully multiplatform supporting JS and native targets in addition to JVM/Android.
 * Text drawing is now provided by [crossword](https://github.com/JakeWharton/crossword) library.


## 0.5.0 - 2020-09-16

 * New: Each line in a multi-line cell is now individually aligned.
 * New: Do not measure ANSI control sequences allowing color to be used inside cells.


## 0.4.0 - 2020-08-12

 * New: `cells()` function adds multiple cells at once to a row.
 * `TextBorder` property name changed to `characters`.


## 0.3.1 - 2020-04-20

 * Fix: Use own implementation of codepoint iteration which enables running on Android.


## 0.3.0 - 2020-02-07

 * New: Support for a table-level border on the table style. This border is automatically collapsed
   with cell borders which are on the edge of the table. The table border makes it easy to enclose
   a table in a border without having to track first/last row/col.
 * Fix: Switch to using codepoints rather than characters as a primitive. As a result, multi-char
   glyphs which render as a single visual "character" are now supported and measured correctly.


## 0.2.0 - 2019-12-09

 * New: Builder-based API for Java users.
 * Fix: Use actual Java default methods on interfaces instead of fake Kotlin default methods.
 * Fix: Remove `data` modifier and public constructors from model types. Java users should use the
   new builders to create instances. Kotlin users should use the factory DSL. Kotlin users can no
   longer destructure or `copy` model types.


## 0.1.0 2019-11-18

Initial release.
