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

import dorkbox.util.bytes.UShort;

public enum MagicNumberType {
    NONE("", "ERROR, unable to recognize magic number"),
    PE32("10B", "PE32, normal executable file"),
    PE32_PLUS("20B", "PE32+ executable"),
    ROM("107", "ROM image"),
    ;

    private final String hexValue;
    private final String description;

    MagicNumberType(String hexValue, String description) {
        this.hexValue = hexValue.toLowerCase();
        this.description = description;
    }

    public static MagicNumberType get(UShort value) {
        String key = value.toHexString();

        for (MagicNumberType mt : values()) {
            if (key.equals(mt.hexValue)) {
                return mt;
            }
        }

        return NONE;
    }

    public String getDescription() {
        return this.description;
    }
}
