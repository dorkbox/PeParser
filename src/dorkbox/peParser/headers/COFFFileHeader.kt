/*
 * Copyright 2023 dorkbox, llc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dorkbox.peParser.headers

import dorkbox.peParser.ByteArray
import dorkbox.peParser.types.*

class COFFFileHeader(bytes: ByteArray) : Header() {
    companion object {
        // see: http://msdn.microsoft.com/en-us/library/ms809762.aspx
        const val HEADER_SIZE = 20
    }

    /** The CPU that this file is intended for  */
    val Machine: MachineType?

    /** The number of sections in the file.  */
    val NumberOfSections: WORD?

    /**
     * The time that the linker (or compiler for an OBJ file) produced this file. This field holds the number of seconds since December
     * 31st, 1969, at 4:00 P.M. (PST)
     */
    val TimeDateStamp: TimeDate?

    /**
     * The file offset of the COFF symbol table. This field is only used in OBJ files and PE files with COFF debug information. PE files
     * support multiple debug formats, so debuggers should refer to the IMAGE_DIRECTORY_ENTRY_DEBUG entry in the data directory (defined
     * later).
     */
    val PointerToSymbolTable: DWORD?

    /** The number of symbols in the COFF symbol table. See above.  */
    val NumberOfSymbols: DWORD?

    /**
     * The size of an optional header that can follow this structure. In OBJs, the field is 0. In executables, it is the size of the
     * IMAGE_OPTIONAL_HEADER structure that follows this structure.
     */
    val SizeOfOptionalHeader: WORD?

    /** Flags with information about the file.  */
    val Characteristics: CoffCharacteristics?

    init {
        Machine = h(MachineType(bytes.readUShort(2), "machine type"))
        NumberOfSections = h(WORD(bytes.readUShort(2), "number of sections"))
        TimeDateStamp = h(TimeDate(bytes.readUInt(4), "time date stamp"))
        PointerToSymbolTable = h(DWORD(bytes.readUInt(4), "pointer to symbol table"))
        NumberOfSymbols = h(DWORD(bytes.readUInt(4), "number of symbols"))
        SizeOfOptionalHeader = h(WORD(bytes.readUShort(2), "size of optional header"))
        Characteristics = h(CoffCharacteristics(bytes.readUShort(2), "characteristics"))
    }
}
