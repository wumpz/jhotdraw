# jhotdraw

[![Java CI with Maven](https://github.com/wumpz/jhotdraw/actions/workflows/maven.yml/badge.svg)](https://github.com/wumpz/jhotdraw/actions/workflows/maven.yml)

## News

> **ATTENTION**: Due to the refactoring in 10.0-SNAPSHOT this version breaks API of JHotdraw. Some adaptions are needed, e.g.: attributes now using `attr()`, ...

* heavy restructuring of classes and interfaces and cleanup
  * removed FigureListener from some Figures and Handles
  * complete attribute handling of Figure moved in class Attributes, access over **attr()**
  * Drawing has no dependency to CompositeFigure anymore and implementations do not use 
   AbstractCompositeFigure implementations
  * Drawing has its own listener DrawingListener now instead of FigureListener and CompositeFigureListener
  * contains(point, scale) is now called to take view scale into account for finding figures
  * removed DEBUG mode and introduced some logging instead
  * removed DOMStorable from Drawing, Figure
  * introduced a new module **jhotdraw-io** for input output and dom storables
* JDK 17
* maven build process
* restructured project layout
  * introduced submodules

## Quickstart

This projects jars are not yet published to maven central or GitHub packages. To use those you first need to build it with **maven** using: `mv clean install`. Now all jars are published to your local maven repository. And you can include those artifacts using e.g.

```xml
<dependency>
  <groupId>org.jhotdraw</groupId>
  <artifactId>jhotdraw-core</artifactId>
  <version>10.0-SNAPSHOT</version>
</dependency>
```

In module `jhotdraw-samples-mini` are small examples mostly highlighting one aspect of JHotdraw usage.
Additional to that are in module `jhotdraw-samples-misc` more sophisticated examples of using this library.


## License

* LGPL V2.1
* Creative Commons Attribution 2.5 License

## History 

This is a fork of jhotdraw from http://sourceforge.net/projects/jhotdraw.
