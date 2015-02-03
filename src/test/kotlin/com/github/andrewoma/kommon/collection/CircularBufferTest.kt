/*
 * Copyright (c) 2015 Andrew O'Malley
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.github.andrewoma.kommon.collection

import org.junit.Test as test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class CircularBufferTest {

    test fun `an empty buffer should have no size`() {
        val buffer = CircularBuffer<Int>(3)
        assertEquals(0, buffer.size())
        assertTrue(buffer.isEmpty())
        assertFalse(buffer.iterator().hasNext())
    }

    test fun `adding`() {
        val buffer = CircularBuffer<Int>(2)

        buffer.add(1)
        assertEquals(1, buffer.size())
        assertEquals(1, buffer[0])
        assertFalse(buffer.isEmpty())
        assertEquals(listOf(1), buffer.stream().toList())

        buffer.add(2)
        assertEquals(2, buffer.size())
        assertEquals(1, buffer[0])
        assertEquals(2, buffer[1])
        assertFalse(buffer.isEmpty())
        assertEquals(listOf(1, 2), buffer.stream().toList())

        buffer.add(3)
        assertEquals(2, buffer.size())
        assertEquals(2, buffer[0])
        assertEquals(3, buffer[1])
        assertFalse(buffer.isEmpty())
        assertEquals(listOf(2, 3), buffer.stream().toList())
    }

    test fun `removing`() {
        val buffer = CircularBuffer<Int>(2)

        buffer.add(1)
        buffer.add(2)
        assertEquals(listOf(1, 2), buffer.stream().toList())

        assertEquals(1, buffer.remove())
        assertEquals(1, buffer.size())
        assertEquals(2, buffer[0])
        assertFalse(buffer.isEmpty())
        assertEquals(listOf(2), buffer.stream().toList())

        assertEquals(2, buffer.remove())
        assertEquals(0, buffer.size())
        assertTrue(buffer.isEmpty())
    }

    test fun `using nulls`() {
        val buffer = CircularBuffer<Int?>(2)
        buffer.add(null)
        buffer.add(null)
        buffer.add(null)
        assertEquals(listOf(null, null), buffer.stream().toList())
    }
}