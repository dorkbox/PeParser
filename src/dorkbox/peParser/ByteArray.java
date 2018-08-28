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
package dorkbox.peParser;

import java.io.ByteArrayInputStream;

import dorkbox.util.OS;
import dorkbox.util.bytes.LittleEndian;
import dorkbox.util.bytes.UByte;
import dorkbox.util.bytes.UInteger;
import dorkbox.util.bytes.ULong;
import dorkbox.util.bytes.UShort;

public class ByteArray extends ByteArrayInputStream {

    public ByteArray(byte[] bytes) {
        super(bytes);
    }

    public String readAsciiString(int length) {
        // pos is incremented by the copybytes method
        return new String(copyBytes(length), OS.US_ASCII).trim();
    }

    public ULong readULong(int length) {
        ULong result = LittleEndian.ULong_.from(this.buf, this.pos, length);
        this.pos += length;
        return result;
    }

    public UInteger readUInt(int length) {
        UInteger result = LittleEndian.UInt_.from(this.buf, this.pos, length);
        this.pos += length;
        return result;
    }

    public UShort readUShort(int length) {
        UShort result = LittleEndian.UShort_.from(this.buf, this.pos, length);
        this.pos += length;
        return result;
    }

    public UByte readUByte() {
        UByte b = UByte.valueOf(this.buf[this.pos]);
        this.pos++;
        return b;
    }

    public byte readRaw(int offset) {
        return this.buf[this.pos + offset];
    }

    public byte[] copyBytes(int length) {
        byte[] data = new byte[length];
        super.read(data, 0, length);
        return data;
    }

    public void mark() {
        super.mark(0);
    }

    public void seek(int position) {
        this.pos = position;
    }

    public int position() {
        return this.pos;
    }

    public int marked() {
        return this.mark;
    }
}
