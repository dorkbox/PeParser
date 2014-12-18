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
package dorkbox.util.pe;

import java.io.ByteArrayInputStream;

import dorkbox.util.OS;
import dorkbox.util.bytes.LittleEndian;

public class ByteArray extends ByteArrayInputStream {
    /** size of the headers, when done reading */
//    public int pos = 0;

    public ByteArray(byte[] bytes) {
        super(bytes);
    }

    public String readAsciiString(int length) {
        return new String(copyBytes(length), OS.US_ASCII).trim();
    }

    public long readULong(int length) {
        int result = LittleEndian.ULong_.fromBytes(this.buf, this.pos, length);
        this.pos += length;
        return result;
    }

    public int readUInt(int length) {
        int result = LittleEndian.UInt_.fromBytes(this.buf, this.pos, length);
        this.pos += length;
        return result;
    }

    public short readUShort(int length) {
        short result = LittleEndian.UShort_.fromBytes(this.buf, this.pos, length);
        this.pos += length;
        return result;
    }

    public byte readUByte() {
        byte b = (byte) (this.buf[this.pos] & 0xFF);
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

    public void seek(long position) {
        this.pos = (int) position;
    }

    public int position() {
        return this.pos;
    }

    public int marked() {
        return this.mark;
    }
}
