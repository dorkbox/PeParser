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
package dorkbox.peParser

import dorkbox.bytes.LittleEndian
import java.io.ByteArrayInputStream
import kotlin.ByteArray
import kotlin.text.Charsets.US_ASCII

class ByteArray(bytes: ByteArray?) : ByteArrayInputStream(bytes) {
    fun readAsciiString(length: Int): String {
        // pos is incremented by the copy bytes method
        return String(copyBytes(length), US_ASCII).trim { it <= ' ' }
    }

    fun readULong(length: Int): ULong {
        val result: ULong = LittleEndian.ULong_.from(buf, pos, length)
        pos += length
        return result
    }

    fun readUInt(length: Int): UInt {
        val result: UInt = LittleEndian.UInt_.from(buf, pos, length)
        pos += length
        return result
    }

    fun readUShort(length: Int): UShort {
        val result: UShort = LittleEndian.UShort_.from(buf, pos, length)
        pos += length
        return result
    }

    fun readUByte(): UByte {
        val b: UByte = buf[pos].toUByte()
        pos++
        return b
    }

    fun readRaw(offset: Int): Byte {
        return buf[pos + offset]
    }

    fun copyBytes(length: Int): ByteArray {
        val data = ByteArray(length)
        super.read(data, 0, length)
        return data
    }

    fun mark() {
        super.mark(0)
    }

    fun seek(position: Int) {
        pos = position
    }

    fun position(): Int {
        return pos
    }

    fun marked(): Int {
        return mark
    }
}
