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

package dorkbox.peParser

import dorkbox.os.OS
import dorkbox.peParser.headers.*
import dorkbox.peParser.headers.resources.ResourceDataEntry
import dorkbox.peParser.headers.resources.ResourceDirectoryEntry
import dorkbox.peParser.headers.resources.ResourceDirectoryHeader
import dorkbox.peParser.misc.DirEntry
import dorkbox.updates.Updates.add
import java.io.*
import java.util.*

class PE {
    companion object {
        // info from:
        // http://evilzone.org/tutorials/(paper)-portable-executable-format-and-its-rsrc-section/
        // http://www.skynet.ie/~caolan/pub/winresdump/winresdump/doc/pefile.html  (older version of the doc...)
        // http://www.csn.ul.ie/~caolan/pub/winresdump/winresdump/doc/pefile2.html
        // http://msdn.microsoft.com/en-us/library/ms809762.aspx

        /**
         * Gets the version number.
         */
        val version = "3.3"
        private const val PE_OFFSET_LOCATION = 0x3c
        private val PE_SIG = "PE\u0000\u0000".toByteArray()

        init {
            // Add this project to the updates system, which verifies this class + UUID + version information
            add(PE::class.java, "5f5fafe156ba4e8f94c28f0c283aa509", version)
        }

        @Throws(Exception::class)
        fun getVersion(executablePath: String): String? {
            val pe = PE(executablePath)
            if (pe.invalidFile) {
                throw Exception("No version found:$executablePath")
            }
            for (mainEntry in pe.optionalHeader!!.tables) {
                if (mainEntry.type === DirEntry.RESOURCE) {
                    val root = mainEntry.data as ResourceDirectoryHeader?
                    for (rootEntry in root!!.entries) {
                        if ("Version" == rootEntry!!.NAME!!.get()) {
                            val versionInfoData = rootEntry.directory!!.entries[0]!!.directory!!.entries[0]!!.resourceDataEntry!!.getData(
                                pe.fileBytes!!
                            )
                            val fileVersionIndex = indexOf(versionInfoData, includeNulls("FileVersion")) + 26
                            val fileVersionEndIndex = indexOf(versionInfoData, byteArrayOf(0x00, 0x00), fileVersionIndex)
                            return removeNulls(String(versionInfoData, fileVersionIndex, fileVersionEndIndex - fileVersionIndex))
                        }
                    }
                }
            }
            throw Exception("No version found:$executablePath")
        }

        private fun includeNulls(str: String): kotlin.ByteArray {
            val chars = str.toCharArray()
            val result = ByteArray(chars.size * 2)
            var i = 0
            var j = 0
            while (i < result.size) {
                result[i] = chars[j].code.toByte()
                i += 2
                j++
            }
            return result
        }

        private fun removeNulls(str: String?): String? {
            return str?.replace("\\x00".toRegex(), "")
        }

        fun indexOf(outerArray: kotlin.ByteArray, smallerArray: kotlin.ByteArray, begin: Int = 0): Int {
            for (i in begin until outerArray.size - smallerArray.size + 1) {
                var found = true
                for (j in smallerArray.indices) {
                    if (outerArray[i + j] != smallerArray[j]) {
                        found = false
                        break
                    }
                }
                if (found) {
                    return i
                }
            }
            return -1
        }
    }

    // TODO: should use an input stream to load header info, instead of the entire thing!
    var fileBytes: ByteArray? = null
    private var coffHeader: COFFFileHeader? = null
    var optionalHeader: OptionalHeader? = null
    private var sectionTable: SectionTable? = null
    private var invalidFile = false

    constructor(fileName: String) {
        val file = File(fileName)
        try {
            val fileInputStream = FileInputStream(file)
            fromInputStream(fileInputStream)
        }
        catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        catch (e: IOException) {
            e.printStackTrace()
        }
    }

    constructor(inputStream: InputStream) {
        try {
            fromInputStream(inputStream)
        }
        catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(FileNotFoundException::class, IOException::class)
    private fun fromInputStream(inputStream: InputStream) {
        val baos = ByteArrayOutputStream(8192)
        val buffer = ByteArray(4096)

        var read: Int
        while (inputStream.read(buffer).also { read = it } > 0) {
            baos.write(buffer, 0, read)
        }

        baos.flush()
        inputStream.close()

        val bytes = baos.toByteArray()
        invalidFile = bytes.size == 0
        fileBytes = ByteArray(bytes)

        // initialize header info
        if (isPE) {
            val offset = PEOffset + PE_SIG.size
            fileBytes!!.seek(offset)
            coffHeader = COFFFileHeader(fileBytes!!)
            optionalHeader = OptionalHeader(fileBytes!!)
            val numberOfEntries: Int = coffHeader!!.NumberOfSections!!.get().toInt()
            sectionTable = SectionTable(fileBytes!!, numberOfEntries)

            // now the bytes are positioned at the start of the section table. ALl other info MUST be done relative to byte offsets/locations!

            // fixup directory names -> table names (from spec!)
            for (section in sectionTable!!.sections) {
                val sectionAddress: Long = section.VIRTUAL_ADDRESS.get().toLong()
                val sectionSize: Long = section.SIZE_OF_RAW_DATA.get().toLong()
                for (entry in optionalHeader!!.tables) {
                    val optionAddress: Long = entry.get().toLong()
                    if (sectionAddress <= optionAddress && sectionAddress + sectionSize > optionAddress) {
                        entry.section = section
                        break
                    }
                }
            }

            // fixup directories
            for (entry in optionalHeader!!.tables) {
                if (entry.type === DirEntry.RESOURCE) {
                    // fixup resources
                    val section = entry.section
                    if (section != null) {
                        val delta: Long = section.VIRTUAL_ADDRESS.get().toLong() - section.POINTER_TO_RAW_DATA.get().toLong()
                        val offsetInFile: Long = entry.get().toLong() - delta
                        if (offsetInFile > Int.MAX_VALUE) {
                            throw RuntimeException("Unable to set offset to more than 2gb!")
                        }
                        fileBytes!!.seek(offsetInFile.toInt())
                        fileBytes!!.mark() // resource data is offset from the beginning of the header!
                        val root: Header = ResourceDirectoryHeader(fileBytes!!, section, 0)
                        entry.data = root
                    }
                }
            }
        }
    }

    val info: String
        get() = if (isPE) {
            val b = StringBuilder()
            b.append("PE signature offset: ").append(PEOffset).append(OS.LINE_SEPARATOR).append("PE signature correct: ").append("yes")
                .append(OS.LINE_SEPARATOR).append(OS.LINE_SEPARATOR).append("----------------").append(OS.LINE_SEPARATOR).append("COFF header info")
                .append(OS.LINE_SEPARATOR).append("----------------").append(OS.LINE_SEPARATOR)
            for (bd in coffHeader!!.headers) {
                bd.format(b)
            }
            b.append(OS.LINE_SEPARATOR)
            b.append("--------------------").append(OS.LINE_SEPARATOR).append("Optional header info").append(OS.LINE_SEPARATOR)
                .append("--------------------").append(OS.LINE_SEPARATOR)
            for (bd in optionalHeader!!.headers) {
                bd.format(b)
            }
            b.append(OS.LINE_SEPARATOR)
            b.append(OS.LINE_SEPARATOR).append("-------------").append(OS.LINE_SEPARATOR).append("Section Table").append(OS.LINE_SEPARATOR)
                .append("-------------").append(OS.LINE_SEPARATOR).append(OS.LINE_SEPARATOR)
            for (section in sectionTable!!.sections) {
                for (bd in section.headers) {
                    bd.format(b)
                }
            }
            b.append(OS.LINE_SEPARATOR)
            b.toString()
        }
        else {
            "PE signature not found. The given file is not a PE file. $OS.LINE_SEPARATOR"
        }
    private val PEOffset: Int
        get() {
            fileBytes!!.mark()
            fileBytes!!.seek(PE_OFFSET_LOCATION)
            val read: Int = fileBytes!!.readUShort(2).toInt()
            fileBytes!!.reset()
            return read
        }
    val isPE: Boolean
        get() {
            if (invalidFile) {
                return false
            }
            var saved = -1
            return try {
                // this can screw up if the length of the file is invalid...
                val offset = PEOffset
                saved = fileBytes!!.position()

                // always have to start from zero if we ask this.
                fileBytes!!.seek(0)
                for (i in PE_SIG.indices) {
                    if (fileBytes!!.readRaw(offset + i) != PE_SIG[i]) {
                        return false
                    }
                }
                true
            }
            catch (e: Exception) {
                false
            }
            finally {
                if (saved != -1) {
                    fileBytes!!.seek(saved)
                }
            }
        }
    val largestResourceAsStream: ByteArrayInputStream?
        get() {
            for (mainEntry in optionalHeader!!.tables) {
                if (mainEntry.type === DirEntry.RESOURCE) {
                    val directoryEntries = LinkedList<ResourceDirectoryEntry?>()
                    val resourceEntries = LinkedList<ResourceDirectoryEntry?>()
                    val root = mainEntry.data as ResourceDirectoryHeader?

                    for (rootEntry in root!!.entries) {
                        collect(directoryEntries, resourceEntries, rootEntry)
                        directoryEntries.add(rootEntry)
                    }

                    var entry: ResourceDirectoryEntry?
                    while (directoryEntries.poll().also { entry = it } != null) {
                        collect(directoryEntries, resourceEntries, entry)
                    }

                    var largest: ResourceDataEntry? = null
                    for (resourceEntry in resourceEntries) {
                        val dataEntry = resourceEntry!!.resourceDataEntry
                        if (largest == null || largest.SIZE.get().toLong() < dataEntry!!.SIZE.get().toLong()) {
                            largest = dataEntry
                        }
                    }

                    // now return our resource, but it has to be wrapped in a new stream!
                    return ByteArrayInputStream(largest!!.getData(fileBytes!!))
                }
            }
            return null
        }

    private fun collect(
        directoryEntries: LinkedList<ResourceDirectoryEntry?>,
        resourceEntries: LinkedList<ResourceDirectoryEntry?>,
        entry: ResourceDirectoryEntry?
    ) {
        if (entry!!.isDirectory) {
            for (dirEntry in entry.directory!!.entries) {
                directoryEntries.add(dirEntry)
            }
        }
        else {
            resourceEntries.add(entry)
        }
    }


}
