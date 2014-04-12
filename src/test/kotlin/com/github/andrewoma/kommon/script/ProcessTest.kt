/*
 * Copyright (c) 2014 Andrew O'Malley
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

package com.github.andrewoma.kommon.script

import org.junit.Test as test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotNull
import kotlin.test.fail

class ProcessTest() {
    test fun shellWithCapture() {
        val result = shell("echo 'Hello' && echo 'Goodbye' 1>&2")
        assertEquals("Hello", result.out.trim())
        assertEquals("Goodbye", result.error.trim())
    }

    test fun shellWithoutCapture() {
        val result = shell("echo 'Hello' && echo 'Goodbye' 1>&2", captureOut = false, captureError = false)
        assertEquals("", result.out)
        assertEquals("", result.error)
    }

    test fun shellWithRedirectError() {
        val out = shell("echo 'Hello' && echo 'Goodbye' 1>&2", redirectError = true).out
        assertEquals("Hello\nGoodbye", out.trim())
    }

    test fun shellWithEnvironment() {
        val out = shell("echo \$HELLO \$WORLD", environment = mapOf("HELLO" to "foo", "WORLD" to "bar")).out
        assertEquals("foo bar", out.trim())
    }

    test(expected = javaClass<IllegalStateException>()) fun shellDefaultVerificationWithFailure() {
        shell("exit 1")
    }

    test fun shellDefaultVerification() {
        shell("exit 0")
    }

    test fun shellWithCustomVerification() {
        shell("exit 1", verify = { true })
    }

    test fun env() {
        assertNotNull(env("HOME"))
    }

    test fun errorMessage() {
        try {
            shell("echo 'Hello' && echo 'Goodbye' 1>&2 && exit 1")
            fail()
        } catch(e: IllegalStateException) {
            println(e.getMessage()) // For visual inspection
        }
    }

    test fun dropRight() {
        assertEquals("...", "hello".dropRight(0))
        assertEquals("...llo", "hello".dropRight(3))
        assertEquals("hello", "hello".dropRight(5))
        assertEquals("hello", "hello".dropRight(100))
    }
}
