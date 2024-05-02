package org.jhotdraw.draw.constrainer;

import java.util.Collection;

/**
 * Delivers needed coordinates for a CoordinateDataReceiver and the valid index.
 * This is needed for multipoint snaps, e.g. angle or distance. This is mainly used by drawing
 * functions that should snap in real time.
 *
 * The supplier is set into a class CoodinateDataReceiver, which mainly are snap functions. This
 * is done by any activated tool. The tool itself is a CoordinateDataSupplier. Therefore selfsnaps, 90 degree
 * snapps, length snaps are possible while drawing.
 */
public interface CoordinateDataSupplier {

  public CoordinateData getConstrainerCoordinates(int before, int after);

  public default CoordinateData getConstrainerCoordinatesForExtensions(
      Collection<? extends CoordinateDataRangeProvider> exts) {
    int before = exts.stream().mapToInt(i -> i.needsPointsBefore()).max().orElse(0);
    int after = exts.stream().mapToInt(i -> i.needsPointsAfter()).max().orElse(0);
    return getConstrainerCoordinates(before, after);
  }
}
