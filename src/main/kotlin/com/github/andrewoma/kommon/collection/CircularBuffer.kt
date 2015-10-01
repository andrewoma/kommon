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

import java.util.*

// TODO - this should be internal but the tests via the gradle build no longer have access
class CircularBuffer<T>(size: Int) : List<T> {
    private var elements: Array<Any?>
    private var start = 0
    private var end = 0
    private var full = false
    private var maxElements = 0

    init {
        require(size >= 0) { "The size must be greater than 0" }
        elements = arrayOfNulls<Any>(size)
        maxElements = elements.size()
    }

    override fun size() = when {
        end < start -> maxElements - start + end
        end == start -> if (full) maxElements else 0
        else -> end - start
    }

    override fun isEmpty() = size() == 0

    fun add(element: T) {
        if (size() == maxElements) {
            remove()
        }

        elements[end++] = element
        if (end >= maxElements) {
            end = 0
        }
        if (end == start) {
            full = true
        }
    }

    fun remove(): T {
        if (isEmpty()) throw NoSuchElementException()

        val element = elements[start]
        elements[start++] = null
        if (start >= maxElements) {
            start = 0
        }
        full = false

        @Suppress("UNCHECKED_CAST")
        return element as T
    }

    private fun increment(index: Int) = (index + 1).let { if (it >= maxElements) 0 else it }

    override fun iterator(): Iterator<T> {
        return object : Iterator<T> {
            private var index = start
            private var lastReturnedIndex = -1
            private var isFirst = full

            override fun hasNext() = isFirst || (index != end)

            override fun next(): T {
                if (!hasNext()) {
                    throw NoSuchElementException()
                }
                isFirst = false
                lastReturnedIndex = index
                index = increment(index)

                @Suppress("UNCHECKED_CAST")
                return elements[lastReturnedIndex] as T
            }
        }
    }

    override fun contains(o: Any?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun containsAll(c: Collection<Any?>): Boolean {
        throw UnsupportedOperationException()
    }

    override fun get(index: Int): T {
        if (index >= size()) throw IndexOutOfBoundsException("")
        val pos = (start + index).let { if (it >= maxElements) it - maxElements else it }
        @Suppress("UNCHECKED_CAST")
        return elements[pos] as T
    }

    override fun indexOf(o: Any?): Int {
        throw UnsupportedOperationException()
    }

    override fun lastIndexOf(o: Any?): Int {
        throw UnsupportedOperationException()
    }

    override fun listIterator(): ListIterator<T> {
        throw UnsupportedOperationException()
    }

    override fun listIterator(index: Int): ListIterator<T> {
        throw UnsupportedOperationException()
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<T> {
        throw UnsupportedOperationException()
    }

    override fun toString(): String {
        return this.joinToString(", ", "[", "]")
    }
}
