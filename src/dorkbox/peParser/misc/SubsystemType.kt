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
package dorkbox.peParser.misc

enum class SubsystemType(private val intValue: Int, val description: String) {
    IMAGE_SYSTEM_UNKNOWN(0, "unknown subsystem"),
    IMAGE_SUBSYSTEM_NATIVE(1,"Device drivers and native Windows processes"),
    IMAGE_SUBSYSTEM_WINDOWS_GUI(2, "The Windows graphical user interface (GUI) subsystem"),
    IMAGE_SUBSYSTEM_WINDOWS_CUI(3,"The Windows character subsystem"),
    IMAGE_SUBSYSTEM_POSIX_CUI(7, "The Posix character subsystem"),
    IMAGE_SUBSYSTEM_WINDOWS_CE_GUI(9,"Windows CE"),
    IMAGE_SUBSYSTEM_EFI_APPLICATION(10, "An Extensible Firmware Interface (EFI) application"),
    IMAGE_SUBSYSTEM_EFI_BOOT_SERVICE_DRIVER(11,"An EFI driver with boot services"),
    IMAGE_SUBSYSTEM_EFI_RUNTIME_DRIVER(12, "An EFI driver with run-time services"),
    IMAGE_SUBSYSTEM_EFI_ROM(13,"An EFI ROM image"),
    IMAGE_SUBSYSTEM_XBOX(14, "XBOX");

    companion object {
        operator fun get(value: UShort): SubsystemType? {
            val valueAsInt: Int = value.toInt()
            for (c in values()) {
                if (c.intValue == valueAsInt) {
                    return c
                }
            }
            return null
        }
    }
}
