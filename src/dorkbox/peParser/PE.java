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
package dorkbox.peParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import dorkbox.os.OS;
import dorkbox.peParser.headers.COFFFileHeader;
import dorkbox.peParser.headers.Header;
import dorkbox.peParser.headers.OptionalHeader;
import dorkbox.peParser.headers.SectionTable;
import dorkbox.peParser.headers.SectionTableEntry;
import dorkbox.peParser.headers.resources.ResourceDataEntry;
import dorkbox.peParser.headers.resources.ResourceDirectoryEntry;
import dorkbox.peParser.headers.resources.ResourceDirectoryHeader;
import dorkbox.peParser.misc.DirEntry;
import dorkbox.peParser.types.ByteDefinition;
import dorkbox.peParser.types.ImageDataDir;

public class PE {
    // info from:
    // http://evilzone.org/tutorials/(paper)-portable-executable-format-and-its-rsrc-section/
    // http://www.skynet.ie/~caolan/pub/winresdump/winresdump/doc/pefile.html  (older version of the doc...)
    // http://www.csn.ul.ie/~caolan/pub/winresdump/winresdump/doc/pefile2.html
    // http://msdn.microsoft.com/en-us/library/ms809762.aspx

    /**
     * Gets the version number.
     */
    public static
    String getVersion() {
        return "2.14";
    }

    private static final int PE_OFFSET_LOCATION = 0x3c;
    private static final byte[] PE_SIG = "PE\0\0".getBytes();

    static {
        // Add this project to the updates system, which verifies this class + UUID + version information
        dorkbox.updates.Updates.INSTANCE.add(PE.class, "5f5fafe156ba4e8f94c28f0c283aa509", getVersion());
    }

    // TODO: should use an input stream to load header info, instead of the entire thing!
    public ByteArray fileBytes = null;

    private COFFFileHeader coffHeader;
    public OptionalHeader optionalHeader;
    private SectionTable sectionTable;
    private boolean invalidFile;


    public PE(String fileName) {
        File file = new File(fileName);
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fromInputStream(fileInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PE(InputStream inputStream) {
        try {
            fromInputStream(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fromInputStream(InputStream inputStream) throws FileNotFoundException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);

        byte[] buffer = new byte[4096];
        int read = 0;
        while ((read = inputStream.read(buffer)) > 0) {
            baos.write(buffer, 0, read);
        }
        baos.flush();
        inputStream.close();

        byte[] bytes = baos.toByteArray();
        invalidFile = bytes.length == 0;

        this.fileBytes = new ByteArray(bytes);

        // initialize header info
        if (isPE()) {
            int offset = getPEOffset() + PE_SIG.length;
            this.fileBytes.seek(offset);

            this.coffHeader = new COFFFileHeader(this.fileBytes);
            this.optionalHeader = new OptionalHeader(this.fileBytes);

            int numberOfEntries = this.coffHeader.NumberOfSections.get().intValue();
            this.sectionTable = new SectionTable(this.fileBytes, numberOfEntries);

            // now the bytes are positioned at the start of the section table. ALl other info MUST be done relative to byte offsets/locations!

            // fixup directory names -> table names (from spec!)
            for (SectionTableEntry section : this.sectionTable.sections) {
                long sectionAddress = section.VIRTUAL_ADDRESS.get().longValue();
                long sectionSize = section.SIZE_OF_RAW_DATA.get().longValue();

                for (ImageDataDir entry : this.optionalHeader.tables) {
                    long optionAddress = entry.get().longValue();

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
                    if (section != null) {
                        long delta = section.VIRTUAL_ADDRESS.get().longValue() - section.POINTER_TO_RAW_DATA.get().longValue();
                        long offsetInFile = entry.get().longValue() - delta;

                        if (offsetInFile > Integer.MAX_VALUE) {
                            throw new RuntimeException("Unable to set offset to more than 2gb!");
                        }

                        this.fileBytes.seek((int) offsetInFile);
                        this.fileBytes.mark(); // resource data is offset from the beginning of the header!

                        Header root = new ResourceDirectoryHeader(this.fileBytes, section, 0);
                        entry.data = root;
                    }
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
        int read = this.fileBytes.readUShort(2).intValue();
        this.fileBytes.reset();
        return read;
    }

    public boolean isPE() {
        if (invalidFile) {
            return false;
        }

        int saved = -1;
        try {
            // this can screw up if the length of the file is invalid...
            int offset = getPEOffset();
            saved = this.fileBytes.position();

            // always have to start from zero if we ask this.
            this.fileBytes.seek(0);

            for (int i = 0; i < PE_SIG.length; i++) {
                if (this.fileBytes.readRaw(offset + i) != PE_SIG[i]) {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (saved != -1) {
                this.fileBytes.seek(saved);
            }
        }
    }

    public ByteArrayInputStream getLargestResourceAsStream() {
        for (ImageDataDir mainEntry : this.optionalHeader.tables) {
            if (mainEntry.getType() == DirEntry.RESOURCE) {


                LinkedList<ResourceDirectoryEntry> directoryEntries = new LinkedList<ResourceDirectoryEntry>();
                LinkedList<ResourceDirectoryEntry> resourceEntries = new LinkedList<ResourceDirectoryEntry>();

                ResourceDirectoryEntry entry = null;
                ResourceDirectoryHeader root = (ResourceDirectoryHeader) mainEntry.data;

                for (ResourceDirectoryEntry rootEntry : root.entries) {
                    collect(directoryEntries, resourceEntries, rootEntry);
                    directoryEntries.add(rootEntry);
                }

                while ((entry = directoryEntries.poll()) != null) {
                    collect(directoryEntries, resourceEntries, entry);
                }

                ResourceDataEntry largest = null;
                for (ResourceDirectoryEntry resourceEntry : resourceEntries) {
                    ResourceDataEntry dataEntry = resourceEntry.resourceDataEntry;

                    if (largest == null || largest.SIZE.get().longValue() < dataEntry.SIZE.get().longValue()) {
                        largest = dataEntry;
                    }
                }

                // now return our resource, but it has to be wrapped in a new stream!
                return new ByteArrayInputStream(largest.getData(this.fileBytes));
            }
        }
        return null;
    }

    public static String getVersion(String executablePath) throws Exception {
        PE pe = new PE(executablePath);

        if (pe.invalidFile) {
            throw new Exception("No version found:" + executablePath);
        }

        for (ImageDataDir mainEntry : pe.optionalHeader.tables) {
            if (mainEntry.getType() == DirEntry.RESOURCE) {
                ResourceDirectoryHeader root = (ResourceDirectoryHeader) mainEntry.data;
                for (ResourceDirectoryEntry rootEntry : root.entries) {
                    if ("Version".equals(rootEntry.NAME.get())) {
                        byte[] versionInfoData = rootEntry.directory.entries[0].directory.entries[0].resourceDataEntry.getData(pe.fileBytes);
                        int fileVersionIndex = indexOf(versionInfoData, includeNulls("FileVersion")) + 26;
                        int fileVersionEndIndex = indexOf(versionInfoData, new byte[]{0x00, 0x00}, fileVersionIndex);
                        return removeNulls(new String(versionInfoData, fileVersionIndex, fileVersionEndIndex - fileVersionIndex));
                    }
                }
            }
        }

        throw new Exception("No version found:" + executablePath);
    }

    private static byte[] includeNulls(String str){
        char[] chars = str.toCharArray();
        byte[] result = new byte[chars.length*2];

        for (int i = 0, j = 0; i < result.length; i += 2, j++) {
            result[i] = (byte) chars[j];
        }

        return result;
    }

    private static String removeNulls(String str) {
        return str == null ? null : str.replaceAll("\\x00", "");
    }

    public static int indexOf(byte[] outerArray, byte[] smallerArray) {
        return indexOf(outerArray, smallerArray, 0);
    }

    public static int indexOf(byte[] outerArray, byte[] smallerArray, int begin) {
        for (int i = begin; i < outerArray.length - smallerArray.length + 1; ++i) {
            boolean found = true;
            for (int j = 0; j < smallerArray.length; ++j) {
                if (outerArray[i + j] != smallerArray[j]) {
                    found = false;
                    break;
                }
            }
            if (found) {
                return i;
            }
        }
        return -1;
    }

    private
    void collect(final LinkedList<ResourceDirectoryEntry> directoryEntries,
                 final LinkedList<ResourceDirectoryEntry> resourceEntries,
                 final ResourceDirectoryEntry entry) {

        if (entry.isDirectory) {
            for (ResourceDirectoryEntry dirEntry : entry.directory.entries) {
                directoryEntries.add(dirEntry);
            }
        } else {
            resourceEntries.add(entry);
        }
    }
}

