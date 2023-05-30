/*
 * @(#)NodeFigure.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.net.figures;

import static org.jhotdraw.draw.AttributeKeys.*;

import java.awt.geom.*;
import java.util.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.connector.LocatorConnector;
import org.jhotdraw.draw.figure.ConnectionFigure;
import org.jhotdraw.draw.figure.LineConnectionFigure;
import org.jhotdraw.draw.figure.RectangleFigure;
import org.jhotdraw.draw.figure.TextFigure;
import org.jhotdraw.draw.handle.BoundsOutlineHandle;
import org.jhotdraw.draw.handle.ConnectorHandle;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.MoveHandle;
import org.jhotdraw.draw.locator.RelativeLocator;
import org.jhotdraw.geom.Geom;
import org.jhotdraw.geom.Insets2D;
import org.jhotdraw.util.*;

/** NodeFigure. */
public class NodeFigure extends TextFigure {

  private static final long serialVersionUID = 1L;
  private LinkedList<Connector> connectors;
  private static LocatorConnector north;
  private static LocatorConnector south;
  private static LocatorConnector east;
  private static LocatorConnector west;

  public NodeFigure() {
    RectangleFigure rf = new RectangleFigure();
    setDecorator(rf);
    createConnectors();
    attr().set(DECORATOR_INSETS, new Insets2D.Double(6, 10, 6, 10));
    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.samples.net.Labels");
    setText(labels.getString("nodeDefaultName"));
    attr().setAttributeEnabled(DECORATOR_INSETS, false);
  }

  private void createConnectors() {
    connectors = new LinkedList<>();
    connectors.add(new LocatorConnector(this, RelativeLocator.north()));
    connectors.add(new LocatorConnector(this, RelativeLocator.east()));
    connectors.add(new LocatorConnector(this, RelativeLocator.west()));
    connectors.add(new LocatorConnector(this, RelativeLocator.south()));
  }

  @Override
  public Collection<Connector> getConnectors(ConnectionFigure prototype) {
    return Collections.unmodifiableList(connectors);
  }

  @Override
  public Collection<Handle> createHandles(int detailLevel) {
    java.util.List<Handle> handles = new LinkedList<Handle>();
    switch (detailLevel) {
      case -1:
        handles.add(new BoundsOutlineHandle(getDecorator(), false, true));
        break;
      case 0:
        handles.add(new MoveHandle(this, RelativeLocator.northWest()));
        handles.add(new MoveHandle(this, RelativeLocator.northEast()));
        handles.add(new MoveHandle(this, RelativeLocator.southWest()));
        handles.add(new MoveHandle(this, RelativeLocator.southEast()));
        for (Connector c : connectors) {
          handles.add(new ConnectorHandle(c, new LineConnectionFigure()));
        }
        break;
    }
    return handles;
  }

  @Override
  public Rectangle2D.Double getFigureDrawingArea() {
    Rectangle2D.Double b = super.getFigureDrawingArea();
    // Grow for connectors
    Geom.grow(b, 10d, 10d);
    return b;
  }

  @Override
  public Connector findConnector(Point2D.Double p, ConnectionFigure figure) {
    // return closest connector
    double min = Double.MAX_VALUE;
    Connector closest = null;
    for (Connector c : connectors) {
      Point2D.Double p2 = Geom.center(c.getBounds());
      double d = Geom.length2(p.x, p.y, p2.x, p2.y);
      if (d < min) {
        min = d;
        closest = c;
      }
    }
    return closest;
  }

  @Override
  public Connector findCompatibleConnector(Connector c, boolean isStart) {
    if (c instanceof LocatorConnector) {
      LocatorConnector lc = (LocatorConnector) c;
      for (Connector cc : connectors) {
        LocatorConnector lcc = (LocatorConnector) cc;
        if (lcc.getLocator().equals(lc.getLocator())) {
          return lcc;
        }
      }
    }
    return connectors.getFirst();
  }

  @Override
  public NodeFigure clone() {
    NodeFigure that = (NodeFigure) super.clone();
    that.createConnectors();
    return that;
  }

  @Override
  public int getLayer() {
    return -1; // stay below ConnectionFigures
  }
}
