InStruct4j
==========

InStruct is a java utility for describing data structures and bytes from files, buffers or arrays and then displaying 
that information via enumerated maps using text or Swing. InStruct allows you to create parser strategies for any 
file which has a trace/reverse capable structure: E.g. Windows PE files, COFF, Game Package files.

Important Features
-----
> - Written using Java 7 auto closeable interfaces and NIO.
> - Datum Precondition asserting
> - Datum Postcondition asserting
> - Datum Pre requisties can be plugged in
> - Datum flags which switch; display, caching and memory behaviour.
> - Handles many data types and sizes; E.g. DWORD, UINT32, WORD, BYTE, QWORD, ULONG64, FLAGS, ENUMS... if a datatype 
does not exist it can be created very easily following the [IDatum] design pattern and built in interfaces.

Several parsers can be instantiated for several files or arrays. A TParser caches whichever values you need in 
between each stream use. 

The stream is automatically closed during each session.

Each [Datum] or variable read can be tested and edited for individual behaviours and assertions

Below is an example which showcases most of the features.  A more complete feature set will be released as the parser 
becomes more specialised. 

Future
---

* Currently working on [JList] and [JTable] java Swing models to display an InStruct. 

* Two example projects which use this utility will be released. One for Windows PE file format 
and Unreal's UPK file format.

Use
---

To use the parser you must subclass the following classes.

Currently only supports little endian byte order.
  
  [must]
  * TParser - the basic parsing engine starting point. It is through this class which you will implement a 
  SeekableByteBuffer to enable the parsing.

  [recommended]
  * ATStruct - supports the creation of a parse strategy. It is through this class which you will be able to add your 
  data structure specifics which relate to all your data structures. This class is super to all enumerations.

  [optional]
  * ATGuuid - an inner datum/struct to support the creation of Guuid.
  * ATScript - an inner datum/struct to implement a byte array: The begninnings of a plugin architecture.

Example
---

The example is of a class which extends [ATStruct]. This class creates a plan from which the parser can work to parse a file and put the values in an EnumMap. Whether or not the bytes are saved or displayable is set up by special flags which are set in the Property Enum.

Each ATStruct<?> must create its own Enum which implements ATStruct.Accessor. The accessor allows the higher level classes to manipulate some data from the lower level class.

It is considered best practice to extends ATStruct in an abtsract class so you can add any of your own functionality. Assume that this is done.

This example roughly emulates the structure of an Unreal engine upk package file.

    public class FileHeader extends MyExtendedATStruct<FileHeader.Property> {
        	
    	public enum Property implements MyExtendedATStruct.Accessor {
    		
    		/**
    		 * File Version: >= 249
    		 * 
             *  This elements will only be parsed if the pre condition is true.  
    		 */
    		HEADER_SIZE(DWORD.create("Header Size"), new AssertVersionGTEQ(249)),
    		
    		/**
    		 * Version: >= 269
    		 */
    		FOLDER_NAME(STRING.create("Folder Name"), new AssertVersionGTEQ(269)),
    		
    		/**
    		 * Supports a flag dataset which return a BitSet.
    		 */
    		FLAGS(FLAGSDWORD.create("Flags")),
    		
    		/**
    		 * Number of names stored in the name table. Always >= 0.
    		 */
    		NAMES_COUNT(DWORD.create("Names Count")),
    		
    		/**
    		 * Offset into the file of the name table, in bytes.
    		 */
    		NAMES_OFFSET(DWORD.create("Names Offset")),
    		
    		/**
    		 * Number of exported objects in the export table. Always >= 0.
    		 */
    		EXPORTS_COUNT(DWORD.create("Exports Count")),
    		
    		/**
    		 * Offset into the file of the export table.
    		 */
    		EXPORTS_OFFSET(DWORD.create("Exports Offset")),
    		
    		/**
    		 * Number of imported objects in the import table. Always >= 0.
    		 */
    		IMPORTS_COUNT(DWORD.create("Imports Count")),
    		
    		/**
    		 * Offset of import-table within the file
    		 */
    		IMPORTS_OFFSET(DWORD.create("Imports Offset")),
    				
    		/**
    		 * This shows an example where flags are added to tell the parser how to handle emulation.
    		 * 
    		 * Version >= 584
    		 */
    		UNKNOWN(BYTE.create(12, "Unknown").flags(DONT_DISPLAY.MASK), new AssertVersionGTEQ(
    					584)),
    		
    		/**
    		 * A Guuid For this constant to work I must have extended ATStruct and ATGuuid. The new Guuid class is then       
         * initialised into a constant. This is how all of the default constants are created in ATStruct.
    		 * Versions: >= 68
         * Another way is to create it manually using new MyGuuid().create...
    		 */
    		PACKAGE_GUID(GUID.create("Package Guid"), new AssertVersionGTEQ(68)),
    		
    		/**
    		 * Version >= 68
    		 */
    		GENERATIONS_COUNT(DWORD.create("Generations Count"), new AssertVersionGTEQ(68)),
    		
    		/**
    		 * Version >= 68
         * This example shows where a pre requisite value is required to determine how the parser should proceed.
    		 */
    		GENERATIONS(GENERATION.create(GENERATIONS_COUNT, "Generations").flags(
    					DONT_DISPLAY.MASK), new AssertVersionGTEQ(68)),
    		
    		/**
    		 * Version >= 245
    		 */
    		ENGINE_VERSION(DWORD.create("Engine Version"), new AssertVersionGTEQ(245)),
    		
    		/**
    		 * Version: >=277
    		 */
    		COOKER_VERSION(DWORD.create("Cooker Version"), new AssertVersionGTEQ(277));
    		
    		IDatumInfo datum;
    		
    		Property(IDatumInfo datum) {
    			this.datum = datum;
    		}
    		
    		@Override
    		public IDatumInfo getDatum() {
    			return datum;
    		}
    		
    		Property(IDatumInfo datum, ATStruct.Condition condition) {
    			this.datum = datum;
    			this.datum.condition = condition;
    		}	
    	}
        
      // Flags have mask and ordinal value for BitSet extraction and use.
    	public enum PackageFlags {
    		ALLOW_DOWNLOAD("Allow Downloading of Package"),
    		CLIENT_OPTIONAL("Optional for Clients"),
    		SERVER_SIDE_ONLY("Server Side Only"),
    		BROKEN_LINKS("Loaded from Linker with brokwn import links"),
    		UNSECURE("Not trusted"),
    		NEED("Client needs to download this package");
    		
    		String name;
    		int mask;
    		
    		PackageFlags(String name) {
    			this.name = name;
    			this.mask = 1 << this.ordinal();
    		}
    	}
    	
      // A struct size can be indeterminate as is the case here. 
      // It can also be static. If your struct has a static size take advantage
      // of the super class paramater for size.
      // Arrays, string and other similar datums can make an array size indeterminate.
    	public FileHeader() {
    		super(Property.class);
    	}
    	
    	@Override
    	public Integer entryOffset(UEHandle h) {
    		return 8;
    	}	
    }

Installation
-----

Import code into eclipse as a java project. 

Licence
-----

Apache License, Version 2.0
http://www.apache.org/licenses/






