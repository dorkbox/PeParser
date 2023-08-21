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
package dorkbox.peParser.headers

import dorkbox.peParser.ByteArray

class SectionTable(bytes: ByteArray, numberOfEntries: Int) : Header() {
    // more info here: http://msdn.microsoft.com/en-us/library/ms809762.aspx
    var sections: MutableList<SectionTableEntry>

    init {
        sections = ArrayList(numberOfEntries)
        bytes.mark()
        for (i in 0 until numberOfEntries) {
            val offset: Int = i * SectionTableEntry.ENTRY_SIZE // 40 bytes per table entry, no spacing between them
            bytes.skip(offset.toLong())

            val sectionTableEntry = SectionTableEntry(bytes, i + 1, offset, SectionTableEntry.ENTRY_SIZE)
            sections.add(sectionTableEntry)
            bytes.reset()
        }
    }
}
