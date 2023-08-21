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
package dorkbox.peParser.types

import dorkbox.os.OS
import dorkbox.peParser.ByteArray
import kotlin.text.Charsets.US_ASCII

class AsciiString(bytes: ByteArray, byteLength: Int, descriptiveName: String) : ByteDefinition<String>(descriptiveName) {
    private val value: String

    init {
        val stringBytes = bytes.copyBytes(byteLength)
        value = String(stringBytes, US_ASCII).trim { it <= ' ' }
    }

    override fun get(): String {
        return value
    }

    override fun format(b: StringBuilder) {
        b.append(descriptiveName).append(": ").append(value).append(OS.LINE_SEPARATOR)
    }
}
