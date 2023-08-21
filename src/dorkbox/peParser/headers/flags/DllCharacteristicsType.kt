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
package dorkbox.peParser.headers.flags

enum class DllCharacteristicsType(private val hexValue: String, val description: String) {
    IMAGE_DLL_CHARACTERISTICS_DYNAMIC_BASE("40", "DLL can be relocated at load time."),
    IMAGE_DLL_CHARACTERISTICS_FORCE_INTEGRITY("80", "Code Integrity checks are enforced."),
    IMAGE_DLL_CHARACTERISTICS_NX_COMPAT("100", "Image is NX compatible."),
    IMAGE_DLL_CHARACTERISTICS_ISOLATION("200", "Isolation aware, but do not isolate the image."),
    IMAGE_DLLCHARACTERISTICS_NO_SEH("400", "Does not use structured exception (SE) handling. No SE handler may be called in this image."),
    IMAGE_DLLCHARACTERISTICS_NO_BIND("800", "Do not bind the image."),
    IMAGE_DLLCHARACTERISTICS_WDM_DRIVER("2000", "A WDM driver."),
    IMAGE_DLLCHARACTERISTICS_TERMINAL_SERVER_AWARE("8000", "Terminal Server aware.");

    companion object {
        operator fun get(key: UShort): Array<DllCharacteristicsType> {
            val chars: MutableList<DllCharacteristicsType> = ArrayList(0)
            val keyAsInt: Int = key.toInt()
            for (c in values()) {
                val mask = c.hexValue.toLong(16)
                if (keyAsInt.toLong() and mask != 0L) {
                    chars.add(c)
                }
            }
            return chars.toTypedArray<DllCharacteristicsType>()
        }
    }
}
