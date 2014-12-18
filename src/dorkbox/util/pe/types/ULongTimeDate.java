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

import java.util.Date;

import dorkbox.util.OS;

public class ULongTimeDate extends ByteDefinition<Date> {

    private final int value;

    public ULongTimeDate(int value, String descriptiveName) {
        super(descriptiveName);
        this.value = value;
    }

    @Override
    public final Date get() {
        long millis = (long) this.value * 1000;
        return new Date(millis);
    }

    @Override
    public void format(StringBuilder b) {
        b.append(getDescriptiveName()).append(": ").append(get().toString()).append(OS.LINE_SEPARATOR);
    }
}
