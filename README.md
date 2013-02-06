InStruct4j
==========

A universal parser for mapping bytes from files, buffers or arrays and then displaying that information via the enumerations.

To use the parser you must subclass the following classes.

Currently only support little endian machines.

ATStruct - supports the creation of a parse strategy.
ATGuuid - an inner datum/struct to support the creation of Guuid.
ATScript - an inner datum/struct to implement a byte array.
TParser - the basic parsing engine. 

This engine allows you to create strategies via the Strategy interface. Several parsers can be created for several files. A TParser caches whichever values you need.



