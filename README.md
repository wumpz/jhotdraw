# jhotdraw

[![Java CI with Maven](https://github.com/wumpz/jhotdraw/actions/workflows/maven.yml/badge.svg)](https://github.com/wumpz/jhotdraw/actions/workflows/maven.yml)

* heavy restructuring of classes and interfaces and cleanup
** complete attribute handling of Figure moved in class Attributes, access over **attr()**
** Drawing has no dependency to CompositeFigure anymore and implementations do not use 
   AbstractCompositeFigure implementations
** Drawing has its own listener DrawingListener now instead of FigureListener and CompositeFigureListener
** contains(point, scale) is now called to take view scale into account for finding figures
** removed DEBUG mode and introduced some logging instead
** removed DOMStorable from Drawing, Figure
** introduced a new module **jhotdraw-io** for input output and dom storables
* JDK 17
* maven build process
* restructured project layout
  * introduced submodules

## License

* LGPL V2.1
* Creative Commons Attribution 2.5 License

## History 

This is a fork of jhotdraw from http://sourceforge.net/projects/jhotdraw.
