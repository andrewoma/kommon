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

package com.github.andrewoma.kommon.io

import java.io.InputStream
import org.junit.Test as test
import java.io.ByteArrayInputStream
import kotlin.test.assertTrue
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LineReaderWithPositionTest {
    val String.input: InputStream
        get() = ByteArrayInputStream(this.toByteArray(Charsets.UTF_8))

    test fun empty() {
        assertTrue(LineReaderWithPosition("".input).sequence().toList().isEmpty())
    }

    test fun singleLineWithinBuffer() {
        val s = "hello"
        val list = LineReaderWithPosition(s.input).sequence().toList()
        assertEquals(1, list.size())
        assertEquals(LineWithPosition(0, s.length(), s, ""), list.first())
    }

    test fun singleLineOverBuffers() {
        val s = "hello there phil"
        val list = LineReaderWithPosition(s.input, 3).sequence().toList()
        assertEquals(1, list.size())
        assertEquals(LineWithPosition(0, s.length(), s, ""), list.first())
    }

    test fun singleLineMatchesBuffer() {
        val s = "123456489"
        val list = LineReaderWithPosition(s.input, 9).sequence().toList()
        assertEquals(1, list.size())
        assertEquals(LineWithPosition(0, s.length(), s, ""), list.first())
    }

    test fun singleLineMultipleOfBuffers() {
        val s = "123456489"
        val list = LineReaderWithPosition(s.input, 3).sequence().toList()
        assertEquals(1, list.size())
        assertEquals(LineWithPosition(0, s.length(), s, ""), list.first())
    }

    test fun multipleLinesWithLf() {
        val s = "12\n34\n56"
        val list = LineReaderWithPosition(s.input).sequence().toList()
        assertEquals(3, list.size())
        assertEquals(LineWithPosition(0, 3, "12", "\n"), list[0])
        assertEquals(LineWithPosition(3, 6, "34", "\n"), list[1])
        assertEquals(LineWithPosition(6, 8, "56", ""), list[2])
    }

    test fun multipleLinesWithLfWithTrailing() {
        val s = "12\n34\n56\n"
        val list = LineReaderWithPosition(s.input).sequence().toList()
        assertEquals(3, list.size())
        assertEquals(LineWithPosition(0, 3, "12", "\n"), list[0])
        assertEquals(LineWithPosition(3, 6, "34", "\n"), list[1])
        assertEquals(LineWithPosition(6, 9, "56", "\n"), list[2])
    }

    test fun multipleLinesWithCr() {
        val s = "12\r34\r56"
        val list = LineReaderWithPosition(s.input).sequence().toList()
        assertEquals(3, list.size())
        assertEquals(LineWithPosition(0, 3, "12", "\r"), list[0])
        assertEquals(LineWithPosition(3, 6, "34", "\r"), list[1])
        assertEquals(LineWithPosition(6, 8, "56", ""), list[2])
    }

    test fun multipleLinesWithCrTrailing() {
        val s = "12\r34\r56\r"
        val list = LineReaderWithPosition(s.input).sequence().toList()
        assertEquals(3, list.size())
        assertEquals(LineWithPosition(0, 3, "12", "\r"), list[0])
        assertEquals(LineWithPosition(3, 6, "34", "\r"), list[1])
        assertEquals(LineWithPosition(6, 9, "56", "\r"), list[2])
    }

    test fun multipleLinesWithCrLf() {
        val s = "12\r\n34\r\n56"
        val list = LineReaderWithPosition(s.input).sequence().toList()
        assertEquals(3, list.size())
        assertEquals(LineWithPosition(0, 4, "12", "\r\n"), list[0])
        assertEquals(LineWithPosition(4, 8, "34", "\r\n"), list[1])
        assertEquals(LineWithPosition(8, 10, "56", ""), list[2])
    }

    test fun multipleLinesWithCrLfTrailing() {
        val s = "12\r\n34\r\n56\r\n"
        val list = LineReaderWithPosition(s.input).sequence().toList()
        assertEquals(3, list.size())
        assertEquals(LineWithPosition(0, 4, "12", "\r\n"), list[0])
        assertEquals(LineWithPosition(4, 8, "34", "\r\n"), list[1])
        assertEquals(LineWithPosition(8, 12, "56", "\r\n"), list[2])
    }

    test fun blankLineCr() {
        val s = "\r"
        val list = LineReaderWithPosition(s.input).sequence().toList()
        assertEquals(1, list.size())
        assertEquals(LineWithPosition(0, 1, "", "\r"), list[0])
    }

    test fun blankLineLf() {
        val s = "\n"
        val list = LineReaderWithPosition(s.input).sequence().toList()
        assertEquals(1, list.size())
        assertEquals(LineWithPosition(0, 1, "", "\n"), list[0])
    }

    test fun blankLineCrLf() {
        val s = "\r\n"
        val list = LineReaderWithPosition(s.input).sequence().toList()
        assertEquals(1, list.size())
        assertEquals(LineWithPosition(0, 2, "", "\r\n"), list[0])
    }

    test fun mingledBlanks() {
        val s = "12\n\n34"
        val list = LineReaderWithPosition(s.input).sequence().toList()
        assertEquals(3, list.size())
        assertEquals(LineWithPosition(0, 3, "12", "\n"), list[0])
        assertEquals(LineWithPosition(3, 4, "", "\n"), list[1])
        assertEquals(LineWithPosition(4, 6, "34", ""), list[2])
    }

    test(expected = javaClass<IllegalStateException>()) fun readPastEof() {
        val s = "hello"
        val reader = LineReaderWithPosition(s.input)
        assertEquals(LineWithPosition(0, s.length(), s, ""), reader.readLine())
        assertNull(reader.readLine())
        reader.readLine()
    }

    fun assertRoundTrip(string: String, bufferSize: Int = 10) {
        val result = LineReaderWithPosition(string.input, bufferSize).sequence()
                .map { it.line + it.delimiter }
                .fold("") { result, current -> result + current }
        assertEquals(string, result)
    }

    test fun roundTrip1() {
        assertRoundTrip(
                """If you are not dealing with Android, you may need to disable the Android Plugin in order to compile the project.

Since Kotlin project contains code written in Kotlin itself, you will also need a Kotlin plugin to build the project in IntelliJ IDEA.
To keep the plugin version in sync with the rest of the team and our Continuous Integration server you should install
the according to the instructions below.""")
    }

    test fun roundTrip2() {
        val input = """buildscript {
  repositories {
    mavenCentral()
  }
  dependencies {
    classpath 'org.jetbrains.kotlin:kotlin-gradle-plugin:0.8.1527'
  }
}

apply plugin: "kotlin"
apply plugin: "maven"

group = "com.github.andrewoma.kommon"
version = "0.1-SNAPSHOT"

compileKotlin {
//    kotlinOptions.annotations = file('annotations')
}

repositories {
  mavenCentral()
}

dependencies {
  compile 'org.jetbrains.kotlin:kotlin-stdlib:0.8.1527'
  testCompile 'junit:junit:4.11'
}

task wrapper(type: Wrapper) {
  gradleVersion = "1.11"
}"""
        assertRoundTrip(input)

        // test a bunch of buffer sizes for boundary conditions
        for (i in 1..input.length() + 10) {
            assertRoundTrip(input, bufferSize = i)
        }
    }

    // TODO ... add a random string generator and test round trips
}