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
package dorkbox.util.pe.types;

import java.util.Arrays;

import dorkbox.util.OS;
import dorkbox.util.bytes.LittleEndian;

public class UChar extends ByteDefinition<Character> {

    private final char value;

    public UChar(byte[] headerBytes, int byteStart, int byteLength, String descriptiveName) {
        super(descriptiveName);

        byte[] bytes = Arrays.copyOfRange(headerBytes, byteStart, byteStart + byteLength);
        this.value = LittleEndian.UChar_.fromBytes(bytes);
    }

    @Override
    public final Character get() {
        return this.value;
    }

    @Override
    public final void format(StringBuilder b) {
        b.append(getDescriptiveName()).append(": ")
         .append(this.value)
         .append(OS.LINE_SEPARATOR);
    }
}
