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
package dorkbox.util.pe.headers;

import java.util.ArrayList;
import java.util.List;

import dorkbox.util.pe.ByteArray;
import dorkbox.util.pe.misc.DirEntry;
import dorkbox.util.pe.misc.MagicNumberType;
import dorkbox.util.pe.types.ByteDefinition;
import dorkbox.util.pe.types.DWORD;
import dorkbox.util.pe.types.HeaderDefinition;
import dorkbox.util.pe.types.ImageBase;
import dorkbox.util.pe.types.ImageBase_Wide;
import dorkbox.util.pe.types.ImageDataDir;
import dorkbox.util.pe.types.ImageDataDirExtra;
import dorkbox.util.pe.types.MagicNumber;
import dorkbox.util.pe.types.DWORD_WIDE;
import dorkbox.util.pe.types.RVA;
import dorkbox.util.pe.types.DllCharacteristics;
import dorkbox.util.pe.types.Subsystem;
import dorkbox.util.pe.types.WORD;

public class OptionalHeader extends Header {

    // see: http://msdn.microsoft.com/en-us/library/ms809762.aspx

    public List<ImageDataDir> tables = new ArrayList<ImageDataDir>(0);

    //
    // Standard fields.
    //
    public final MagicNumber MAGIC_NUMBER;
    public final WORD MAJOR_LINKER_VERSION;
    public final WORD MINOR_LINKER_VERSION;
    public final DWORD SIZE_OF_CODE;
    public final DWORD SIZE_OF_INIT_DATA;
    public final DWORD SIZE_OF_UNINIT_DATA;
    public final DWORD ADDR_OF_ENTRY_POINT;
    public final DWORD BASE_OF_CODE;
    public final DWORD BASE_OF_DATA;

    private boolean IS_32_BIT;

    //
    // NT additional fields.
    //
    @SuppressWarnings("rawtypes")
    public final ByteDefinition IMAGE_BASE;
    public final DWORD SECTION_ALIGNMENT;
    public final DWORD FILE_ALIGNMENT;
    public final WORD MAJOR_OS_VERSION;
    public final WORD MINOR_OS_VERSION;
    public final WORD MAJOR_IMAGE_VERSION;
    public final WORD MINOR_IMAGE_VERSION;
    public final WORD MAJOR_SUBSYSTEM_VERSION;
    public final WORD MINOR_SUBSYSTEM_VERSION;
    public final DWORD WIN32_VERSION_VALUE;
    public final DWORD SIZE_OF_IMAGE;
    public final DWORD SIZE_OF_HEADERS;
    public final DWORD CHECKSUM;
    public final Subsystem SUBSYSTEM;
    public final DllCharacteristics DLL_CHARACTERISTICS;
    @SuppressWarnings("rawtypes")
    public final ByteDefinition SIZE_OF_STACK_RESERVE;
    @SuppressWarnings("rawtypes")
    public final ByteDefinition SIZE_OF_STACK_COMMIT;
    @SuppressWarnings("rawtypes")
    public final ByteDefinition SIZE_OF_HEAP_RESERVE;
    @SuppressWarnings("rawtypes")
    public final ByteDefinition SIZE_OF_HEAP_COMMIT;
    public final DWORD LOADER_FLAGS;
    public final RVA NUMBER_OF_RVA_AND_SIZES;


    public final ImageDataDir EXPORT_TABLE;
    public final ImageDataDir IMPORT_TABLE;
    public final ImageDataDir RESOURCE_TABLE;
    public final ImageDataDir EXCEPTION_TABLE;
    public final ImageDataDir CERTIFICATE_TABLE;
    public final ImageDataDir BASE_RELOCATION_TABLE;

    public final ImageDataDir DEBUG;
    public final ImageDataDir COPYRIGHT;
    public final ImageDataDir GLOBAL_PTR;
    public final ImageDataDir TLS_TABLE;
    public final ImageDataDir LOAD_CONFIG_TABLE;

    public final ImageDataDirExtra BOUND_IMPORT;
    public final ImageDataDirExtra IAT;
    public final ImageDataDirExtra DELAY_IMPORT_DESCRIPTOR;
    public final ImageDataDirExtra CLR_RUNTIME_HEADER;

    public OptionalHeader(ByteArray bytes) {
        // the header length is variable.

        //
        // Standard fields.
        //
        h(new HeaderDefinition("Standard fields"));
        bytes.mark();

        this.MAGIC_NUMBER = h(new MagicNumber(bytes.readUShort(2), "magic number"));
        this.MAJOR_LINKER_VERSION = h(new WORD(bytes.readUShort(1), "major linker version"));
        this.MINOR_LINKER_VERSION = h(new WORD(bytes.readUShort(1), "minor linker version"));
        this.SIZE_OF_CODE = h(new DWORD(bytes.readUInt(4), "size of code"));
        this.SIZE_OF_INIT_DATA = h(new DWORD(bytes.readUInt(4), "size of initialized data"));
        this.SIZE_OF_UNINIT_DATA = h(new DWORD(bytes.readUInt(4), "size of unitialized data"));
        this.ADDR_OF_ENTRY_POINT = h(new DWORD(bytes.readUInt(4), "address of entry point"));
        this.BASE_OF_CODE = h(new DWORD(bytes.readUInt(4), "address of base of code"));
        this.BASE_OF_DATA = h(new DWORD(bytes.readUInt(4), "address of base of data"));

        this.IS_32_BIT = this.MAGIC_NUMBER.get() == MagicNumberType.PE32;

        bytes.reset();
        if (this.IS_32_BIT) {
            bytes.skip(28);
        } else {
            bytes.skip(24);
        }


        //
        // Standard fields.
        //
        h(new HeaderDefinition("Windows specific fields"));

        if (this.IS_32_BIT) {
            this.IMAGE_BASE = h(new ImageBase(bytes.readUInt(4), "image base"));
        } else {
            this.IMAGE_BASE = h(new ImageBase_Wide(bytes.readULong(8), "image base"));
        }

        this.SECTION_ALIGNMENT = h(new DWORD(bytes.readUInt(4), "section alignment in bytes"));
        this.FILE_ALIGNMENT = h(new DWORD(bytes.readUInt(4), "file alignment in bytes"));

        this.MAJOR_OS_VERSION = h(new WORD(bytes.readUShort(2), "major operating system version"));
        this.MINOR_OS_VERSION = h(new WORD(bytes.readUShort(2), "minor operating system version"));
        this.MAJOR_IMAGE_VERSION = h(new WORD(bytes.readUShort(2), "major image version"));
        this.MINOR_IMAGE_VERSION = h(new WORD(bytes.readUShort(2), "minor image version"));
        this.MAJOR_SUBSYSTEM_VERSION = h(new WORD(bytes.readUShort(2), "major subsystem version"));
        this.MINOR_SUBSYSTEM_VERSION = h(new WORD(bytes.readUShort(2), "minor subsystem version"));

        this.WIN32_VERSION_VALUE = h(new DWORD(bytes.readUInt(4), "win32 version value (reserved, must be zero)"));
        this.SIZE_OF_IMAGE = h(new DWORD(bytes.readUInt(4), "size of image in bytes"));
        this.SIZE_OF_HEADERS = h(new DWORD(bytes.readUInt(4), "size of headers (MS DOS stub, PE header, and section headers)"));
        this.CHECKSUM = h(new DWORD(bytes.readUInt(4), "checksum"));
        this.SUBSYSTEM = h(new Subsystem(bytes.readUShort(2), "subsystem"));
        this.DLL_CHARACTERISTICS = h(new DllCharacteristics(bytes.readUShort(2), "dll characteristics"));

        if (this.IS_32_BIT) {
            this.SIZE_OF_STACK_RESERVE = h(new DWORD(bytes.readUInt(4), "size of stack reserve"));
            this.SIZE_OF_STACK_COMMIT = h(new DWORD(bytes.readUInt(4), "size of stack commit"));
            this.SIZE_OF_HEAP_RESERVE = h(new DWORD(bytes.readUInt(4), "size of heap reserve"));
            this.SIZE_OF_HEAP_COMMIT = h(new DWORD(bytes.readUInt(4), "size of heap commit"));
        } else {
            this.SIZE_OF_STACK_RESERVE = h(new DWORD_WIDE(bytes.readULong(8), "size of stack reserve"));
            this.SIZE_OF_STACK_COMMIT = h(new DWORD_WIDE(bytes.readULong(8), "size of stack commit"));
            this.SIZE_OF_HEAP_RESERVE = h(new DWORD_WIDE(bytes.readULong(8), "size of heap reserve"));
            this.SIZE_OF_HEAP_COMMIT = h(new DWORD_WIDE(bytes.readULong(8), "size of heap commit"));
        }

        this.LOADER_FLAGS = h(new DWORD(bytes.readUInt(4), "loader flags (reserved, must be zero)"));
        this.NUMBER_OF_RVA_AND_SIZES = h(new RVA(bytes.readUInt(4), "number of rva and sizes"));


        bytes.reset();
        if (this.IS_32_BIT) {
            bytes.skip(96);
        } else {
            bytes.skip(112);
        }

        //
        // Data directories
        //
        h(new HeaderDefinition("Data Directories"));
        this.EXPORT_TABLE = table(h(new ImageDataDir(bytes, DirEntry.EXPORT)));
        this.IMPORT_TABLE = table(h(new ImageDataDir(bytes, DirEntry.IMPORT)));
        this.RESOURCE_TABLE = table(h(new ImageDataDir(bytes, DirEntry.RESOURCE)));
        this.EXCEPTION_TABLE = table(h(new ImageDataDir(bytes, DirEntry.EXCEPTION)));
        this.CERTIFICATE_TABLE = table(h(new ImageDataDir(bytes, DirEntry.SECURITY)));
        this.BASE_RELOCATION_TABLE = table(h(new ImageDataDir(bytes, DirEntry.BASERELOC)));

        this.DEBUG = table(h(new ImageDataDir(bytes, DirEntry.DEBUG)));
        this.COPYRIGHT = table(h(new ImageDataDir(bytes, DirEntry.COPYRIGHT)));
        this.GLOBAL_PTR = table(h(new ImageDataDir(bytes, DirEntry.GLOBALPTR)));
        this.TLS_TABLE = table(h(new ImageDataDir(bytes, DirEntry.TLS)));
        this.LOAD_CONFIG_TABLE = table(h(new ImageDataDir(bytes, DirEntry.LOAD_CONFIG)));

        this.BOUND_IMPORT = h(new ImageDataDirExtra(bytes, "bound import"));
        this.IAT = h(new ImageDataDirExtra(bytes, "IAT"));
        this.DELAY_IMPORT_DESCRIPTOR = h(new ImageDataDirExtra(bytes, "delay import descriptor"));
        this.CLR_RUNTIME_HEADER = h(new ImageDataDirExtra(bytes, "COM+ runtime header"));

        // reserved 8 bytes!!
        bytes.skip(8);
    }

    private <T extends ImageDataDir> T table(T object) {
        this.tables.add(object);
        return object;
    }
}
