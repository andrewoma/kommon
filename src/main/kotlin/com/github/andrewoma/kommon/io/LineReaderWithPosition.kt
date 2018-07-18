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

package com.github.andrewoma.kommon.io

import java.io.InputStream
import java.io.PushbackInputStream
import java.nio.charset.Charset

data class LineWithPosition(val start: Int, val end: Int, val line: String, val delimiter: String)

/**
 * LineReaderWithPosition is a line reader that records the byte positions of the line in the stream
 * as well as the delimiters for line endings.
 *
 * Delimiters allow the file to be faithfully reconstructed.
 *
 * Byte positions allows for resuming reading a stream from a given position. e.g. tailing logs as they are written
 *
 * LineReaderWithPosition assumes the input stream is buffered.
 */
class LineReaderWithPosition(inputStream: InputStream, val lineBufferSize: Int = 8192, val charSet: Charset = Charsets.UTF_8) {
    private enum class Eof { NotFound, Found, Reported }

    private companion object {
        val CR = '\r'.toInt()
        val LF = '\n'.toInt()

        val CR_DELIM = "\r"
        val LF_DELIM = "\n"
        val CR_LF_DELIM = "\r\n"
    }

    private val buffer = ByteArray(lineBufferSize)
    private var result: ByteArray? = null
    private var start = 0
    private var end = 0
    private var bufferPos = 0
    private var eof = Eof.NotFound
    private val inputStream = PushbackInputStream(inputStream)

    fun readLine(): LineWithPosition? {
        check(eof != Eof.Reported) { "Attempt to read past EOF" }

        if (eof == Eof.Found) {
            eof = Eof.Reported
            return null
        }

        while (true) {
            val char = inputStream.read()

            // EOF
            if (char == -1) {
                return if (start == end) {
                    eof = Eof.Reported
                    null
                } else {
                    eof = Eof.Found
                    result("")
                }
            }

            // Character read
            end++

            // EOL
            if (char == CR || char == LF) {
                val delimiter = if (char == CR) {
                    val next = inputStream.read()
                    if (next == LF) {
                        end++
                        CR_LF_DELIM
                    } else {
                        if (next != -1) {
                            inputStream.unread(next)
                        }
                        CR_DELIM
                    }
                } else {
                    LF_DELIM
                }
                return result(delimiter)
            } else {
                // Append the character to temporary buffers
                append(char.toByte())
            }
        }
    }

    private fun append(byte: Byte) {
        if (bufferPos == lineBufferSize) {
            // Buffer full
            if (result != null) {
                val newResult = ByteArray(result!!.size + lineBufferSize)
                System.arraycopy(result!!, 0, newResult, 0, result!!.size)
                System.arraycopy(buffer, 0, newResult, result!!.size, lineBufferSize)
                result = newResult
            } else {
                result = ByteArray(lineBufferSize)
                System.arraycopy(buffer, 0, result!!, 0, lineBufferSize)
            }
            bufferPos = 0
        }

        buffer[bufferPos] = byte
        bufferPos++
    }

    private fun result(delimiter: String): LineWithPosition? {
        val string = when {
            result == null -> String(buffer, 0, bufferPos, charSet)
            bufferPos == 0 -> String(result!!, charSet)
            else -> {
                val newResult = ByteArray(result!!.size + bufferPos)
                System.arraycopy(result!!, 0, newResult, 0, result!!.size)
                System.arraycopy(buffer, 0, newResult, result!!.size, bufferPos)
                String(newResult, charSet)
            }
        }

        val line = LineWithPosition(start, end, string, delimiter)

        start = end
        bufferPos = 0
        result = null

        return line
    }

    fun asSequence(): Sequence<LineWithPosition> {
        return generateSequence { this.readLine() }
    }
}
