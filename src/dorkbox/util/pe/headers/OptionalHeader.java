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
import dorkbox.util.pe.misc.MagicNumber;
import dorkbox.util.pe.types.ByteDefinition;
import dorkbox.util.pe.types.HeaderDefinition;
import dorkbox.util.pe.types.ImageDataDir;
import dorkbox.util.pe.types.ImageDataDirExtra;
import dorkbox.util.pe.types.ULong;
import dorkbox.util.pe.types.ULongImageBase;
import dorkbox.util.pe.types.ULongLong;
import dorkbox.util.pe.types.ULongLongImageBase;
import dorkbox.util.pe.types.ULongRva;
import dorkbox.util.pe.types.UShort;
import dorkbox.util.pe.types.UShortDllCharacteristics;
import dorkbox.util.pe.types.UShortMagicNumber;
import dorkbox.util.pe.types.UShortSubsystem;

public class OptionalHeader extends Header {

    public List<ImageDataDir> tables = new ArrayList<ImageDataDir>(0);

    //
    // Standard fields.
    //
    public final UShortMagicNumber MAGIC_NUMBER;
    public final UShort MAJOR_LINKER_VERSION;
    public final UShort MINOR_LINKER_VERSION;
    public final ULong SIZE_OF_CODE;
    public final ULong SIZE_OF_INIT_DATA;
    public final ULong SIZE_OF_UNINIT_DATA;
    public final ULong ADDR_OF_ENTRY_POINT;
    public final ULong BASE_OF_CODE;
    public final ULong BASE_OF_DATA;

    private boolean IS_32_BIT;

    //
    // NT additional fields.
    //
    @SuppressWarnings("rawtypes")
    public final ByteDefinition IMAGE_BASE;
    public final ULong SECTION_ALIGNMENT;
    public final ULong FILE_ALIGNMENT;
    public final UShort MAJOR_OS_VERSION;
    public final UShort MINOR_OS_VERSION;
    public final UShort MAJOR_IMAGE_VERSION;
    public final UShort MINOR_IMAGE_VERSION;
    public final UShort MAJOR_SUBSYSTEM_VERSION;
    public final UShort MINOR_SUBSYSTEM_VERSION;
    public final ULong WIN32_VERSION_VALUE;
    public final ULongLong SIZE_OF_IMAGE;
    public final ULong SIZE_OF_HEADERS;
    public final ULong CHECKSUM;
    public final UShortSubsystem SUBSYSTEM;
    public final UShortDllCharacteristics DLL_CHARACTERISTICS;
    @SuppressWarnings("rawtypes")
    public final ByteDefinition SIZE_OF_STACK_RESERVE;
    @SuppressWarnings("rawtypes")
    public final ByteDefinition SIZE_OF_STACK_COMMIT;
    @SuppressWarnings("rawtypes")
    public final ByteDefinition SIZE_OF_HEAP_RESERVE;
    @SuppressWarnings("rawtypes")
    public final ByteDefinition SIZE_OF_HEAP_COMMIT;
    public final ULong LOADER_FLAGS;
    public final ULongRva NUMBER_OF_RVA_AND_SIZES;


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

        this.MAGIC_NUMBER = h(new UShortMagicNumber(bytes.readUShort(2), "magic number"));
        this.MAJOR_LINKER_VERSION = h(new UShort(bytes.readUShort(1), "major linker version"));
        this.MINOR_LINKER_VERSION = h(new UShort(bytes.readUShort(1), "minor linker version"));
        this.SIZE_OF_CODE = h(new ULong(bytes.readUInt(4), "size of code"));
        this.SIZE_OF_INIT_DATA = h(new ULong(bytes.readUInt(4), "size of initialized data"));
        this.SIZE_OF_UNINIT_DATA = h(new ULong(bytes.readUInt(4), "size of unitialized data"));
        this.ADDR_OF_ENTRY_POINT = h(new ULong(bytes.readUInt(4), "address of entry point"));
        this.BASE_OF_CODE = h(new ULong(bytes.readUInt(4), "address of base of code"));
        this.BASE_OF_DATA = h(new ULong(bytes.readUInt(4), "address of base of data"));

        this.IS_32_BIT = this.MAGIC_NUMBER.get() == MagicNumber.PE32;

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
            this.IMAGE_BASE = h(new ULongImageBase(bytes.readUInt(4), "image base"));
        } else {
            this.IMAGE_BASE = h(new ULongLongImageBase(bytes.readULong(8), "image base"));
        }

        this.SECTION_ALIGNMENT = h(new ULong(bytes.readUInt(4), "section alignment in bytes"));
        this.FILE_ALIGNMENT = h(new ULong(bytes.readUInt(4), "file alignment in bytes"));

        this.MAJOR_OS_VERSION = h(new UShort(bytes.readUShort(2), "major operating system version"));
        this.MINOR_OS_VERSION = h(new UShort(bytes.readUShort(2), "minor operating system version"));
        this.MAJOR_IMAGE_VERSION = h(new UShort(bytes.readUShort(2), "major image version"));
        this.MINOR_IMAGE_VERSION = h(new UShort(bytes.readUShort(2), "minor image version"));
        this.MAJOR_SUBSYSTEM_VERSION = h(new UShort(bytes.readUShort(2), "major subsystem version"));
        this.MINOR_SUBSYSTEM_VERSION = h(new UShort(bytes.readUShort(2), "minor subsystem version"));

        this.WIN32_VERSION_VALUE = h(new ULong(bytes.readUInt(4), "win32 version value (reserved, must be zero)"));
        this.SIZE_OF_IMAGE = h(new ULongLong(bytes.readULong(4), "size of image in bytes"));
        this.SIZE_OF_HEADERS = h(new ULong(bytes.readUInt(4), "size of headers (MS DOS stub, PE header, and section headers)"));
        this.CHECKSUM = h(new ULong(bytes.readUInt(4), "checksum"));
        this.SUBSYSTEM = h(new UShortSubsystem(bytes.readUShort(2), "subsystem"));
        this.DLL_CHARACTERISTICS = h(new UShortDllCharacteristics(bytes.readUShort(2), "dll characteristics"));

        if (this.IS_32_BIT) {
            this.SIZE_OF_STACK_RESERVE = h(new ULong(bytes.readUInt(4), "size of stack reserve"));
            this.SIZE_OF_STACK_COMMIT = h(new ULong(bytes.readUInt(4), "size of stack commit"));
            this.SIZE_OF_HEAP_RESERVE = h(new ULong(bytes.readUInt(4), "size of heap reserve"));
            this.SIZE_OF_HEAP_COMMIT = h(new ULong(bytes.readUInt(4), "size of heap commit"));
        } else {
            this.SIZE_OF_STACK_RESERVE = h(new ULongLong(bytes.readULong(8), "size of stack reserve"));
            this.SIZE_OF_STACK_COMMIT = h(new ULongLong(bytes.readULong(8), "size of stack commit"));
            this.SIZE_OF_HEAP_RESERVE = h(new ULongLong(bytes.readULong(8), "size of heap reserve"));
            this.SIZE_OF_HEAP_COMMIT = h(new ULongLong(bytes.readULong(8), "size of heap commit"));
        }

        this.LOADER_FLAGS = h(new ULong(bytes.readUInt(4), "loader flags (reserved, must be zero)"));
        this.NUMBER_OF_RVA_AND_SIZES = h(new ULongRva(bytes.readUInt(4), "number of rva and sizes"));


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
