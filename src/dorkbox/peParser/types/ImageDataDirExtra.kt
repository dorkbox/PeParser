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
import dorkbox.peParser.ByteArray

class ImageDataDirExtra(bytes: ByteArray, description: String) : ByteDefinition<UInt>(description) {
    private val virtualAddress: TInteger
    private val size: TInteger

    /** 8 bytes each  */
    init {
        virtualAddress = TInteger(bytes.readUInt(4), "Virtual Address")
        size = TInteger(bytes.readUInt(4), "Size")
    }

    override fun get(): UInt {
        return virtualAddress.get()
    }

    fun getSize(): UInt {
        return size.get()
    }

    override fun format(b: StringBuilder) {
        b.append(descriptiveName).append(": ").append(OS.LINE_SEPARATOR)
            .append("\t").append("address: ").append(virtualAddress).append(" (").append(virtualAddress.get().toHexString()).append(")").append(OS.LINE_SEPARATOR)
            .append("\t").append("size: ").append(size.get()).append(" (").append(size.get().toHexString()).append(")").append(OS.LINE_SEPARATOR)
    }
}
