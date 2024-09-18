# storky

`storky` is a thread-safe, serializable, in-memory **key value store** provided as a micro library written in Scala.

## Requirements

* Scala >= 3.3.0

* Java >= 17

* Built with scala-cli using latest Scala 3.3 LTS

## How to use storky

This is a micro library, which means that it is a small, single-file library with a permissive license that you can:

1. Just copy-paste into your project

2. Download using this directive in your code:
```scala
//> using dep "storky:storky:1.0.0,url=https://github.com/bjornregnell/storky/releases/download/v1.0.0/storky_3-1.0.0.jar"
```
Or start the Scala REPL using Scala >= 3.5.0 with
```bash
scala repl --dep "storky:storky:1.0.0,url=https://github.com/bjornregnell/storky/releases/download/v1.0.0/storky_3-1.0.0.jar"
```


Then you can:
```scala
scala> val s = storky.Store.empty[Int, String]()
val s: storky.Store[Int, String] = Map()

scala> val old = s.put(1, "hello")
val old: Option[String] = None

scala> val old2 = s.put(1,"hello updated")
val old2: Option[String] = Some(hello)

scala> val value = s.get(1)
val value: Option[String] = Some(hello updated)

scala> val deleted = s.remove(1)
val deleted: Option[String] = Some(hello updated)

scala> val removed = s.get(1)
val removed: Option[String] = None
```

See more operations for atomic updating of multiple values in `storky.scala`

## How to build storky

Create a jar-file using scala-cli
```
scala-cli --power package storky.scala -o storky_3-1.0.0.jar --library
```

##

