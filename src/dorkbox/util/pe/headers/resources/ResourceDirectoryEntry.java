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
import dorkbox.util.pe.types.DWORD;
import dorkbox.util.pe.types.ResourceDirName;

public class ResourceDirectoryEntry extends Header {

    public static final int HEADER_SIZE = 8;

    private static final int DATA_IS_DIRECTORY_MASK = 0x80000000;
    private static final int ENTRY_OFFSET_MASK = 0x7FFFFFFF;

    public final ResourceDirName NAME;

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
    public final DWORD DATA_OFFSET;

    // is this a directory (according to above) or an entry?
    public final boolean isDirectory;

    public final int level;

    public ResourceDirectoryHeader directory = null;
    public ResourceDataEntry resourceDataEntry = null;


    /**
     * @param level "Type", "Name", or "Language ID" entry, depending on level of table.
     */
    public ResourceDirectoryEntry(ByteArray bytes, SectionTableEntry section, int level) {
        this.level = level;
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

        this.NAME = h(new ResourceDirName(bytes.readUInt(4), "name", bytes, level));
        this.DATA_OFFSET = h(new DWORD(bytes.readUInt(4), "data offset"));


        long dataOffset = ENTRY_OFFSET_MASK & this.DATA_OFFSET.get().longValue();
        if (dataOffset == 0L) {
            // if it is ZERO, than WTF? is it a a directory header! (maybe?)
            this.isDirectory = false;
            return;
        }

        if (dataOffset > Integer.MAX_VALUE) {
            throw new RuntimeException("Unable to set offset to more than 2gb!");
        }

        this.isDirectory = 0L != (DATA_IS_DIRECTORY_MASK & this.DATA_OFFSET.get().longValue());

        int saved = bytes.position();
        bytes.seek(bytes.marked() + (int) dataOffset);
//        System.err.println(Integer.toHexString(bytes.position));

        if (this.isDirectory) {
            this.directory = new ResourceDirectoryHeader(bytes, section, level);
        } else {
            this.resourceDataEntry = new ResourceDataEntry(bytes, section);
        }

        bytes.seek(saved);
    }
}
