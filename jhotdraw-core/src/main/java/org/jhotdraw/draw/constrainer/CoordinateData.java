package org.jhotdraw.draw.constrainer;

import java.awt.geom.Point2D;

/**
 * data of additional coordinates for constrainer.
 *
 * @author tw
 */
public final class CoordinateData {

  private final Point2D.Double[] coords;
  private final int acutalIndex;

  public CoordinateData(Point2D.Double[] coords, int acutalIndex) {
    this.coords = coords;
    this.acutalIndex = acutalIndex;
  }

  public Point2D.Double[] getCoords() {
    return coords;
  }

  public int getActualIndex() {
    return acutalIndex;
  }
}
