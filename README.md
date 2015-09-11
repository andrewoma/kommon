#### Kommon Overview

Kommon is a "commons" library for Kotlin.

It fills in a few gaps in Kotlin's current standard library and will hopefully cease to exist
as the standard library matures.

#### Status

Unstable, but probably works.

[0.1](http://search.maven.org/#artifactdetails%7Ccom.github.andrewoma.kommon%7Ckommon%7C0.1%7Cjar) is available
in Maven Central and is compatible with Kotlin M11.

[0.2](http://search.maven.org/#artifactdetails%7Ccom.github.andrewoma.kommon%7Ckommon%7C0.2%7Cjar) is available
in Maven Central and is compatible with Kotlin M12.

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
for (window in lines.window(before = 1, after = 1) {
    val (prev, current, next) = window
    ...
}
```

##### Strings
// Truncate, keeping the end of a string
assertEquals("llo", "hello".truncateRight(3))
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
