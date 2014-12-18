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

public enum MachineTypeType {

    NONE("", "No specified machine type"),
    IMAGE_FILE_MACHINE_UNKNOWN("0", "the contents of this field are assumed to be applicable for any machine type"),
    IMAGE_FILE_MACHINE_AM33("1d3", "Matsushita AM33"),
    IMAGE_FILE_MACHINE_AMD64("8664", "x64"),
    IMAGE_FILE_MACHINE_ARM("1c0", "ARM little endian"),
    IMAGE_FILE_MACHINE_ARMV7("1c4", "ARMv7 (or higher) Thumb mode only"),
    IMAGE_FILE_MACHINE_EBC("ebc", "EFI byte code"),
    IMAGE_FILE_MACHINE_I386("14c", "Intel 386 or later processors and compatible processors"),
    IMAGE_FILE_MACHINE_IA64("200", "Intel Itanium processor family"),
    IMAGE_FILE_MACHINE_M32R("9041", "Mitsubishi M32R little endian"),
    IMAGE_FILE_MACHINE_MIPS16("266", "MIPS16"),
    IMAGE_FILE_MACHINE_MIPSFPU("366", "MIPS with FPU"),
    IMAGE_FILE_MACHINE_MIPSFPU16("466", "MIPS16 with FPU"),
    IMAGE_FILE_MACHINE_POWERPC("1f0", "Power PC little endian"),
    IMAGE_FILE_MACHINE_POWERPCFP("1f1", "Power PC with floating point support"),
    IMAGE_FILE_MACHINE_R4000("166", "MIPS little endian"),
    IMAGE_FILE_MACHINE_SH3("1a2", "Hitachi SH3"),
    IMAGE_FILE_MACHINE_SH3DSP("1a3", "Hitachi SH3 DSP"),
    IMAGE_FILE_MACHINE_SH4("1a6", "Hitachi SH4"),
    IMAGE_FILE_MACHINE_SH5("1a8", "Hitachi SH5"),
    IMAGE_FILE_MACHINE_THUMB("1c2", "ARM or Thumb (\"interworking\")"),
    IMAGE_FILE_MACHINE_WCEMIPSV2("169", "MIPS little-endian WCE v2")
    ;

    private final String hexValue;
    private final String description;

    MachineTypeType(String hexValue, String description) {
        this.hexValue = hexValue;
        this.description = description;
    }

    public static MachineTypeType get(UShort value) {
        String key = value.toHexString();

        for (MachineTypeType mt : values()) {
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
