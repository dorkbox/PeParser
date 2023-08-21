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
package dorkbox.peParser.headers.resources

import dorkbox.peParser.ByteArray
import dorkbox.peParser.headers.Header
import dorkbox.peParser.headers.SectionTableEntry
import dorkbox.peParser.types.DWORD
import dorkbox.peParser.types.TimeDate
import dorkbox.peParser.types.WORD

class ResourceDirectoryHeader(bytes: ByteArray, section: SectionTableEntry?, level: Int) : Header() {
    val RSRC_CHARACTERISTICS: DWORD
    val TIME_STAMP: TimeDate
    val MAJOR_VERSION: WORD
    val MINOR_VERSION: WORD
    val NUM_NAME_ENTRIES: WORD
    val NUM_ID_ENTRIES: WORD

    var entries: Array<ResourceDirectoryEntry?>

    init {
        RSRC_CHARACTERISTICS = DWORD(bytes.readUInt(4), "Resource Characteristics") // not used.
        TIME_STAMP = TimeDate(bytes.readUInt(4), "Date") // The time that the resource data was created by the resource compiler.
        MAJOR_VERSION = WORD(bytes.readUShort(2), "Major Version")
        MINOR_VERSION = WORD(bytes.readUShort(2), "Minor Version")
        NUM_NAME_ENTRIES = WORD(bytes.readUShort(2), "Number of Name Entries")
        NUM_ID_ENTRIES = WORD(bytes.readUShort(2), "Number of ID Entries")

        val numberOfNamedEntires: Int = NUM_NAME_ENTRIES.get().toInt()
        val numberOfIDEntires: Int = NUM_ID_ENTRIES.get().toInt()
        val numberOfEntries = numberOfNamedEntires + numberOfIDEntires

        entries = arrayOfNulls(numberOfEntries)
        // IE:
        //  ROOT  (lvl 0)
        //   \- Bitmap  (lvl 1)
        //   |- Icons
        //     \- 1
        //     |- 2 (lvl 2)
        //   |- Dialog
        //   |- String
        for (i in 0 until numberOfEntries) {
            entries[i] = ResourceDirectoryEntry(bytes, section, level + 1)
        }
    }
}
