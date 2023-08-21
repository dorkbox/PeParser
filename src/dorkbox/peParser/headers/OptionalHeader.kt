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
package dorkbox.peParser.headers

import dorkbox.peParser.ByteArray
import dorkbox.peParser.misc.DirEntry
import dorkbox.peParser.misc.MagicNumberType
import dorkbox.peParser.types.*

class OptionalHeader(bytes: ByteArray) : Header() {
    // see: http://msdn.microsoft.com/en-us/library/ms809762.aspx
    var tables: MutableList<ImageDataDir> = ArrayList(0)

    //
    // Standard fields.
    //
    val MAGIC_NUMBER: MagicNumber?
    val MAJOR_LINKER_VERSION: WORD?
    val MINOR_LINKER_VERSION: WORD?
    val SIZE_OF_CODE: DWORD?
    val SIZE_OF_INIT_DATA: DWORD?
    val SIZE_OF_UNINIT_DATA: DWORD?
    val ADDR_OF_ENTRY_POINT: DWORD?
    val BASE_OF_CODE: DWORD?
    val BASE_OF_DATA: DWORD?
    private val IS_32_BIT: Boolean

    //
    // NT additional fields.
    //
    var IMAGE_BASE: ByteDefinition<*>? = null
    val SECTION_ALIGNMENT: DWORD?
    val FILE_ALIGNMENT: DWORD?
    val MAJOR_OS_VERSION: WORD?
    val MINOR_OS_VERSION: WORD?
    val MAJOR_IMAGE_VERSION: WORD?
    val MINOR_IMAGE_VERSION: WORD?
    val MAJOR_SUBSYSTEM_VERSION: WORD?
    val MINOR_SUBSYSTEM_VERSION: WORD?
    val WIN32_VERSION_VALUE: DWORD?
    val SIZE_OF_IMAGE: DWORD?
    val SIZE_OF_HEADERS: DWORD?
    val CHECKSUM: DWORD?
    val SUBSYSTEM: Subsystem?
    val DLL_CHARACTERISTICS: DllCharacteristics?
    var SIZE_OF_STACK_RESERVE: ByteDefinition<*>? = null
    var SIZE_OF_STACK_COMMIT: ByteDefinition<*>? = null
    var SIZE_OF_HEAP_RESERVE: ByteDefinition<*>? = null
    var SIZE_OF_HEAP_COMMIT: ByteDefinition<*>? = null
    val LOADER_FLAGS: DWORD?
    val NUMBER_OF_RVA_AND_SIZES: RVA?
    val EXPORT_TABLE: ImageDataDir?
    val IMPORT_TABLE: ImageDataDir?
    val RESOURCE_TABLE: ImageDataDir?
    val EXCEPTION_TABLE: ImageDataDir?
    val CERTIFICATE_TABLE: ImageDataDir?
    val BASE_RELOCATION_TABLE: ImageDataDir?
    val DEBUG: ImageDataDir?
    val COPYRIGHT: ImageDataDir?
    val GLOBAL_PTR: ImageDataDir?
    val TLS_TABLE: ImageDataDir?
    val LOAD_CONFIG_TABLE: ImageDataDir?
    val BOUND_IMPORT: ImageDataDirExtra?
    val IAT: ImageDataDirExtra?
    val DELAY_IMPORT_DESCRIPTOR: ImageDataDirExtra?
    val CLR_RUNTIME_HEADER: ImageDataDirExtra?

    init {
        // the header length is variable.

        //
        // Standard fields.
        //
        h(HeaderDefinition("Standard fields"))
        bytes.mark()
        MAGIC_NUMBER = h(MagicNumber(bytes.readUShort(2), "magic number"))
        MAJOR_LINKER_VERSION = h(WORD(bytes.readUShort(1), "major linker version"))
        MINOR_LINKER_VERSION = h(WORD(bytes.readUShort(1), "minor linker version"))
        SIZE_OF_CODE = h(DWORD(bytes.readUInt(4), "size of code"))
        SIZE_OF_INIT_DATA = h(DWORD(bytes.readUInt(4), "size of initialized data"))
        SIZE_OF_UNINIT_DATA = h(DWORD(bytes.readUInt(4), "size of unitialized data"))
        ADDR_OF_ENTRY_POINT = h(DWORD(bytes.readUInt(4), "address of entry point"))
        BASE_OF_CODE = h(DWORD(bytes.readUInt(4), "address of base of code"))
        BASE_OF_DATA = h(DWORD(bytes.readUInt(4), "address of base of data"))
        IS_32_BIT = MAGIC_NUMBER.get() === MagicNumberType.PE32
        bytes.reset()
        if (IS_32_BIT) {
            bytes.skip(28)
        }
        else {
            bytes.skip(24)
        }


        //
        // Standard fields.
        //
        h(HeaderDefinition("Windows specific fields"))
        if (IS_32_BIT) {
            IMAGE_BASE = h(ImageBase(bytes.readUInt(4), "image base"))
        }
        else {
            IMAGE_BASE = h(ImageBase_Wide(bytes.readULong(8), "image base"))
        }
        SECTION_ALIGNMENT = h(DWORD(bytes.readUInt(4), "section alignment in bytes"))
        FILE_ALIGNMENT = h(DWORD(bytes.readUInt(4), "file alignment in bytes"))
        MAJOR_OS_VERSION = h(WORD(bytes.readUShort(2), "major operating system version"))
        MINOR_OS_VERSION = h(WORD(bytes.readUShort(2), "minor operating system version"))
        MAJOR_IMAGE_VERSION = h(WORD(bytes.readUShort(2), "major image version"))
        MINOR_IMAGE_VERSION = h(WORD(bytes.readUShort(2), "minor image version"))
        MAJOR_SUBSYSTEM_VERSION = h(WORD(bytes.readUShort(2), "major subsystem version"))
        MINOR_SUBSYSTEM_VERSION = h(WORD(bytes.readUShort(2), "minor subsystem version"))
        WIN32_VERSION_VALUE = h(DWORD(bytes.readUInt(4), "win32 version value (reserved, must be zero)"))
        SIZE_OF_IMAGE = h(DWORD(bytes.readUInt(4), "size of image in bytes"))
        SIZE_OF_HEADERS = h(DWORD(bytes.readUInt(4), "size of headers (MS DOS stub, PE header, and section headers)"))
        CHECKSUM = h(DWORD(bytes.readUInt(4), "checksum"))
        SUBSYSTEM = h(Subsystem(bytes.readUShort(2), "subsystem"))
        DLL_CHARACTERISTICS = h(DllCharacteristics(bytes.readUShort(2), "dll characteristics"))
        if (IS_32_BIT) {
            SIZE_OF_STACK_RESERVE = h(DWORD(bytes.readUInt(4), "size of stack reserve"))
            SIZE_OF_STACK_COMMIT = h(DWORD(bytes.readUInt(4), "size of stack commit"))
            SIZE_OF_HEAP_RESERVE = h(DWORD(bytes.readUInt(4), "size of heap reserve"))
            SIZE_OF_HEAP_COMMIT = h(DWORD(bytes.readUInt(4), "size of heap commit"))
        }
        else {
            SIZE_OF_STACK_RESERVE = h(DWORD_WIDE(bytes.readULong(8), "size of stack reserve"))
            SIZE_OF_STACK_COMMIT = h(DWORD_WIDE(bytes.readULong(8), "size of stack commit"))
            SIZE_OF_HEAP_RESERVE = h(DWORD_WIDE(bytes.readULong(8), "size of heap reserve"))
            SIZE_OF_HEAP_COMMIT = h(DWORD_WIDE(bytes.readULong(8), "size of heap commit"))
        }
        LOADER_FLAGS = h(DWORD(bytes.readUInt(4), "loader flags (reserved, must be zero)"))
        NUMBER_OF_RVA_AND_SIZES = h(RVA(bytes.readUInt(4), "number of rva and sizes"))
        bytes.reset()
        if (IS_32_BIT) {
            bytes.skip(96)
        }
        else {
            bytes.skip(112)
        }

        //
        // Data directories
        //
        h(HeaderDefinition("Data Directories"))
        EXPORT_TABLE = table(h(ImageDataDir(bytes, DirEntry.EXPORT)))
        IMPORT_TABLE = table(h(ImageDataDir(bytes, DirEntry.IMPORT)))
        RESOURCE_TABLE = table(h(ImageDataDir(bytes, DirEntry.RESOURCE)))
        EXCEPTION_TABLE = table(h(ImageDataDir(bytes, DirEntry.EXCEPTION)))
        CERTIFICATE_TABLE = table(h(ImageDataDir(bytes, DirEntry.SECURITY)))
        BASE_RELOCATION_TABLE = table(h(ImageDataDir(bytes, DirEntry.BASERELOC)))
        DEBUG = table(h(ImageDataDir(bytes, DirEntry.DEBUG)))
        COPYRIGHT = table(h(ImageDataDir(bytes, DirEntry.COPYRIGHT)))
        GLOBAL_PTR = table(h(ImageDataDir(bytes, DirEntry.GLOBALPTR)))
        TLS_TABLE = table(h(ImageDataDir(bytes, DirEntry.TLS)))
        LOAD_CONFIG_TABLE = table(h(ImageDataDir(bytes, DirEntry.LOAD_CONFIG)))
        BOUND_IMPORT = h(ImageDataDirExtra(bytes, "bound import"))
        IAT = h(ImageDataDirExtra(bytes, "IAT"))
        DELAY_IMPORT_DESCRIPTOR = h(ImageDataDirExtra(bytes, "delay import descriptor"))
        CLR_RUNTIME_HEADER = h(ImageDataDirExtra(bytes, "COM+ runtime header"))

        // reserved 8 bytes!!
        bytes.skip(8)
    }

    private fun <T : ImageDataDir> table(`object`: T): T {
        tables.add(`object`)
        return `object`
    }
}
