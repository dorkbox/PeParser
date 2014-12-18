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
package dorkbox.util.pe.headers.resources;

import dorkbox.util.pe.ByteArray;
import dorkbox.util.pe.headers.Header;
import dorkbox.util.pe.headers.SectionTableEntry;
import dorkbox.util.pe.types.ULong;

public class ResourceDataEntry extends Header {

    public final ULong OFFSET_TO_DATA; // The address of a unit of resource data in the Resource Data area.
    public final ULong SIZE;
    public final ULong CODE_PAGE;
    public final ULong RESERVED;

    private final SectionTableEntry section;

    /**
     * @param section - necessary to know this section for when computing the location of the resource data!
     */
    @SuppressWarnings("unused")
    public ResourceDataEntry(ByteArray bytes, int entryOffset, SectionTableEntry section) {
        this.section = section;

        this.OFFSET_TO_DATA = new ULong(bytes.readUInt(4), "offsetToData");
        this.SIZE = new ULong(bytes.readUInt(4), "Size");
        this.CODE_PAGE = new ULong(bytes.readUInt(4), "CodePage");
        this.RESERVED = new ULong(bytes.readUInt(4), "Reserved");
    }

    public byte[] getData(ByteArray bytes) {
        // this is where to get the data from the ABSOLUTE position in the file!
        int dataOffset = this.section.POINTER_TO_RAW_DATA.get() + this.OFFSET_TO_DATA.get() - this.section.VIRTUAL_ADDRESS.get();
        //String asHex = Integer.toHexString(dataOffset);
        int saved = bytes.position();
        bytes.seek(dataOffset);
        byte[] copyBytes = bytes.copyBytes(this.SIZE.get());
        bytes.seek(saved);
        return copyBytes;
    }
}
