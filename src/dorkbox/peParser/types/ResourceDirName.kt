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
package dorkbox.peParser.types

import dorkbox.hex.toHexString
import dorkbox.os.OS
import dorkbox.peParser.misc.ResourceTypes
import java.nio.charset.StandardCharsets

class ResourceDirName(intValue: UInt, descriptiveName: String, bytes: dorkbox.peParser.ByteArray, private val level: Int) :
    ByteDefinition<String>(descriptiveName) {

    private var value: String? = null

    init {

        /*
        * This field contains either an integer ID or a pointer to a structure that contains a string name.
        *
        * If the high bit (0x80000000) is zero, this field is interpreted as an integer ID.
        *
        * If the high bit is nonzero, the lower 31 bits are an offset (relative to the start of the resources) to an
        * IMAGE_RESOURCE_DIR_STRING_U structure. This structure contains a WORD character count, followed by a UNICODE
        * string with the resource name.
        *
        * Yes, even PE files intended for non-UNICODE Win32 implementations use UNICODE here. To convert the UNICODE
        * string to an ANSI string, use the WideCharToMultiByte function.
        */
        val valueInt: Long = intValue.toLong()

        // now process the name
        val isString = 0L != valueInt and NAME_IS_STRING_MASK.toLong()
        if (isString) {
            val savedPosition = bytes.position()
            //
            // High bit is 1
            //
            val offset = valueInt and NAME_OFFSET_MASK.toLong()
            if (offset > Int.MAX_VALUE.toLong()) {
                throw RuntimeException("Unable to set offset to more than 2gb!")
            }

            // offset from the start of the resource data to the name string of this particular resource.
            bytes.seek(bytes.marked() + offset.toInt())
            val length: Int = bytes.readUShort(2).toInt()
            val buff = ByteArray(length * 2) // UTF-8 chars are 16 bits = 2
            // bytes
            for (i in buff.indices) {
                buff[i] = bytes.readUByte().toByte()
            }

            // go back
            bytes.seek(savedPosition)
            value = String(buff, StandardCharsets.UTF_16LE).trim { it <= ' ' }
        }
        else {
            //
            // High bit is 0
            //

            // if it's NOT a STRING, then we do additional lookups.

            // determine what "name" means
            when (level) {
                1    -> value = ResourceTypes.get(intValue)?.detailedInfo
                2    -> value = intValue.toHexString()
                3    -> value = intValue.toHexString()
                else -> value = intValue.toHexString()
            }
        }
    }

    override fun get(): String {
        return value!!
    }

    override fun format(b: StringBuilder) {
        b.append(descriptiveName).append(": ")
        when (level) {
            1    -> {}
            2    -> b.append("name: ")
            3    -> b.append("Language: ")
            else -> b.append("??: ")
        }
        b.append(value).append(OS.LINE_SEPARATOR)
    }

    companion object {
        private const val NAME_IS_STRING_MASK = -0x80000000
        private const val NAME_OFFSET_MASK = 0x7FFFFFFF
    }
}
