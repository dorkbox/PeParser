/*
 * Copyright 2012 dorkbox, llc
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
package dorkbox.util.pe.headers;

import dorkbox.util.pe.ByteArray;
import dorkbox.util.pe.types.AsciiString;
import dorkbox.util.pe.types.HeaderDefinition;
import dorkbox.util.pe.types.DWORD;
import dorkbox.util.pe.types.SectionCharacteristics;
import dorkbox.util.pe.types.WORD;

public class SectionTableEntry extends Header {

    // more info here: http://msdn.microsoft.com/en-us/library/ms809762.aspx

    public final static int ENTRY_SIZE = 40;

    public final AsciiString NAME;
    public final DWORD VIRTUAL_SIZE;
    public final DWORD VIRTUAL_ADDRESS;
    public final DWORD SIZE_OF_RAW_DATA;
    public final DWORD POINTER_TO_RAW_DATA;
    public final DWORD POINTER_TO_RELOCATIONS;
    public final DWORD POINTER_TO_LINE_NUMBERS;
    public final WORD NUMBER_OF_RELOCATIONS;
    public final WORD NUMBER_OF_LINE_NUMBERS;
    public final SectionCharacteristics CHARACTERISTICS;

    @SuppressWarnings("unused")
    public SectionTableEntry(ByteArray bytes, int entryNumber, int offset, int size) {

        h(new HeaderDefinition("Section table entry: " + entryNumber));

        this.NAME = h(new AsciiString(bytes, 8, "name"));
        this.VIRTUAL_SIZE = h(new DWORD(bytes.readUInt(4), "virtual size"));
        this.VIRTUAL_ADDRESS = h(new DWORD(bytes.readUInt(4), "virtual address"));

        this.SIZE_OF_RAW_DATA = h(new DWORD(bytes.readUInt(4), "size of raw data"));
        this.POINTER_TO_RAW_DATA = h(new DWORD(bytes.readUInt(4), "pointer to raw data"));
        this.POINTER_TO_RELOCATIONS = h(new DWORD(bytes.readUInt(4), "pointer to relocations"));
        this.POINTER_TO_LINE_NUMBERS = h(new DWORD(bytes.readUInt(4), "pointer to line numbers"));

        this.NUMBER_OF_RELOCATIONS = h(new WORD(bytes.readUShort(2), "number of relocations"));
        this.NUMBER_OF_LINE_NUMBERS = h(new WORD(bytes.readUShort(2), "number of line numbers"));
        this.CHARACTERISTICS = h(new SectionCharacteristics(bytes.readUInt(4), "characteristics"));
    }
}
