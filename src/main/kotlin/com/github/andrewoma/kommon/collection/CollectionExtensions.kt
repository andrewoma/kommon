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

import kotlin.support.AbstractIterator
import java.util.ArrayList
import java.util.HashMap

public fun <T> Sequence<T>.chunked(size: Int): Sequence<List<T>> {
    val iterator = this.iterator()

    return object : Sequence<List<T>> {
        override fun iterator() = object : AbstractIterator<List<T>>() {
            override fun computeNext() {
                val next = ArrayList<T>(size)
                while (iterator.hasNext() && next.size() < size) {
                    next.add(iterator.next())
                }
                if (next.isEmpty()) done() else setNext(next)
            }
        }
    }
}

/**
 * Creates a HashMap with a capacity to handle 'size' elements without requiring internal resizing.
 * It does this by adding roughly 30% extra to the size to allow for the default 0.75 load factor
 */
public fun <K, V> hashMapOfExpectedSize(size: Int): HashMap<K, V> = HashMap(capacity(size))

fun capacity(size: Int) = (size.toLong() + (size / 3) + 1).let {
    if (it > Integer.MAX_VALUE) Integer.MAX_VALUE else it.toInt()
}

/**
 * Creates a fixed size window view within a stream, allowing arbitrary look aheads or look behinds.
 *
 * e.g. listOf(1, 2, 3, 4).stream().window(before = 1, after = 1).forEach { println(it) }
 *   [null, 1, 2]
 *   [1, 2, 3]
 *   [2, 3, 4]
 *   [3, 4, null]
 *
 * Note that the position of current element is always same as the and window is always the same size
 * (padded with nulls where required).
 *
 * This allows for easy processing of common cases through list destructuring. e.g.
 *
 *   listOf(1, 2, 3).stream().window(after = 1).forEach {
 *     val (current, next) = it
 *     ...
 *   }
 *
 *  For better performance setting reuseList = true will return the internal buffer directly instead of
 *  creating a copy. WARNING: Only do this if consuming the returned list before the next iteration
 */
[suppress("BASE_WITH_NULLABLE_UPPER_BOUND")]
public fun <T> Sequence<T>.window(before: Int = 0, after: Int = 0, reuseList: Boolean = false): Sequence<List<T?>> {
    val iterator = this.iterator()

    return object : Sequence<List<T?>> {
        val size = before + after + 1
        val buffer = CircularBuffer<T?>(size)
        var ahead = 0
        var first = true;

        init {
            for (i in 1..size) {
                when {
                    i <= before -> buffer.add(null)
                    iterator.hasNext() -> { buffer.add(iterator.next()); ahead++ }
                    else -> buffer.add(null)
                }
            }
        }

        override fun iterator() = object : AbstractIterator<List<T?>>() {
            fun setNext() = setNext(if (reuseList) buffer else ArrayList(buffer))

            override fun computeNext() {
                // TODO ... special casing for first seems ugly
                if (first) {
                    first = false
                    if (ahead == 0) done() else { ahead--; setNext() }
                    return
                }

                when {
                    iterator.hasNext() -> { buffer.add(iterator.next()); setNext() }
                    ahead == 0 -> done()
                    else -> { buffer.add(null); ahead--; setNext() }
                }
            }
        }
    }
}