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
package dorkbox.peParser.types;

import dorkbox.util.OS;
import dorkbox.util.bytes.UInteger;
import dorkbox.util.bytes.ULong;
import dorkbox.peParser.misc.ImageBaseType;

public class ImageBase_Wide extends ByteDefinition<ULong> {

    private final ULong value;

    public ImageBase_Wide(ULong value, String descriptiveName) {
        super(descriptiveName);
        this.value = value;
    }

    @Override
    public final ULong get() {
        return this.value;
    }

    @Override
    public void format(StringBuilder b) {
        ImageBaseType imageBase = ImageBaseType.get(UInteger.valueOf(this.value.longValue()));

        b.append(getDescriptiveName()).append(": ")
         .append(this.value).append(" (0x").append(this.value.toHexString()).append(") (");

        if (imageBase != null) {
            b.append(imageBase.getDescription());
        } else {
            b.append("no image base default");
        }
        b.append(")").append(OS.LINE_SEPARATOR);
    }
}
