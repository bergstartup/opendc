/*
 * Copyright (c) 2021 AtLarge Research
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.opendc.trace.gwf

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.opendc.trace.*
import java.net.URL
import java.time.Duration
import java.time.Instant

/**
 * Test suite for the [GwfTraceFormat] class.
 */
internal class GwfTraceFormatTest {
    @Test
    fun testTraceExists() {
        val input = checkNotNull(GwfTraceFormatTest::class.java.getResource("/trace.gwf"))
        val format = GwfTraceFormat()
        assertDoesNotThrow {
            format.open(input)
        }
    }

    @Test
    fun testTraceDoesNotExists() {
        val input = checkNotNull(GwfTraceFormatTest::class.java.getResource("/trace.gwf"))
        val format = GwfTraceFormat()
        assertThrows<IllegalArgumentException> {
            format.open(URL(input.toString() + "help"))
        }
    }

    @Test
    fun testTables() {
        val input = checkNotNull(GwfTraceFormatTest::class.java.getResource("/trace.gwf"))
        val format = GwfTraceFormat()
        val trace = format.open(input)

        assertEquals(listOf(TABLE_TASKS), trace.tables)
    }

    @Test
    fun testTableExists() {
        val input = checkNotNull(GwfTraceFormatTest::class.java.getResource("/trace.gwf"))
        val format = GwfTraceFormat()
        val table = format.open(input).getTable(TABLE_TASKS)

        assertNotNull(table)
        assertDoesNotThrow { table!!.newReader() }
    }

    @Test
    fun testTableDoesNotExist() {
        val input = checkNotNull(GwfTraceFormatTest::class.java.getResource("/trace.gwf"))
        val format = GwfTraceFormat()
        val trace = format.open(input)

        assertFalse(trace.containsTable("test"))
        assertNull(trace.getTable("test"))
    }

    @Test
    fun testTableReader() {
        val input = checkNotNull(GwfTraceFormatTest::class.java.getResource("/trace.gwf"))
        val format = GwfTraceFormat()
        val table = format.open(input).getTable(TABLE_TASKS)!!
        val reader = table.newReader()

        assertAll(
            { assertTrue(reader.nextRow()) },
            { assertEquals("0", reader.get(TASK_WORKFLOW_ID)) },
            { assertEquals("1", reader.get(TASK_ID)) },
            { assertEquals(Instant.ofEpochSecond(16), reader.get(TASK_SUBMIT_TIME)) },
            { assertEquals(Duration.ofSeconds(11), reader.get(TASK_RUNTIME)) },
            { assertEquals(emptySet<String>(), reader.get(TASK_PARENTS)) },
        )
    }

    @Test
    fun testTableReaderPartition() {
        val input = checkNotNull(GwfTraceFormatTest::class.java.getResource("/trace.gwf"))
        val format = GwfTraceFormat()
        val table = format.open(input).getTable(TABLE_TASKS)!!

        assertThrows<IllegalArgumentException> { table.newReader("test") }
    }
}
