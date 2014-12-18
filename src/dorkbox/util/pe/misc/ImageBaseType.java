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
package dorkbox.util.pe.misc;

import dorkbox.util.bytes.UInteger;

public enum ImageBaseType {
    IMAGE_BASE_DEFAULT(0x10000000L, "DLL default"),
    IMAGE_BASE_WIN_CE(0x00010000L, "default for Windows CE EXEs"),
    IMAGE_BASE_WIN(0x00400000L, "default for Windows NT, 2000, XP, 95, 98 and Me"),
    ;

    private final long value;
    private final String description;

    ImageBaseType(long value, String description) {
        this.value = value;
        this.description = description;
    }

    public static ImageBaseType get(UInteger key) {
        long keyAsLong = key.longValue();

        for (ImageBaseType c : values()) {
            if (keyAsLong == c.value) {
                return c;
            }
        }

        return null;
    }

    public String getDescription() {
        return this.description;
    }
}
