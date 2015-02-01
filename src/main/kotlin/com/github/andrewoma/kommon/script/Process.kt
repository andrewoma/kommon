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

import java.io.File
import java.io.Writer
import java.io.InputStream
import java.io.InputStreamReader
import java.io.StringWriter

/**
 * Executes a system command using ProcessBuilder
 */
public fun exec(command: List<String>,
        directory: File = currentDir,
        environment: Map<String, String> = mapOf(),
        useShell: Boolean = false,
        redirectError: Boolean = false): Process {

    val b = ProcessBuilder()
    b.directory(directory)
    b.redirectErrorStream(redirectError)
    // TODO ... support other platforms
    b.command(if (useShell) arrayListOf("/bin/sh", "-c", *command.copyToArray()) else command)
    b.environment()?.putAll(environment)

    return b.start()
}

fun String.dropRight(num: Int): String {
    if (num >= this.length()) return this
    return "..." + this.drop(this.length() - num)
}

/**
 * Executes a system command using the system shell, handling the output while it waits for the process to terminate
 */
public fun shell(command: String,
        directory: File = currentDir,
        environment: Map<String, String> = mapOf(),
        redirectError: Boolean = false,
        captureOut: Boolean = true,
        captureError: Boolean = true,
        verify: (Int) -> Boolean = { it == 0 }): ProcessResult {

    val result = exec(command = listOf(command), directory = directory, redirectError = redirectError,
            useShell = true, environment = environment).result(captureOut, captureError)

    if (verify(result.exitCode)) return result

    val error = """Command exit code failed verification.
        Command: $command
        Directory: $directory
        Environment: $environment
        RedirectError: $redirectError
        CaptureOut: $captureOut
        CaptureError: $captureError
        ExitCode: ${result.exitCode}
        Out: ${result.out.dropRight(1000)}
        Error: ${result.error.dropRight(1000)}"""

    check(verify(result.exitCode), error)
    return result
}

/**
 * Process result contains the exit code process output if captured
 */
public class ProcessResult(val out: String, val error: String, val exitCode: Int) {
    fun verify(f: (Int) -> Boolean = { it == 0 }): ProcessResult {
        check(f(exitCode), "Unexpected exit code: $exitCode")
        return this;
    }
}

/**
 * Waits for a process to terminate, either capturing or discarding output as specified.
 */
public fun Process.result(captureOut: Boolean = true, captureError: Boolean = true): ProcessResult {
    // NullWriter allows output to be consumed and discarded. Otherwise execution blocks on some platforms
    class NullWriter : Writer() {
        override fun write(cbuf: CharArray, off: Int, len: Int) {
        }
        override fun flush() {
        }
        override fun close() {
        }
        override fun toString(): String = ""
    }

    class OutputConsumer(val input: InputStream?, val writer: Writer) : Thread() {
        {
            if (input != null) start()
        }

        override fun run() {
            InputStreamReader(input!!).copyTo(writer)
        }
    }

    val outConsumer = OutputConsumer(this.getInputStream(), if (captureOut) StringWriter() else NullWriter())
    val errorConsumer = OutputConsumer(this.getErrorStream(), if (captureError) StringWriter() else NullWriter())
    val exitCode = this.waitFor()
    outConsumer.join()
    errorConsumer.join()

    return ProcessResult(outConsumer.writer.toString(), errorConsumer.writer.toString(), exitCode)
}
