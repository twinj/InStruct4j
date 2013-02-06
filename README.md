InStruct4j
==========

A universal parser for mapping bytes from files, buffers or arrays and then displaying that information via the enumerated maps. This iperfect for creating parser strategies for any file which has a tracaeable structure: E.g. Windows PE files, COFF, Package files.

To use the parser you must subclass the following classes.

Currently only support little endian.

  - ATStruct - supports the creation of a parse strategy.
  - ATGuuid - an inner datum/struct to support the creation of Guuid.
  - ATScript - an inner datum/struct to implement a byte array.
  - TParser - the basic parsing engine. 

This engine allows you to create parsing strategies via the [TParser.Strategy] interface. Several parsers can be created for several files or arrays. A TParser caches whichever values you need in between stream use.

Features
-----

Below is an example which showcases most of the features.  A proper feature set will be released as the parser becomes more specialised. 

Future
---

Currently working on [JList] and [JTable] java Swing models to display an InStruct. 

Example
---

The example is of a class which extends [ATStruct]. This class creates a plan from which the parser can work to parse a file and put the values in an EnumMap. Whether or not the bytes are saved or displayable is set up by special flags which are set in the Property Enum.

Each ATStruct<?> must create its own Enum which implements ATStruct.Accessor. The accessor allows the higher level classes to manipulate some data from the lower level class.

It is considered best practice to extends ATStruct in an abtsract class so you can add any of your own functionality. Assume that this is done.

Use
---

This example roughly emulates the structure of an Unreal engine upk package file.

    public class FileHeader extends MyATStruct<UHeader.Property> {
        	
    	public enum Property implements PStruct.Accessor {
    		
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
    		 * 
    		 * Legacy
    		 */
    		IMPORTS_COUNT(DWORD.create("Imports Count")),
    		
    		/**
    		 * Offset of import-table within the file
    		 * 
    		 * Legacy
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
        
        // 
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
    	
    	public UHeader() {
    		super(Property.class);
    	}
    	
    	@Override
    	public Integer entryOffset(UEHandle h) {
    		return 8;
    	}	
    }
    



Installation
-----

Import code into eclipse as a new project. No settings are supplied just source code.

Licence
-----

LGPL (currently under review)







