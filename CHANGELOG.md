Change Log
==========

Version 0.3.0 *(2020-02-07)*
----------------------------

 * New: Support for a table-level border on the table style. This border is automatically collapsed
   with cell borders which are on the edge of the table. The table border makes it easy to enclose
   a table in a border without having to track first/last row/col.
 * Fix: Switch to using codepoints rather than characters as a primitive. As a result, multi-char
   glyphs which render as a single visual "character" are now supported and measured correctly.


Version 0.2.0 *(2019-12-09)*
----------------------------

 * New: Builder-based API for Java users.
 * Fix: Use actual Java default methods on interfaces instead of fake Kotlin default methods.
 * Fix: Remove `data` modifier and public constructors from model types. Java users should use the
   new builders to create instances. Kotlin users should use the factory DSL. Kotlin users can no
   longer destructure or `copy` model types.


Version 0.1.0 *(2019-11-18)*
----------------------------

Initial release.
