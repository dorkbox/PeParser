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

import dorkbox.os.OS

class HeaderDefinition(descriptiveName: String) : ByteDefinition<String>(descriptiveName) {
    override fun get(): String {
        return descriptiveName
    }

    override fun format(b: StringBuilder) {
        b.append(OS.LINE_SEPARATOR)
            .append(get())
            .append(OS.LINE_SEPARATOR)
            .append(".......................")
            .append(OS.LINE_SEPARATOR)
            .append(OS.LINE_SEPARATOR)
    }
}
