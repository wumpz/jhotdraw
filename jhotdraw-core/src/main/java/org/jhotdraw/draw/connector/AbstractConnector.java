/*
 * @(#)AbstractConnector.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.connector;

import java.awt.*;
import java.awt.geom.*;
import org.jhotdraw.draw.figure.ConnectionFigure;
import org.jhotdraw.draw.figure.DecoratedFigure;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.geom.Geom;

/**
 * This abstract class can be extended to implement a {@link Connector}.
 *
 * @see Connector
 */
public class AbstractConnector implements Connector {

  private static final long serialVersionUID = 1L;
  /** The owner of the connector */
  private Figure owner;
  /** Whether we should connect to the figure or to its decorator. */
  private boolean isConnectToDecorator;
  /**
   * Whether the state of this connector is persistent. Set this to true only, when the user
   * interface allows to change the state of the connector.
   */
  private boolean isStatePersistent;

  /**
   * Constructs a connector that has no owner. It is only used internally to resurrect a connectors
   * from a StorableOutput. It should never be called directly.
   */
  public AbstractConnector() {
    owner = null;
  }

  /** Constructs a connector with the given owner figure. */
  public AbstractConnector(Figure owner) {
    this.owner = owner;
  }

  public void setConnectToDecorator(boolean newValue) {
    isConnectToDecorator = newValue;
  }

  public boolean isConnectToDecorator() {
    return isConnectToDecorator;
  }

  protected final Figure getConnectorTarget(Figure f) {
    return (isConnectToDecorator && ((DecoratedFigure) f).getDecorator() != null)
        ? ((DecoratedFigure) f).getDecorator()
        : f;
  }

  /**
   * Tests if a point is contained in the connector. This implementation tests if the point is
   * contained by the figure object, which owns this connector.
   */
  @Override
  public boolean contains(Point2D.Double p) {
    return getOwner().contains(p);
  }

  @Override
  public Point2D.Double findStart(ConnectionFigure connection) {
    return findPoint(connection);
  }

  @Override
  public Point2D.Double findEnd(ConnectionFigure connection) {
    return findPoint(connection);
  }

  /**
   * Gets the connection point. Override when the connector does not need to distinguish between the
   * start and end point of a connection.
   */
  protected Point2D.Double findPoint(ConnectionFigure connection) {
    return Geom.center(getBounds());
  }

  /** Gets the connector's owner. */
  @Override
  public Figure getOwner() {
    return owner;
  }

  /** Sets the connector's owner. */
  public void setOwner(Figure newValue) {
    owner = newValue;
  }

  @Override
  public Object clone() {
    try {
      AbstractConnector that = (AbstractConnector) super.clone();
      return that;
    } catch (CloneNotSupportedException e) {
      InternalError error = new InternalError(e.toString());
      // error.initCause(e); <- requires JDK 1.4
      throw error;
    }
  }

  /**
   * This is called, when the start location of the connection has been moved by the user. The user
   * has this probably done, to adjust the layout. The connector may use this as a hint to improve
   * the results for the next call to findEnd.
   */
  public void updateStartLocation(Point2D.Double p) {}

  /**
   * This is called, when the end location of the connection has been moved by the user. The user
   * has this probably done, to adjust the layout. The connector may use this as a hint to improve
   * the results for the next call to findStart.
   */
  public void updateEndLocation(Point2D.Double p) {}

  @Override
  public Point2D.Double getAnchor() {
    return Geom.center(getBounds());
  }

  @Override
  public void updateAnchor(Point2D.Double p) {}

  @Override
  public Rectangle2D.Double getBounds() {
    return isConnectToDecorator()
        ? ((DecoratedFigure) getOwner()).getDecorator().getBounds()
        : getOwner().getBounds();
  }

  @Override
  public Rectangle2D.Double getDrawingArea() {
    Point2D.Double anchor = getAnchor();
    return new Rectangle2D.Double(anchor.x - 4, anchor.y - 4, 8, 8);
  }

  @Override
  public void draw(Graphics2D g) {
    Point2D.Double anchor = getAnchor();
    Ellipse2D.Double e = new Ellipse2D.Double(anchor.x - 3, anchor.y - 3, 6, 6);
    g.setColor(Color.BLUE);
    g.fill(e);
    // g.setColor(Color.BLACK);
    // g.draw(e);
  }
}
