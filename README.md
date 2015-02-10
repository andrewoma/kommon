#### Kommon Overview

Kommon is a "commons" library for Kotlin.

It fills in a few gaps in Kotlin's current standard library and will hopefully cease to exist
as the standard library matures.

#### Status

Unstable and not for production use.

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
// Concatenate Maps
val map = mapOf(1 to 2) + mapOf(3 to 4)

// Process a stream in chunks. e.g. insert records in batches of 10
val records: Stream<Record> ...
for (batch in records.chunked(10)) {
    insert(batch)
}

// Process a stream using a window to look ahead and/or behind
val lines: Stream<String> ...
for (window in lines.window(before = 1, after = 1) {
    val (prev, current, next) = window
    ...
}
```

##### Strings
```kotlin
// Trim the margin from multi-line strings
val map = """
    create table actor (
        actor_id integer identity,
        first_name character varying(45) not null,
        last_name character varying(45) null,
        last_update timestamp not null
    )
""".trimMargin()

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
