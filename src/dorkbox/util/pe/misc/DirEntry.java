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
package dorkbox.util.pe.misc;

public enum DirEntry {

    EXPORT("Export Directory"),
    IMPORT("Import Directory"),
    RESOURCE("Resource Directory"),
    EXCEPTION("Exception Directory"),
    SECURITY("Security Directory"),
    BASERELOC("Base Relocation Table"),
    DEBUG("Debug Directory"),
    COPYRIGHT("Description String"),
    GLOBALPTR("Machine Value (MIPS GP)"),
    TLS("TLS Directory"),
    LOAD_CONFIG("Load Configuration Directory"),
    ;

    private final String description;

    DirEntry(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
