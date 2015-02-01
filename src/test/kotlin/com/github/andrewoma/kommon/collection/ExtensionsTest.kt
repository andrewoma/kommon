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
import kotlin.test.assertFalse
import java.util.HashMap

class ExtensionsTest {
    test fun `map plus should concatenate maps`() {
        assertEquals(mapOf(1 to 2) + mapOf(3 to 4) + mapOf(5 to 6), mapOf(1 to 2, 3 to 4, 5 to 6))
        assertEquals(mapOf(1 to 2) + mapOf(3 to 4, 5 to 6), mapOf(1 to 2, 3 to 4, 5 to 6))
    }

    test fun `map plus should concatenate into a new instance`() {
        val map = mapOf(1 to 2)
        val newMap = map + mapOf(3 to 4)
        assertEquals(mapOf(1 to 2, 3 to 4), newMap)
        assertFalse(map.identityEquals(newMap))
    }

    test fun `chunked empty stream should yield an empty list`() {
        assertEquals(listOf<List<Int>>(), listOf<Int>().stream().chunked(1).toList())
    }

    test fun `chunked stream of chunk size should yield a single list`() {
        assertEquals(listOf(listOf(1)), listOf(1).stream().chunked(1).toList())
        assertEquals(listOf(listOf(1, 2)), listOf(1, 2).stream().chunked(2).toList())
    }

    test fun `chunked stream of multiples should yield full lists`() {
        assertEquals(listOf(listOf(1), listOf(2)), listOf(1, 2).stream().chunked(1).toList())
        assertEquals(listOf(listOf(1, 2), listOf(3, 4)), listOf(1, 2, 3, 4).stream().chunked(2).toList())
    }

    test fun `chunked stream indivisible by size should yield remainder list`() {
        assertEquals(listOf(listOf(1, 2), listOf(3)), listOf(1, 2, 3).stream().chunked(2).toList())
    }

    test fun `chunked stream should yield remainder as list if stream less than chunk size`() {
        assertEquals(listOf(listOf(1, 2)), listOf(1, 2).stream().chunked(5).toList())
    }

    test fun `hashMap with expected size should allow for load factor`() {
        assertEquals(1, capacity(0))
        assertEquals(14, capacity(10))
        assertEquals(134, capacity(100))
        assertEquals(1334, capacity(1000))
        assertEquals(Integer.MAX_VALUE, capacity(Integer.MAX_VALUE - 1)) // Check we don't overflow
    }
}
