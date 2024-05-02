/*
 * @(#)ConnectorHandle.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.handle;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Collections;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.figure.ConnectionFigure;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * A {@link Handle} associated to a {@link Connector} which allows to create a new {@link
 * ConnectionFigure} by dragging the handle to another connector.
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class ConnectorHandle extends AbstractHandle {

  /** Holds the ConnectionFigure which is currently being created. */
  private ConnectionFigure createdConnection;

  /** The prototype for the ConnectionFigure to be created */
  private ConnectionFigure prototype;

  /** The Connector. */
  private Connector connector;

  /** The current connectable Figure. */
  private Figure connectableFigure;

  /** The current connectable Connector. */
  private Connector connectableConnector;

  /** All connectors of the connectable Figure. */
  protected Collection<Connector> connectors = Collections.emptyList();

  public ConnectorHandle(Connector connector, ConnectionFigure prototype) {
    super(connector.getOwner());
    this.connector = connector;
    this.prototype = prototype;
  }

  public Point2D.Double getLocationOnDrawing() {
    return connector.getAnchor();
  }

  @Override
  public Point getScreenLocation() {
    return view.drawingToView(connector.getAnchor());
  }

  @Override
  public void draw(Graphics2D g) {
    Graphics2D gg = (Graphics2D) g.create();
    gg.transform(view.getDrawingToViewTransform());
    for (Connector c : connectors) {
      c.draw(gg);
    }
    if (createdConnection == null) {
      drawCircle(
          g,
          getEditor()
              .getHandleAttribute(HandleAttributeKeys.DISCONNECTED_CONNECTOR_HANDLE_FILL_COLOR),
          getEditor()
              .getHandleAttribute(HandleAttributeKeys.DISCONNECTED_CONNECTOR_HANDLE_STROKE_COLOR));
    } else {
      drawCircle(
          g,
          getEditor().getHandleAttribute(HandleAttributeKeys.CONNECTED_CONNECTOR_HANDLE_FILL_COLOR),
          getEditor()
              .getHandleAttribute(HandleAttributeKeys.CONNECTED_CONNECTOR_HANDLE_STROKE_COLOR));
      Point p = view.drawingToView(createdConnection.getEndPoint());
      g.setColor(getEditor()
          .getHandleAttribute(HandleAttributeKeys.CONNECTED_CONNECTOR_HANDLE_FILL_COLOR));
      int width = getHandlesize();
      g.fillOval(p.x - width / 2, p.y - width / 2, width, width);
      g.setColor(getEditor()
          .getHandleAttribute(HandleAttributeKeys.CONNECTED_CONNECTOR_HANDLE_STROKE_COLOR));
      g.drawOval(p.x - width / 2, p.y - width / 2, width, width);
    }
  }

  @Override
  public void trackStart(Point anchor, int modifiersEx) {
    setConnection(createConnection());
    Point2D.Double p = getLocationOnDrawing();
    getConnection().setStartPoint(p);
    getConnection().setEndPoint(p);
    view.getDrawing().add(getConnection());
  }

  @Override
  public void trackStep(Point anchor, Point lead, int modifiersEx) {
    // updateConnectors(lead);
    Point2D.Double p = view.viewToDrawing(lead);
    fireAreaInvalidated(getDrawingArea());
    Figure figure = findConnectableFigure(p, view.getDrawing());
    if (figure != connectableFigure) {
      connectableFigure = figure;
      repaintConnectors();
    }
    connectableConnector = findConnectableConnector(figure, p);
    if (connectableConnector != null) {
      p = connectableConnector.getAnchor();
    }
    getConnection().willChange();
    getConnection().setEndPoint(p);
    getConnection().changed();
    fireAreaInvalidated(getDrawingArea());
  }

  @Override
  public Rectangle getDrawingArea() {
    if (getConnection() != null) {
      Rectangle r = new Rectangle(view.drawingToView(getConnection().getEndPoint()));
      r.grow(getHandlesize(), getHandlesize());
      return r;
    } else {
      return new Rectangle(); // empty rectangle
    }
  }

  @Override
  public void trackEnd(Point anchor, Point lead, int modifiersEx) {
    Point2D.Double p = view.viewToDrawing(lead);
    if (view.getConstrainer() != null) {
      p = view.getConstrainer().constrainPoint(p);
    }
    Figure f = findConnectableFigure(p, view.getDrawing());
    connectableConnector = findConnectableConnector(f, p);
    if (connectableConnector != null) {
      final Drawing drawing = view.getDrawing();
      final ConnectionFigure c = getConnection();
      getConnection().setStartConnector(connector);
      getConnection().setEndConnector(connectableConnector);
      getConnection().updateConnection();
      view.clearSelection();
      view.addToSelection(c);
      view.getDrawing().fireUndoableEditHappened(new AbstractUndoableEdit() {
        private static final long serialVersionUID = 1L;

        @Override
        public String getPresentationName() {
          ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
          return labels.getString("edit.createConnectionFigure.text");
        }

        @Override
        public void undo() throws CannotUndoException {
          super.undo();
          drawing.remove(c);
        }

        @Override
        public void redo() throws CannotRedoException {
          super.redo();
          drawing.add(c);
          view.clearSelection();
          view.addToSelection(c);
        }
      });
    } else {
      view.getDrawing().remove(getConnection());
      fireAreaInvalidated(getDrawingArea());
    }
    connectableConnector = null;
    connectors = Collections.emptyList();
    setConnection(null);
    setTargetFigure(null);
  }

  /** Creates the ConnectionFigure. By default the figure prototype is cloned. */
  protected ConnectionFigure createConnection() {
    return (ConnectionFigure) prototype.clone();
  }

  protected void setConnection(ConnectionFigure newConnection) {
    createdConnection = newConnection;
  }

  protected ConnectionFigure getConnection() {
    return createdConnection;
  }

  protected Figure getTargetFigure() {
    return connectableFigure;
  }

  protected void setTargetFigure(Figure newTargetFigure) {
    connectableFigure = newTargetFigure;
  }

  private Figure findConnectableFigure(Point2D.Double p, Drawing drawing) {
    for (Figure figure : drawing.getFiguresFrontToBack()) {
      if (!figure.includes(getConnection()) && figure.isConnectable() && figure.contains(p)) {
        return figure;
      }
    }
    return null;
  }

  /** Finds a connection end figure. */
  protected Connector findConnectableConnector(Figure connectableFigure, Point2D.Double p) {
    Connector target =
        (connectableFigure == null) ? null : connectableFigure.findConnector(p, getConnection());
    if ((connectableFigure != null)
        && connectableFigure.isConnectable()
        && !connectableFigure.includes(getOwner())
        && getConnection().canConnect(connector, target)) {
      return target;
    }
    return null;
  }

  @Override
  public boolean isCombinableWith(Handle handle) {
    return false;
  }

  /**
   * Updates the list of connectors that we draw when the user moves or drags the mouse over a
   * figure to which can connect.
   */
  public void repaintConnectors() {
    Rectangle2D.Double invalidArea = null;
    for (Connector c : connectors) {
      if (invalidArea == null) {
        invalidArea = c.getDrawingArea();
      } else {
        invalidArea.add(c.getDrawingArea());
      }
    }
    connectors = (connectableFigure == null)
        ? Collections.emptyList()
        : connectableFigure.getConnectors(prototype);
    for (Connector c : connectors) {
      if (invalidArea == null) {
        invalidArea = c.getDrawingArea();
      } else {
        invalidArea.add(c.getDrawingArea());
      }
    }
    if (invalidArea != null) {
      view.getComponent().repaint(view.drawingToView(invalidArea));
    }
  }
}
