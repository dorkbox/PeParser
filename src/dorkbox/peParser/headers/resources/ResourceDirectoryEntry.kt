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
package dorkbox.peParser.headers.resources

import dorkbox.peParser.ByteArray
import dorkbox.peParser.headers.Header
import dorkbox.peParser.headers.SectionTableEntry
import dorkbox.peParser.types.DWORD
import dorkbox.peParser.types.ResourceDirName

class ResourceDirectoryEntry(bytes: ByteArray, section: SectionTableEntry?, val level: Int) : Header() {
    companion object {
        const val HEADER_SIZE = 8
        private const val DATA_IS_DIRECTORY_MASK = -0x80000000
        private const val ENTRY_OFFSET_MASK = 0x7FFFFFFF
    }

    val NAME: ResourceDirName?

    /**
     * This field is either an offset to another resource directory or a pointer to information about a specific resource instance.
     *
     * If the high bit (0x80000000) is set, this directory entry refers to a subdirectory.
     * The lower 31 bits are an offset (relative to the start of the resources) to another IMAGE_RESOURCE_DIRECTORY.
     *
     * If the high bit isn't set, the lower 31 bits point to an IMAGE_RESOURCE_DATA_ENTRY structure.
     *
     * The IMAGE_RESOURCE_DATA_ENTRY structure contains the location of the resource's raw data, its size, and its code page.
     */
    val DATA_OFFSET: DWORD?

    // is this a directory (according to above) or an entry?
    var isDirectory: Boolean
    var directory: ResourceDirectoryHeader? = null
    var resourceDataEntry: ResourceDataEntry? = null

    /**
     * @param level "Type", "Name", or "Language ID" entry, depending on level of table.
     */
    init {
        //        System.err.println(Integer.toHexString(bytes.position));

        // when this is level 2, it is the SUB-DIR of a main directory,
        // IE:
        //  ROOT  (lvl 0)
        //   \- Bitmap  (lvl 1)
        //   |- Icons
        //     \- 1
        //     |- 2 (lvl 2)
        //   |- Dialog
        //   |- String
        NAME = h(ResourceDirName(bytes.readUInt(4), "name", bytes, level))
        DATA_OFFSET = h(DWORD(bytes.readUInt(4), "data offset"))
        val dataOffset: Long = (ENTRY_OFFSET_MASK.toLong() and DATA_OFFSET.get().toLong())
        if (dataOffset == 0L) {
            // if it is ZERO, than WTF? is it a directory header! (maybe?)
            isDirectory = false
        }
        else {
            if (dataOffset > (Int.MAX_VALUE.toLong())) {
                throw RuntimeException("Unable to set offset to more than 2gb!")
            }

            isDirectory = 0L != (DATA_IS_DIRECTORY_MASK.toLong() and DATA_OFFSET.get().toLong())
            val saved = bytes.position()
            bytes.seek(bytes.marked() + dataOffset.toInt())
            //        System.err.println(Integer.toHexString(bytes.position));
            if (isDirectory) {
                directory = ResourceDirectoryHeader(bytes, section, level)
            }
            else {
                resourceDataEntry = ResourceDataEntry(bytes, section!!)
            }
            bytes.seek(saved)
        }
    }
}
