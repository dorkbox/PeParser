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

import dorkbox.peParser.misc.SubsystemType;
import dorkbox.util.OS;
import dorkbox.util.bytes.UShort;

public class Subsystem extends ByteDefinition<SubsystemType> {

    private final UShort value;

    public Subsystem(UShort value, String descriptiveName) {
        super(descriptiveName);
        this.value = value;
    }

    @Override
    public final SubsystemType get() {
        return SubsystemType.get(this.value);
    }

    @Override
    public void format(StringBuilder b) {
        SubsystemType s = get();

        if (s != null) {
            b.append(getDescriptiveName()).append(": ").append(s.getDescription()).append(OS.LINE_SEPARATOR);
        } else {
            b.append("ERROR, no subsystem description for value: ").append(this.value).append(OS.LINE_SEPARATOR);
        }
    }
}
