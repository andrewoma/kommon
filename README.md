#### Kommon Overview

Kommon is a "commons" library for Kotlin.

It fills in a few gaps in Kotlin's current standard library and will hopefully cease to exist
as the standard library matures.

#### Status

Unstable, but probably works.

Releases are available in Maven Central [here](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.andrewoma.kommon%22).

The current release (0.10) is compatible with 1.0.2.

Older releases are available for historical Kotlin versions.

#### Features

##### Shell

The shell package contains some convenience functions for executing external processes. e.g.
```kotlin
val out = shell("ls -lah").out
val out = shell("echo \$HELLO \$WORLD", environment = mapOf("HELLO" to "foo", "WORLD" to "bar")).out
```

##### Collections

The collections package contains extension functions for collections. e.g.
```kotlin
// Process a sequence in chunks. e.g. insert records in batches of 10
val records: Sequence<Record> ...
for (batch in records.chunked(10)) {
    insert(batch)
}

// Process a sequence using a window to look ahead and/or behind
val lines: Sequence<String> ...
for ((prev, current, next) in lines.window(before = 1, after = 1).asIterable) {
    ...
}
```

##### Strings
```kotlin
// Truncate, keeping the end of a string
assertEquals("llo", "hello".truncateRight(3))
```

##### Language
```kotlin
// Helper to generate equals with a type safe function
class Foo(val bar: String, val baz: Int, val quz: String?) {
    override fun equals(other: Any?) = equals(this, other) { o1, o2 ->
        return o1.bar == o2.bar && o1.baz == o2.baz && o1.quz == o2.quz
    }
}

// Helper to generate equals with a list of properties
class Bar(val foo: String, val baz: Int, val quz: String?) {
    override fun equals(other: Any?) = equals(this, other, Bar::foo, Bar::baz, Bar::quz)
}
```

##### Misc
```kotlin
// A stop watch for basic timings
val sw = StopWatch()
sw.start()
...
sw.stop()
println(sw.toString(TimeUnit.SECONDS))
```

See the [tests](/src/test/kotlin/com/github/andrewoma/kommon) for more examples.

#### License
This project is licensed under a MIT license.

[![Build Status](https://travis-ci.org/andrewoma/kommon.svg?branch=master)](https://travis-ci.org/andrewoma/kommon)
