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
import dorkbox.util.pe.types.ULongTimeDate;
import dorkbox.util.pe.types.UShort;

public class ResourceDirectoryHeader extends Header {

    public final ULong RSRC_CHARACTERISTICS;
    public final ULongTimeDate TIME_STAMP;
    public final UShort MAJOR_VERSION;
    public final UShort MINOR_VERSION;
    public final UShort NUM_NAME_ENTRIES;
    public final UShort NUM_ID_ENTRIES;

    public ResourceDirectoryEntry[] entries;

    public ResourceDirectoryHeader(ByteArray bytes, SectionTableEntry section, int level) {
        this.RSRC_CHARACTERISTICS = new ULong(bytes.readUInt(4), "Resource Characteristics"); // not used.
        this.TIME_STAMP = new ULongTimeDate(bytes.readUInt(4), "Date");  // The time that the resource data was created by the resource compiler.
        this.MAJOR_VERSION = new UShort(bytes.readUShort(2), "Major Version");
        this.MINOR_VERSION = new UShort(bytes.readUShort(2), "Minor Version");
        this.NUM_NAME_ENTRIES = new UShort(bytes.readUShort(2), "Number of Name Entries");
        this.NUM_ID_ENTRIES = new UShort(bytes.readUShort(2), "Number of ID Entries");


        int numberOfNamedEntires = this.NUM_NAME_ENTRIES.get();
        int numberOfIDEntires = this.NUM_ID_ENTRIES.get();

        int numberOfEntries = numberOfNamedEntires + numberOfIDEntires;

        this.entries = new ResourceDirectoryEntry[numberOfEntries];
        // IE:
        //  ROOT  (lvl 0)
        //   \- Bitmap  (lvl 1)
        //   |- Icons
        //     \- 1
        //     |- 2 (lvl 2)
        //   |- Dialog
        //   |- String


        for (int i=0;i<numberOfEntries;i++) {
            this.entries[i] = new ResourceDirectoryEntry(bytes, section, level+1);
        }
    }
}
