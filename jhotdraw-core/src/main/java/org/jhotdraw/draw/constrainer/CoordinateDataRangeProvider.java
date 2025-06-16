package org.jhotdraw.draw.constrainer;

/**
 * Which range of Coordinates is needed from CoordinateDataSupplier? Some constraints need more than one point to
 * get calculated, e.g. line snap.
 *
 */
public interface CoordinateDataRangeProvider {
  int needsPointsAfter();

  int needsPointsBefore();
}
