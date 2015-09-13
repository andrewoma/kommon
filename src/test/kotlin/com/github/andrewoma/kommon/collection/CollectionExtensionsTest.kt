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

import kotlin.test.assertEquals
import org.junit.Test

class CollectionExtensionsTest {

    @Test fun `Chunked empty sequence should yield an empty list`() {
        assertEquals(listOf<List<Int>>(), listOf<Int>().asSequence().chunked(1).toList())
    }

    @Test fun `Chunked sequence of chunk size should yield a single list`() {
        assertEquals(listOf(listOf(1)), listOf(1).asSequence().chunked(1).toList())
        assertEquals(listOf(listOf(1, 2)), listOf(1, 2).asSequence().chunked(2).toList())
    }

    @Test fun `Chunked sequence of multiples should yield full lists`() {
        assertEquals(listOf(listOf(1), listOf(2)), listOf(1, 2).asSequence().chunked(1).toList())
        assertEquals(listOf(listOf(1, 2), listOf(3, 4)), listOf(1, 2, 3, 4).asSequence().chunked(2).toList())
    }

    @Test fun `Chunked sequence indivisible by size should yield remainder list`() {
        assertEquals(listOf(listOf(1, 2), listOf(3)), listOf(1, 2, 3).asSequence().chunked(2).toList())
    }

    @Test fun `Chunked sequence should yield remainder as list if sequence less than chunk size`() {
        assertEquals(listOf(listOf(1, 2)), listOf(1, 2).asSequence().chunked(5).toList())
    }

    @Test fun `HashMap with expected size should allow for load factor`() {
        assertEquals(1, capacity(0))
        assertEquals(14, capacity(10))
        assertEquals(134, capacity(100))
        assertEquals(1334, capacity(1000))
        assertEquals(Integer.MAX_VALUE, capacity(Integer.MAX_VALUE - 1)) // Check we don't overflow
    }

    @Test fun `HashMap with expected size should return a hashmap`() {
        assertEquals(mapOf<Int, Int>(), hashMapOfExpectedSize<Int, Int>(10))
    }

    @Test fun `Window looking ahead and behind should be fixed size`() {
        assertWindow(listOf(1, 2, 3).asSequence().window(before = 1, after = 1), "[[null, 1, 2], [1, 2, 3], [2, 3, null]]")
    }

    @Test fun `Window looking ahead should be fixed size`() {
        assertWindow(listOf(1, 2, 3).asSequence().window(after = 1), "[[1, 2], [2, 3], [3, null]]")
    }

    @Test fun `Window looking behind should be fixed size`() {
        assertWindow(listOf(1, 2, 3).asSequence().window(before = 1), "[[null, 1], [1, 2], [2, 3]]")
    }

    @Test fun `Window of current element should be window of size 1`() {
        assertWindow(listOf(1, 2, 3).asSequence().window(), "[[1], [2], [3]]")
    }

    @Test fun `Window of empty sequence should be supported`() {
        assertWindow(listOf<Int>().asSequence().window(), "[]")
    }

    @Test fun `Window ouput should support destructuring`() {
        val result = listOf(1, 2, 3).asSequence().window(before = 1, after = 1).map {
            val (prev, curr, next) = it
            println("$prev $curr $next")
            curr!! * 10
        }.toList()

        assertEquals(listOf(10, 20, 30), result)
    }

    private fun assertWindow(actual: Sequence<List<Int?>>, expected: String) {
        assertEquals(expected, actual.toList().toString())
    }
}
