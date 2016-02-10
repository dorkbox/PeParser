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
package dorkbox.peParser.headers.resources;

import dorkbox.peParser.ByteArray;
import dorkbox.peParser.headers.Header;
import dorkbox.peParser.headers.SectionTableEntry;
import dorkbox.peParser.types.DWORD;
import dorkbox.peParser.types.TimeDate;
import dorkbox.peParser.types.WORD;

public class ResourceDirectoryHeader extends Header {

    public final DWORD RSRC_CHARACTERISTICS;
    public final TimeDate TIME_STAMP;
    public final WORD MAJOR_VERSION;
    public final WORD MINOR_VERSION;
    public final WORD NUM_NAME_ENTRIES;
    public final WORD NUM_ID_ENTRIES;

    public ResourceDirectoryEntry[] entries;

    public ResourceDirectoryHeader(ByteArray bytes, SectionTableEntry section, int level) {
        this.RSRC_CHARACTERISTICS = new DWORD(bytes.readUInt(4), "Resource Characteristics"); // not used.
        this.TIME_STAMP = new TimeDate(bytes.readUInt(4), "Date");  // The time that the resource data was created by the resource compiler.
        this.MAJOR_VERSION = new WORD(bytes.readUShort(2), "Major Version");
        this.MINOR_VERSION = new WORD(bytes.readUShort(2), "Minor Version");
        this.NUM_NAME_ENTRIES = new WORD(bytes.readUShort(2), "Number of Name Entries");
        this.NUM_ID_ENTRIES = new WORD(bytes.readUShort(2), "Number of ID Entries");


        int numberOfNamedEntires = this.NUM_NAME_ENTRIES.get().intValue();
        int numberOfIDEntires = this.NUM_ID_ENTRIES.get().intValue();

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
