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

import java.io.StringReader

/**
 * Trims the margin from a multi-line string.
 *
 * The main use case if for cleaning up triple-quoted Strings embedded in code such as:
 *
 *     val s = """
 *         The rain in spain
 *         falls mainly on the plain
 *     """
 *
 * The algorithm is:
 * 1. Strip the first and last lines if they are empty or only contain whitespace.
 * 2. Find the minimum of the first non-whitespace character in all lines
 * 3. Trim the minimum from the start of each line
 *
 * Limitations:
 * 1. There's no accounting of special characters such as tabs
 * 2. Line delimiters are not preserved - the system default line delimiter is used
 * 3. The implementation doesn't try to be particularly efficient
 */
public fun String.trimMargin(): String {
    fun trimBlanksLines(): List<String> {
        val lines = StringReader(this).useLines { it.toList() }
        val start = if (lines.isNotEmpty() && lines.first().isBlank()) 1 else 0
        val end = if (lines.size() > 1 && lines.last().isBlank()) lines.size() - 1 else lines.size()
        return lines.subList(start, end)
    }

    fun firstNonWhitespaceCharacter(s: String) =
            s.withIndex().firstOrNull { !it.value.isWhitespace() }?.index ?: s.length()

    val lines = trimBlanksLines()
    val margin = lines.fold(Integer.MAX_VALUE) {(min, s) -> Math.min(min, firstNonWhitespaceCharacter(s)) }

    return lines.stream().map { it.substring(margin) }.joinToString(LINE_SEPARATOR)
}

public fun String.isBlank(): Boolean = this.trim().isEmpty()

public val LINE_SEPARATOR: String = System.getProperty("line.separator")!!
