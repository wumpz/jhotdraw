package org.jhotdraw.draw.constrainer;

/**
 * To set a coordinate data supplier. This is used for e.g. points used by constrainers.
 */
public interface CoordinateDataReceiver {

  void clearCoordinateSupplier();

  CoordinateDataSupplier getCoordinateSupplier();

  void setCoordinateSupplier(CoordinateDataSupplier coordinateSupplier);
}
