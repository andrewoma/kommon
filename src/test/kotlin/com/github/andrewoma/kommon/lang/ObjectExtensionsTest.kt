/*
 * Copyright (c) 2016 Andrew O'Malley
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

import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class Foo(val bar: String, val baz: Int, val quz: String?) {
    override fun equals(other: Any?) = equals(this, other) { o1, o2 ->
        return o1.bar == o2.bar && o1.baz == o2.baz && o1.quz == o2.quz
    }

    override fun hashCode() = Objects.hash(bar, baz, quz)
}

class Bar(val foo: String, val baz: Int, val quz: String?) {
    override fun equals(other: Any?) = equals(this, other, Bar::foo, Bar::baz, Bar::quz)

    override fun hashCode() = Objects.hash(foo, baz, quz)
}

class ObjectExtensionsTest {

    @Test fun `should implement equality via function`() {
        assertEquals(Foo("a", 1, "b"), Foo("a", 1, "b"))
        assertEquals(Foo("a", 1, null), Foo("a", 1, null))
        val foo = Foo("a", 1, null)
        assertEquals(foo, foo)

        assertFalse(Foo("a", 1, "b") == Foo("b", 1, "b"))
        assertFalse(Foo("a", 1, "b") == Foo("a", 2, "b"))
        assertFalse(Foo("a", 1, "b") == Foo("a", 1, "c"))
        assertFalse(Foo("a", 1, "b") == Foo("a", 1, null))
        assertFalse(Foo("a", 1, "b").equals("x"))
    }

    @Test fun `should implement equality via properties`() {
        assertEquals(Bar("a", 1, "b"), Bar("a", 1, "b"))
        assertEquals(Bar("a", 1, null), Bar("a", 1, null))
        val Bar = Bar("a", 1, null)
        assertEquals(Bar, Bar)

        assertFalse(Bar("a", 1, "b") == Bar("b", 1, "b"))
        assertFalse(Bar("a", 1, "b") == Bar("a", 2, "b"))
        assertFalse(Bar("a", 1, "b") == Bar("a", 1, "c"))
        assertFalse(Bar("a", 1, "b") == Bar("a", 1, null))
        assertFalse(Bar("a", 1, "b").equals("x"))
    }
}
