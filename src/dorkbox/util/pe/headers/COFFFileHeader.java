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
import dorkbox.util.pe.types.ULong;
import dorkbox.util.pe.types.ULongTimeDate;
import dorkbox.util.pe.types.UShort;
import dorkbox.util.pe.types.UShortCoffCharacteristics;
import dorkbox.util.pe.types.UShortMachineType;

public class COFFFileHeader extends Header {

    public static final int HEADER_SIZE = 20;

    public final UShortMachineType MACHINE;
    public final UShort SECTION_NR;
    public final ULongTimeDate TIME_DATE;
    public final ULong POINTER_TO_SYMBOLTABLE;
    public final ULong NUMBER_OF_SYMBOLS;
    public final UShort SIZE_OF_OPT_HEADER;
    public final UShortCoffCharacteristics CHARACTERISTICS;

    public COFFFileHeader(ByteArray bytes) {
        this.MACHINE    = h(new UShortMachineType(bytes.readUShort(2), "machine type"));
        this.SECTION_NR = h(new UShort(bytes.readUShort(2), "number of sections"));
        this.TIME_DATE  = h(new ULongTimeDate(bytes.readUInt(4), "time date stamp"));
        this.POINTER_TO_SYMBOLTABLE = h(new ULong(bytes.readUInt(4), "pointer to symbol table"));
        this.NUMBER_OF_SYMBOLS      = h(new ULong(bytes.readUInt(4), "number of symbols"));
        this.SIZE_OF_OPT_HEADER = h(new UShort(bytes.readUShort(2), "size of optional header"));
        this.CHARACTERISTICS    = h(new UShortCoffCharacteristics(bytes.readUShort(2), "characteristics"));
    }
}
