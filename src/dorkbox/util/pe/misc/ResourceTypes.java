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

public enum ResourceTypes {
    N_1("???_0"),
    CURSOR("Cursor"),
    BITMAP("Bitmap"),
    ICON("Icon"),
    MENU("Menu"),
    DLG_BOX("Fialog Box"),
    STRING_TABLE_ENTRY("String"),
    FONT_DIR("Font Directory"),
    FONT("Font"),
    ACCEL_TABLE("Accelerators"),
    RAW_DATA("application defined resource (raw data)"),
    MESSAGE_TABLE_ENTRY("Message entry"),
    GROUP_CURSOR("Group Cursor"),
    N_13("???_13"),
    GROUP_ICON("Group Icon"),
    N_15("???_15"),
    VER_INFO("Version"),
    DLG_INCLUDE("dlginclude"),
    N_18("???_18"),
    PNP_RESOURCE("Plug and Play Resource"),
    VXD("VXD"),
    ANIM_CURSOR("Animated Cursor"),
    ANIM_ICON("Animated Icon"),
    HTML("HTML"),
    MANIFEST("Manifest"),
    ;

    private final String detailedInfo;

    ResourceTypes(String detailedInfo) {
        this.detailedInfo = detailedInfo;
    }

    public String getDetailedInfo() {
        return this.detailedInfo;
    }

    public static ResourceTypes get(UInteger valueInt) {
        int valueAsInt = valueInt.intValue();

        for (ResourceTypes t : values()) {
            if (valueAsInt == t.ordinal()) {
                return t;
            }
        }

        return null;
    }
}
