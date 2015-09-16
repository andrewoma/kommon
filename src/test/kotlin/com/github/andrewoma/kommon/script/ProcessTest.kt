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

import org.junit.Ignore
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.fail

class ProcessTest() {
    @Test fun `shell should capture output by default`() {
        val result = shell("echo 'Hello' && echo 'Goodbye' 1>&2")
        assertEquals("Hello", result.out.trim())
        assertEquals("Goodbye", result.error.trim())
    }

    @Test fun `shell without capture should not capture output`() {
        val result = shell("echo 'Hello' && echo 'Goodbye' 1>&2", captureOut = false, captureError = false)
        assertEquals("", result.out)
        assertEquals("", result.error)
    }

    @Test fun `shell with redirected stderr should capture error in out`() {
        val out = shell("echo 'Hello' && echo 'Goodbye' 1>&2", redirectError = true).out
        assertEquals("Hello\nGoodbye", out.trim())
    }

    @Test fun `shell should have access to passed environment`() {
        val out = shell("echo \$HELLO \$WORLD", environment = mapOf("HELLO" to "foo", "WORLD" to "bar")).out
        assertEquals("foo bar", out.trim())
    }

    @Test(expected = IllegalStateException::class) fun `shell with default verification should fail on non-zero exit code`() {
        shell("exit 1")
    }

    @Test fun `shell with default verification should succeed on zero exit code`() {
        shell("exit 0")
    }

    @Test fun `shell should honour custom verification function`() {
        shell("exit 1", verify = { true })
    }

    @Test fun `env should give access to environment variables`() {
        assertNotNull(env("HOME"))
    }

    @Ignore @Test fun `shell should give meaningful errors on failure`() {
        try {
            shell("echo 'Hello' && echo 'Goodbye' 1>&2 && exit 1")
            fail()
        } catch(e: IllegalStateException) {
            val expected = """Command exit code failed verification.
        Command: echo 'Hello' && echo 'Goodbye' 1>&2 && exit 1
        Directory: /Users/andrew/dev/projects/kommon
        Environment: {}
        RedirectError: false
        CaptureOut: true
        CaptureError: true
        ExitCode: 1
        Out: Hello

        Error: Goodbye
"""
            assertEquals(expected, e.getMessage())
        }
    }
}
