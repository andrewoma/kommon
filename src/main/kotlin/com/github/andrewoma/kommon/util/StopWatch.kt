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

package com.github.andrewoma.kommon.util

import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.*

/**
 * StopWatch is a simple timer. Multiple timings can be accumulated by calling start() and stop()
 * in sequence.
 */
class StopWatch(private val currentTime: () -> Long = { System.nanoTime() }) {
    private var start: Long = 0
    private var elapsedNanoseconds: Long = 0
    private var running: Boolean = false

    fun start(): StopWatch {
        check(!running) { "The stop watch is already started" }
        start = currentTime()
        running = true
        return this
    }

    inline fun <R> time(f: () -> R): R {
        start()
        try {
            return f()
        } finally {
            stop()
        }
    }

    fun stop(): StopWatch {
        check(running) { "The stop watch is already stopped" }
        running = false
        elapsedNanoseconds += currentTime() - start
        return this
    }

    fun reset(): StopWatch {
        elapsedNanoseconds = 0
        running = false
        return this
    }

    fun elapsed(unit: TimeUnit): Long {
        val elapsed = if (running) elapsedNanoseconds + currentTime() - start else elapsedNanoseconds
        return unit.convert(elapsed, NANOSECONDS)
    }

    override fun toString() = toString(MILLISECONDS)

    fun toString(unit: TimeUnit, precision: Int = 3): String {
        val value = elapsed(NANOSECONDS).toDouble() / NANOSECONDS.convert(1, unit)

        return "%.${precision}f %s".format(value, unit.abbreviation())
    }
}

fun TimeUnit.abbreviation(): String = when (this) {
    NANOSECONDS -> "ns"
    MICROSECONDS -> "\u03bcs"
    MILLISECONDS -> "ms"
    SECONDS -> "s"
    MINUTES -> "m"
    HOURS -> "h"
    DAYS -> "d"
    else -> name
}
