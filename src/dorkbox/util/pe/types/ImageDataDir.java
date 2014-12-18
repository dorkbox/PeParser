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
package dorkbox.util.pe.types;

import dorkbox.util.OS;
import dorkbox.util.pe.ByteArray;
import dorkbox.util.pe.headers.Header;
import dorkbox.util.pe.headers.SectionTableEntry;
import dorkbox.util.pe.misc.DirEntry;

public class ImageDataDir extends ByteDefinition<Integer> {

    private final DirEntry entry;

    private int virtualAddress;
    private int size;

    private SectionTableEntry section;
    public Header data;

    /** 8 bytes each */
    public ImageDataDir(ByteArray bytes, DirEntry entry) {
        super(entry.getDescription());
        this.entry = entry;

        this.virtualAddress = new ULong(bytes.readUInt(4), "Virtual Address").get();
        this.size = new ULong(bytes.readUInt(4), "Size").get();
    }

    public DirEntry getType() {
        return this.entry;
    }

    @Override
    public Integer get() {
        return this.virtualAddress;
    }

    public Integer getSize() {
        return this.size;
    }

    @Override
    public void format(StringBuilder b) {
        b.append(getDescriptiveName()).append(": ").append(OS.LINE_SEPARATOR)
         .append("\t").append("address: ").append(this.virtualAddress).append(" (0x").append(Integer.toHexString(this.virtualAddress)).append(")").append(OS.LINE_SEPARATOR)
         .append("\t").append("size: ").append(this.size).append(" (0x").append(Integer.toHexString(this.size)).append(")").append(OS.LINE_SEPARATOR);
    }

    public void setSection(SectionTableEntry section) {
        this.section = section;
    }

    public SectionTableEntry getSection() {
        return this.section;
    }
}
