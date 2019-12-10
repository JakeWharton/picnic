Change Log
==========

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
