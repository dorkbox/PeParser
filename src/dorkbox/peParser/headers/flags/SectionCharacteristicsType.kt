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
package dorkbox.peParser.headers.flags

enum class SectionCharacteristicsType(private val hexValue: String, val description: String) {
    IMAGE_SCN_TYPE_NO_PAD("8", "The section should not be padded to the next boundary. DEPRECATED"),
    IMAGE_SCN_CNT_CODE("20", "The section contains executable code."),
    IMAGE_SCN_CNT_INITIALIZED_DATA("40", "The section contains initialized data."),
    IMAGE_SCN_CNT_UNINITIALIZED_DATA("80", "The section contains uninitialized data."),
    IMAGE_SCN_LNK_INFO("200", "The section contains comments or other information. Valid for object files only."),
    IMAGE_SCN_LNK_REMOVE("800", "The section will not become part of the image. Valid for object files only."),
    IMAGE_SCN_LNK_COMDAT("1000", "The section contains COMDAT data."),
    IMAGE_SCN_GPREL("8000", "The section contains data referenced through the global pointer (GP)."),
    IMAGE_SCN_MEM_16BIT("20000", "For ARM machine types, the section contains Thumb code."),
    IMAGE_SCN_ALIGN_1BYTES("100000", "Align data on a 1-byte boundary. Valid only for object files."),
    IMAGE_SCN_ALIGN_2BYTES("200000", "Align data on a 2-byte boundary. Valid only for object files."),
    IMAGE_SCN_ALIGN_4BYTES("300000", "Align data on a 4-byte boundary. Valid only for object files."),
    IMAGE_SCN_ALIGN_8BYTES("400000",  "Align data on a 8-byte boundary. Valid only for object files."),
    IMAGE_SCN_ALIGN_16BYTES("500000", "Align data on a 16-byte boundary. Valid only for object files."),
    IMAGE_SCN_ALIGN_32BYTES("600000", "Align data on a 32-byte boundary. Valid only for object files."),
    IMAGE_SCN_ALIGN_64BYTES("700000", "Align data on a 64-byte boundary. Valid only for object files."),
    IMAGE_SCN_ALIGN_128BYTES("800000", "Align data on a 128-byte boundary. Valid only for object files."),
    IMAGE_SCN_ALIGN_256BYTES("900000", "Align data on a 256-byte boundary. Valid only for object files."),
    IMAGE_SCN_ALIGN_512BYTES("A00000", "Align data on a 512-byte boundary. Valid only for object files."),
    IMAGE_SCN_ALIGN_1024BYTES("B00000", "Align data on a 1024-byte boundary. Valid only for object files."),
    IMAGE_SCN_ALIGN_2048BYTES("C00000", "Align data on a 2048-byte boundary. Valid only for object files."),
    IMAGE_SCN_ALIGN_4096BYTES("D00000", "Align data on a 4096-byte boundary. Valid only for object files."),
    IMAGE_SCN_ALIGN_8192BYTES("E00000", "Align data on a 8192-byte boundary. Valid only for object files."),
    IMAGE_SCN_LNK_NRELOC_OVFL("1000000", "The section contains extended relocations."),
    IMAGE_SCN_MEM_DISCARDABLE("2000000", "The section can be discarded as needed."),
    IMAGE_SCN_MEM_NOT_CACHED("4000000", "The section cannot be cached."),
    IMAGE_SCN_MEM_NOT_PAGED("8000000", "The section is not pageable."),
    IMAGE_SCN_MEM_SHARED("10000000", "The section can be shared in memory."),
    IMAGE_SCN_MEM_EXECUTE("20000000", "The section can be executed as code."),
    IMAGE_SCN_MEM_READ("80000000", "The section can be read."),
    IMAGE_SCN_MEM_WRITE("80000000", "The section can be written to.");

    companion object {
        operator fun get(key: UInt): Array<SectionCharacteristicsType> {
            val chars: MutableList<SectionCharacteristicsType> = ArrayList(0)
            val keyAsLong: Long = key.toLong()
            for (c in values()) {
                val mask = c.hexValue.toLong(16)
                if (keyAsLong and mask != 0L) {
                    chars.add(c)
                }
            }
            return chars.toTypedArray<SectionCharacteristicsType>()
        }
    }
}
