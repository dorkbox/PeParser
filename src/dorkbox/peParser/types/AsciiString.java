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
import dorkbox.peParser.ByteArray;

public class AsciiString extends ByteDefinition<String> {

    private final String value;

    public AsciiString(ByteArray bytes, int byteLength, String descriptiveName) {
        super(descriptiveName);

        byte[] stringBytes = bytes.copyBytes(byteLength);
        this.value = new String(stringBytes, OS.US_ASCII).trim();
    }

    @Override
    public final String get() {
        return this.value;
    }

    @Override
    public void format(StringBuilder b) {
        b.append(getDescriptiveName()).append(": ")
         .append(this.value)
         .append(OS.LINE_SEPARATOR);
    }
}
