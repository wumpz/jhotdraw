/*____________________________________________________________________
  INIT Copyright (C) 2000 All rights reserved
        
    File:            OrderedFigureElement.java
    Description:     X
    Author:          WMG
    Created:         2000.03.28
  ____________________________________________________________________
*/



package CH.ifa.draw.standard;

import CH.ifa.draw.framework.Figure;



class OrderedFigureElement implements Comparable {



  //_________________________________________________________VARIABLES



  private Figure  _theFigure;
  private int     _nZ;



  //______________________________________________________CONSTRUCTORS



  public OrderedFigureElement(Figure aFigure, int nZ) {

    _theFigure = aFigure;
    _nZ = nZ;

  }



  private OrderedFigureElement() {

  }



  //____________________________________________________PUBLIC METHODS



  public Figure getFigure() {

    return _theFigure;

  }



  public int getZValue() {

    return _nZ;

  }



  public int compareTo(Object o) {

    OrderedFigureElement ofe = (OrderedFigureElement) o;

    if (_nZ == ofe.getZValue()) {
      return 0;
    }

    if (_nZ > ofe.getZValue()) {
      return 1;
    }

    return -1;

  }



  //_______________________________________________________________END

} //end of class OrderedFigureElement
