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

import dorkbox.peParser.headers.Header
import dorkbox.peParser.headers.SectionTableEntry
import dorkbox.peParser.types.DWORD

class ResourceDataEntry(bytes: dorkbox.peParser.ByteArray, private val section: SectionTableEntry) : Header() {
    // The address of a unit of resource data in the Resource Data area.
    val OFFSET_TO_DATA: DWORD
    val SIZE: DWORD
    val CODE_PAGE: DWORD
    val RESERVED: DWORD

    /**
     * @param section - necessary to know this section for when computing the location of the resource data!
     */
    init {
        OFFSET_TO_DATA = DWORD(bytes.readUInt(4), "offsetToData")
        SIZE = DWORD(bytes.readUInt(4), "Size")
        CODE_PAGE = DWORD(bytes.readUInt(4), "CodePage")
        RESERVED = DWORD(bytes.readUInt(4), "Reserved")
    }

    fun getData(bytes: dorkbox.peParser.ByteArray): ByteArray {
        // this is where to get the data from the ABSOLUTE position in the file!
        val dataOffset = section.POINTER_TO_RAW_DATA.get().toLong() + OFFSET_TO_DATA.get().toLong() - section.VIRTUAL_ADDRESS.get().toLong()
        if (dataOffset > Int.MAX_VALUE) {
            throw RuntimeException("Unable to set offset to more than 2gb!")
        }

        //String asHex = Integer.toHexString(dataOffset);
        val saved = bytes.position()
        bytes.seek(dataOffset.toInt())
        val bytesToCopyLong = SIZE.get().toLong()
        if (bytesToCopyLong > Int.MAX_VALUE) {
            throw RuntimeException("Unable to copy more than 2gb of bytes!")
        }
        val copyBytes = bytes.copyBytes(bytesToCopyLong.toInt())
        bytes.seek(saved)
        return copyBytes
    }
}
