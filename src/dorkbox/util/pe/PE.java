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
package dorkbox.util.pe;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

import dorkbox.util.OS;
import dorkbox.util.pe.headers.COFFFileHeader;
import dorkbox.util.pe.headers.Header;
import dorkbox.util.pe.headers.OptionalHeader;
import dorkbox.util.pe.headers.SectionTable;
import dorkbox.util.pe.headers.SectionTableEntry;
import dorkbox.util.pe.headers.resources.ResourceDataEntry;
import dorkbox.util.pe.headers.resources.ResourceDirectoryEntry;
import dorkbox.util.pe.headers.resources.ResourceDirectoryHeader;
import dorkbox.util.pe.misc.DirEntry;
import dorkbox.util.pe.types.ByteDefinition;
import dorkbox.util.pe.types.ImageDataDir;

public class PE {
    // info from:
    // http://evilzone.org/tutorials/(paper)-portable-executable-format-and-its-rsrc-section/
    // http://www.skynet.ie/~caolan/pub/winresdump/winresdump/doc/pefile.html  (older version of the doc...)
    // http://www.csn.ul.ie/~caolan/pub/winresdump/winresdump/doc/pefile2.html
    // http://msdn.microsoft.com/en-us/library/ms809762.aspx


    private static final int PE_OFFSET_LOCATION = 0x3c;
    private static final byte[] PE_SIG = "PE\0\0".getBytes();

    // TODO: should use an input stream to load header info, instead of the entire thing!
    public ByteArray fileBytes = null;

    private COFFFileHeader coffHeader;
    public OptionalHeader optionalHeader;
    private SectionTable sectionTable;


    public PE(String fileName) {
        File file = new File(fileName);
        try {
            fromInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fromInputStream(File file) throws FileNotFoundException, IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);

        byte[] buffer = new byte[4096];
        int read = 0;
        while ((read = fileInputStream.read(buffer)) > 0) {
            baos.write(buffer, 0, read);
        }
        baos.flush();
        fileInputStream.close();

        this.fileBytes = new ByteArray(baos.toByteArray());

        // initialize header info
        if (isPE()) {
            int offset = getPEOffset() + PE_SIG.length;
            this.fileBytes.seek(offset);

            this.coffHeader = new COFFFileHeader(this.fileBytes);
            this.optionalHeader = new OptionalHeader(this.fileBytes);

            int numberOfEntries = this.coffHeader.SECTION_NR.get();
            this.sectionTable = new SectionTable(this.fileBytes, numberOfEntries);

            // now the bytes are positioned at the start of the section table. ALl other info MUST be done relative to byte offsets/locations!

            // fixup directory names -> table names (from spec!)
            for (SectionTableEntry section : this.sectionTable.sections) {
                int sectionAddress = section.VIRTUAL_ADDRESS.get();
                int sectionSize = section.SIZE_OF_RAW_DATA.get();

                for (ImageDataDir entry : this.optionalHeader.tables) {
                    int optionAddress = entry.get();

                    if (sectionAddress <= optionAddress &&
                        sectionAddress + sectionSize > optionAddress) {

                        entry.setSection(section);
                        break;
                    }
                }
            }

            // fixup directories
            for (ImageDataDir entry : this.optionalHeader.tables) {
                if (entry.getType() == DirEntry.RESOURCE) {
                    // fixup resources
                    SectionTableEntry section = entry.getSection();
                    int delta = section.VIRTUAL_ADDRESS.get() - section.POINTER_TO_RAW_DATA.get();
                    int offsetInFile = entry.get() - delta;
                    this.fileBytes.seek(offsetInFile);
                    this.fileBytes.mark(); // resource data is offset from the beginning of the header!

                    Header root = new ResourceDirectoryHeader(this.fileBytes, section, 0);
                    entry.data = root;
                }
            }
        }
    }

    public String getInfo() {
        if (isPE()) {
            StringBuilder b = new StringBuilder();

            b.append("PE signature offset: ").append(getPEOffset()).append(OS.LINE_SEPARATOR)
             .append("PE signature correct: ").append("yes").append( OS.LINE_SEPARATOR)
             .append(OS.LINE_SEPARATOR)
             .append("----------------").append(OS.LINE_SEPARATOR)
             .append("COFF header info").append(OS.LINE_SEPARATOR)
             .append("----------------").append(OS.LINE_SEPARATOR);

            for (ByteDefinition<?> bd : this.coffHeader.headers) {
                bd.format(b);
            }
            b.append(OS.LINE_SEPARATOR);

            b.append("--------------------").append(OS.LINE_SEPARATOR)
             .append("Optional header info").append(OS.LINE_SEPARATOR)
             .append("--------------------").append(OS.LINE_SEPARATOR);

            for (ByteDefinition<?> bd : this.optionalHeader.headers) {
                bd.format(b);
            }
            b.append(OS.LINE_SEPARATOR);


             b.append(OS.LINE_SEPARATOR)
              .append("-------------").append(OS.LINE_SEPARATOR)
              .append("Section Table").append(OS.LINE_SEPARATOR)
              .append("-------------").append(OS.LINE_SEPARATOR)
              .append(OS.LINE_SEPARATOR);

             for (SectionTableEntry section : this.sectionTable.sections) {
                 for (ByteDefinition<?> bd : section.headers) {
                     bd.format(b);
                 }
             }

             b.append(OS.LINE_SEPARATOR);
             return b.toString();
        } else {
            return "PE signature not found. The given file is not a PE file." + OS.LINE_SEPARATOR;
        }
    }

    private int getPEOffset() {
        this.fileBytes.mark();
        this.fileBytes.seek(PE_OFFSET_LOCATION);
        int read = this.fileBytes.readUShort(2);
        this.fileBytes.reset();
        return read;
    }

    public boolean isPE() {
        // always have to start from zero if we ask this.
        int offset = getPEOffset();
        int saved = this.fileBytes.position();
        this.fileBytes.seek(0);
            try {
                for (int i = 0; i < PE_SIG.length; i++) {
                if (this.fileBytes.readRaw(offset + i) != PE_SIG[i]) {
                    return false;
                }
            }
            return true;
        } finally {
            this.fileBytes.seek(saved);
        }
    }

    @SuppressWarnings("null")
    public ByteArrayInputStream getLargestResourceAsStream() {
        for (ImageDataDir entry : this.optionalHeader.tables) {
            if (entry.getType() == DirEntry.RESOURCE) {
                ResourceDataEntry check = null;

                LinkedList<ResourceDirectoryEntry> LIST = new LinkedList<ResourceDirectoryEntry>();
                ResourceDirectoryHeader root = (ResourceDirectoryHeader) entry.data;
                for (ResourceDirectoryEntry rootEntry : root.entries) {
                    LIST.add(rootEntry);
                }

                while(LIST.peek() != null) {
                    ResourceDataEntry valid = check(check, LIST, LIST.poll());
                    if (valid != null) {
                        check = valid;
                    }
                }

                // now return our resource, but it has to be wrapped in a new stream!
                return new ByteArrayInputStream(check.getData(this.fileBytes));
            }
        }
        return null;
    }

    private ResourceDataEntry check(ResourceDataEntry check, LinkedList<ResourceDirectoryEntry> LIST, ResourceDirectoryEntry entry) {
        if (entry.isDirectory) {
            for (ResourceDirectoryEntry rootEntry : entry.directory.entries) {
                LIST.add(rootEntry);
            }
        } else {
            // this is what we are looking for!
            ResourceDataEntry dataEntry = entry.resourceDataEntry;
            if (check == null || check.SIZE.get() < dataEntry.SIZE.get()) {
                return dataEntry;
            }
        }
        return null;
    }
}
