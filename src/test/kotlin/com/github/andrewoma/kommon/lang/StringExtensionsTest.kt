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

package com.github.andrewoma.kommon.lang

import org.junit.Test as test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StringExtensionsTest {
    test fun `trim margin should have no affect on empty string`() {
        assertEquals("", "".trimMargin())
    }

    test fun `trim margin should trim left a single line`() {
        assertEquals("hello there", "    hello there".trimMargin())
    }

    test fun `trim margin should remove empty first and last lines`() {
        val input = """
            The rain in Spain
               Falls mainly on the plain
            Or does it?
        """

        val expected = """The rain in Spain
   Falls mainly on the plain
Or does it?"""

        assertEquals(expected, input.trimMargin())
    }

    test fun `trim margin should preserve first and last lines if not blank`() {
        val input = """            The rain in Spain
               Falls mainly on the plain
            Or does it?"""

        val expected = """The rain in Spain
   Falls mainly on the plain
Or does it?"""

        assertEquals(expected, input.trimMargin())
    }

    test fun `isBlank should be true for blank string`() {
        assertTrue("\r\n\t ".isBlank())
    }

    test fun `isBlank should be false for non-blank string`() {
        assertFalse("\r\n\t a".isBlank())
    }

    test fun `truncateRight should return the right hand side of a string`() {
        assertEquals("", "hello".truncateRight(0))
        assertEquals("llo", "hello".truncateRight(3))
        assertEquals("hello", "hello".truncateRight(5))
        assertEquals("hello", "hello".truncateRight(100))
    }

    test fun `truncateRight should prefix on truncation`() {
        assertEquals("...", "hello".truncateRight(0, "..."))
        assertEquals("...llo", "hello".truncateRight(3, "..."))
    }
}
