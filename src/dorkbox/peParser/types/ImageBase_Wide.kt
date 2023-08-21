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

import dorkbox.hex.toHexString
import dorkbox.os.OS
import dorkbox.peParser.misc.ImageBaseType

class ImageBase_Wide(private val value: ULong, descriptiveName: String) : ByteDefinition<ULong>(descriptiveName) {
    override fun get(): ULong {
        return value
    }

    override fun format(b: StringBuilder) {
        val imageBase = ImageBaseType.get(value.toUInt())

        b.append(descriptiveName).append(": ").append(value).append(" (0x").append(value.toHexString()).append(") (")
        if (imageBase != null) {
            b.append(imageBase.description)
        }
        else {
            b.append("no image base default")
        }
        b.append(")").append(OS.LINE_SEPARATOR)
    }
}
