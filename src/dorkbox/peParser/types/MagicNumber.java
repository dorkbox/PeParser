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

import dorkbox.bytes.UShort;
import dorkbox.os.OS;
import dorkbox.peParser.misc.MagicNumberType;

public class MagicNumber extends ByteDefinition<MagicNumberType> {

    private final UShort value;

    public MagicNumber(UShort value, String descriptiveName) {
        super(descriptiveName);
        this.value = value;
    }

    @Override
    public final MagicNumberType get() {
        return MagicNumberType.get(this.value);
    }

    @Override
    public final void format(StringBuilder b) {
        b.append(getDescriptiveName()).append(": ")
         .append(this.value).append(" --> ").append(get().getDescription())
         .append(OS.LINE_SEPARATOR);
    }
}
