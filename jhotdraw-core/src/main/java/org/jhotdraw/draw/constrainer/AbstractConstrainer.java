/*
 * @(#)AbstractConstrainer.java
 *
 * Copyright (c) 2007 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.constrainer;

import java.awt.geom.Point2D;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/** This abstract class can be extended to implement a {@link Constrainer}. */
public abstract class AbstractConstrainer implements Constrainer, CoordinateDataReceiver {

  private static final long serialVersionUID = 1L;

  /** The listeners waiting for model changes. */
  protected EventListenerList listenerList = new EventListenerList();

  /**
   * Only one <code>ChangeEvent</code> is needed per model instance since the event's only
   * (read-only) state is the source property. The source of events generated here is always "this".
   */
  protected transient ChangeEvent changeEvent = null;

  public AbstractConstrainer() {}

  @Override
  public void addChangeListener(ChangeListener l) {
    listenerList.add(ChangeListener.class, l);
  }

  @Override
  public void removeChangeListener(ChangeListener l) {
    listenerList.remove(ChangeListener.class, l);
  }

  /**
   * Runs each <code>ChangeListener</code>'s <code>stateChanged</code> method.
   *
   * @see EventListenerList
   */
  protected void fireStateChanged() {
    Object[] listeners = listenerList.getListenerList();
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == ChangeListener.class) {
        if (changeEvent == null) {
          changeEvent = new ChangeEvent(this);
        }
        ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
      }
    }
  }

  @Override
  public AbstractConstrainer clone() {
    AbstractConstrainer that;
    try {
      that = (AbstractConstrainer) super.clone();
    } catch (CloneNotSupportedException ex) {
      throw new InternalError("unable to clone", ex);
    }
    that.listenerList = new EventListenerList();
    return that;
  }

  private final Set<AbstractCoordinateConstrainerExtension> coordExtension = new LinkedHashSet<>();

  public void clearCoordConstrainerExtensions() {
    coordExtension.clear();
  }

  public void addCoordConstrainerExtension(AbstractCoordinateConstrainerExtension ext) {
    coordExtension.add(ext);
  }

  public void removeCoordConstrainerExtension(AbstractCoordinateConstrainerExtension ext) {
    coordExtension.remove(ext);
  }

  public <T extends AbstractCoordinateConstrainerExtension> T getCoordConstrainerExtension(
      Class<T> clazz) {
    for (AbstractCoordinateConstrainerExtension item : coordExtension) {
      if (clazz.isInstance(item)) {
        return (T) item;
      }
    }
    return null;
  }

  private CoordinateDataSupplier coordinateSupplier = null;

  @Override
  public void clearCoordinateSupplier() {
    coordinateSupplier = null;
  }

  @Override
  public CoordinateDataSupplier getCoordinateSupplier() {
    return coordinateSupplier;
  }

  @Override
  public void setCoordinateSupplier(CoordinateDataSupplier coordinateSupplier) {
    this.coordinateSupplier = coordinateSupplier;
  }

  /**
   * Constrains this point by extensions. This is used to e.g. contrain angles, distances, ....
   * To activage this, it has to be included in some constrainPoint methods while implementing
   * a new Constrainer.
   *
   * The main procedure here is, that drawing tools are able to provide points while drawing. Mainly
   * all CoordinateDataSuppliers are used. To add one you need to set it using setCoordinateSupplier.
   *
   * @param point
   * @param snapDistance
   * @return
   */
  protected Point2D.Double constrainPointByExtensions(
      final Point2D.Double point, final double snapDistance) {
    Point2D.Double snap = point;
    if (coordinateSupplier != null && !coordExtension.isEmpty()) {
      CoordinateData cdata =
          coordinateSupplier.getConstrainerCoordinatesForExtensions(coordExtension);

      for (AbstractCoordinateConstrainerExtension ext : coordExtension) {
        if (ext.isActive()) {
          snap = ext.constrainPoint(cdata, snapDistance, snap);
        }
      }
    }
    return snap;
  }
}
