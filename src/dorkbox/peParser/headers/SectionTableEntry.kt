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

class SectionTableEntry(bytes: ByteArray, entryNumber: Int, offset: Int, size: Int) : Header() {
    companion object {
        // more info here: http://msdn.microsoft.com/en-us/library/ms809762.aspx
        const val ENTRY_SIZE = 40
    }

    val NAME: AsciiString
    val VIRTUAL_SIZE: DWORD
    val VIRTUAL_ADDRESS: DWORD
    val SIZE_OF_RAW_DATA: DWORD
    val POINTER_TO_RAW_DATA: DWORD
    val POINTER_TO_RELOCATIONS: DWORD
    val POINTER_TO_LINE_NUMBERS: DWORD
    val NUMBER_OF_RELOCATIONS: WORD
    val NUMBER_OF_LINE_NUMBERS: WORD
    val CHARACTERISTICS: SectionCharacteristics

    init {
        h(HeaderDefinition("Section table entry: $entryNumber"))
        NAME = h(AsciiString(bytes, 8, "name"))
        VIRTUAL_SIZE = h(DWORD(bytes.readUInt(4), "virtual size"))
        VIRTUAL_ADDRESS = h(DWORD(bytes.readUInt(4), "virtual address"))
        SIZE_OF_RAW_DATA = h(DWORD(bytes.readUInt(4), "size of raw data"))
        POINTER_TO_RAW_DATA = h(DWORD(bytes.readUInt(4), "pointer to raw data"))
        POINTER_TO_RELOCATIONS = h(DWORD(bytes.readUInt(4), "pointer to relocations"))
        POINTER_TO_LINE_NUMBERS = h(DWORD(bytes.readUInt(4), "pointer to line numbers"))
        NUMBER_OF_RELOCATIONS = h(WORD(bytes.readUShort(2), "number of relocations"))
        NUMBER_OF_LINE_NUMBERS = h(WORD(bytes.readUShort(2), "number of line numbers"))
        CHARACTERISTICS = h(SectionCharacteristics(bytes.readUInt(4), "characteristics"))
    }
}
