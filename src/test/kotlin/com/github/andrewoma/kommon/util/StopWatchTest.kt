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

import org.junit.Test
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.MINUTES
import java.util.concurrent.TimeUnit.NANOSECONDS
import java.util.concurrent.TimeUnit.SECONDS
import kotlin.test.assertEquals

class StopWatchTest {
    var time = 0L
    val stopWatch = StopWatch({ time })

    @Test fun `Elapsed should equal start - stop`() {
        stopWatch.start()
        time = 10
        stopWatch.stop()
        time = 20
        assertEquals(10L, stopWatch.elapsed(NANOSECONDS))
    }

    @Test fun `Elapsed should equal start - current while running`() {
        stopWatch.start()
        time = 10
        assertEquals(10L, stopWatch.elapsed(NANOSECONDS))
    }

    @Test fun `Elapse should accumulate`() {
        stopWatch.start()
        time = 10
        stopWatch.stop()
        time = 20
        stopWatch.start()
        time = 25
        stopWatch.stop()
        time = 30

        assertEquals(15L, stopWatch.elapsed(NANOSECONDS))
    }

    @Test fun `Time should equal function execution time`() {
        time = 10
        stopWatch.time {
            time = 25
        }
        time = 35
        assertEquals(15L, stopWatch.elapsed(NANOSECONDS))
    }

    @Test fun `Reset should clear timer`() {
        stopWatch.start()
        time = 10
        stopWatch.stop()
        assertEquals(10L, stopWatch.elapsed(NANOSECONDS))

        stopWatch.reset()
        assertEquals(0L, stopWatch.elapsed(NANOSECONDS))
    }

    @Test fun `Elapsed should convert units`() {
        stopWatch.start()
        time = SECONDS.toNanos(2)
        stopWatch.stop()
        assertEquals(2L, stopWatch.elapsed(SECONDS))
        assertEquals(2000L, stopWatch.elapsed(MILLISECONDS))
    }

    @Test fun `toString should display with precision and units`() {
        stopWatch.start()
        time = SECONDS.toNanos(2)
        stopWatch.stop()
        assertEquals("0.033 m", stopWatch.toString(MINUTES))
        assertEquals("2.000 s", stopWatch.toString(SECONDS))
        assertEquals("2000.000 ms", stopWatch.toString())
        assertEquals("2 s", stopWatch.toString(SECONDS, 0))
    }
}
