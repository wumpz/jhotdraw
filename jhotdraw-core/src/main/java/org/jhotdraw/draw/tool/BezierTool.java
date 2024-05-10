/*
 * @(#)BezierTool.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.tool;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.constrainer.CoordinateData;
import org.jhotdraw.draw.constrainer.CoordinateDataReceiver;
import org.jhotdraw.draw.constrainer.CoordinateDataSupplier;
import org.jhotdraw.draw.figure.BezierFigure;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.geom.Geom;
import org.jhotdraw.geom.path.Bezier;
import org.jhotdraw.geom.path.BezierPath;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * A {@link Tool} which allows to create a new {@link BezierFigure} by drawing its path.
 *
 * <p>To creation of the BezierFigure can be finished by adding a segment which closes the path, or
 * by double clicking on the drawing area, or by selecting a different tool in the DrawingEditor.
 */
public class BezierTool extends AbstractTool implements CoordinateDataSupplier {

  private static final long serialVersionUID = 1L;

  private Boolean finishWhenMouseReleased;
  protected Map<AttributeKey<?>, Object> attributes;
  private boolean isToolDoneAfterCreation;

  /** The prototype for new figures. */
  private BezierFigure prototype;

  /** The created figure. */
  protected BezierFigure createdFigure;

  protected Figure addedFigure;

  private int nodeCountBeforeDrag;

  /** A localized name for this tool. The presentationName is displayed by the UndoableEdit. */
  private String presentationName;

  private Point mouseLocation;

  /** Holds the view on which we are currently creating a figure. */
  private DrawingView creationView;

  private final boolean calculateFittedCurveAfterCreation;

  public BezierTool(BezierFigure prototype) {
    this(prototype, null);
  }

  public BezierTool(BezierFigure prototype, boolean calculateFittedCurveAfterCreation) {
    this(prototype, null, null, calculateFittedCurveAfterCreation);
  }

  public BezierTool(BezierFigure prototype, Map<AttributeKey<?>, Object> attributes) {
    this(prototype, attributes, null);
  }

  public BezierTool(BezierFigure prototype, Map<AttributeKey<?>, Object> attributes, String name) {
    this(prototype, attributes, null, true);
  }

  public BezierTool(
      BezierFigure prototype,
      Map<AttributeKey<?>, Object> attributes,
      String name,
      boolean calculateFittedCurveAfterCreation) {
    this.prototype = prototype;
    this.attributes = attributes;
    if (name == null) {
      ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
      name = labels.getString("edit.createFigure.text");
    }
    this.presentationName = name;
    this.calculateFittedCurveAfterCreation = calculateFittedCurveAfterCreation;
  }

  public String getPresentationName() {
    return presentationName;
  }

  @Override
  public void activate(DrawingEditor editor) {
    super.activate(editor);
    getView().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    if (getView().getConstrainer() instanceof CoordinateDataReceiver receiver) {
      receiver.setCoordinateSupplier(this);
    }
  }

  @Override
  public void deactivate(DrawingEditor editor) {
    super.deactivate(editor);
    getView().setCursor(Cursor.getDefaultCursor());
    if (createdFigure != null) {
      if (anchor != null && mouseLocation != null) {
        Rectangle r = new Rectangle(anchor);
        r.add(mouseLocation);
        if (createdFigure.getNodeCount() > 0 && createdFigure.isClosed()) {
          r.add(getView().drawingToView(createdFigure.getStartPoint()));
        }
        fireAreaInvalidated(r);
      }
      finishCreation(createdFigure, creationView);
      createdFigure = null;
    }
    if (getView().getConstrainer() != null
        && getView().getConstrainer() instanceof CoordinateDataReceiver receiver) {
      receiver.clearCoordinateSupplier();
    }
  }

  @Override
  public void mousePressed(MouseEvent evt) {
    if (mouseLocation != null) {
      Rectangle r = new Rectangle(mouseLocation);
      r.add(evt.getPoint());
      r.grow(1, 1);
      fireAreaInvalidated(r);
    }
    mouseLocation = evt.getPoint();
    super.mousePressed(evt);
    if (createdFigure != null && creationView != getView()) {
      finishCreation(createdFigure, creationView);
      createdFigure = null;
    }
    if (createdFigure == null) {
      creationView = getView();
      creationView.clearSelection();
      finishWhenMouseReleased = null;
      createdFigure = createFigure();
      createdFigure.addNode(new BezierPath.Node(
          creationView.getConstrainer() == null
              ? creationView.viewToDrawing(anchor)
              : creationView
                  .getConstrainer()
                  .constrainPoint(creationView.viewToDrawing(anchor), createdFigure)));
      getDrawing().add(processCreatedFigureBeforeAddingToDocument(createdFigure));
    } else {
      if (evt.getClickCount() == 1) {
        addPointToFigure(
            creationView.getConstrainer() == null
                ? creationView.viewToDrawing(anchor)
                : creationView
                    .getConstrainer()
                    .constrainPoint(creationView.viewToDrawing(anchor), createdFigure));
      }
    }
    nodeCountBeforeDrag = createdFigure.getNodeCount();
  }

  @Override
  protected Figure processCreatedFigureBeforeAddingToDocument(Figure figure) {
    addedFigure = figure;
    return figure;
  }

  @SuppressWarnings("unchecked")
  protected BezierFigure createFigure() {
    BezierFigure f = prototype.clone();
    getEditor().applyDefaultAttributesTo(f);
    if (attributes != null) {
      for (Map.Entry<AttributeKey<?>, Object> entry : attributes.entrySet()) {
        f.attr().set((AttributeKey<Object>) entry.getKey(), entry.getValue());
      }
    }
    return f;
  }

  protected Figure getCreatedFigure() {
    return createdFigure;
  }

  protected Figure getAddedFigure() {
    return createdFigure;
  }

  protected void addPointToFigure(Point2D.Double newPoint) {
    int pointCount = createdFigure.getNodeCount();
    createdFigure.willChange();
    if (pointCount < 2) {
      createdFigure.addNode(new BezierPath.Node(newPoint));
    } else {
      Point2D.Double endPoint = createdFigure.getEndPoint();
      Point2D.Double secondLastPoint =
          (pointCount <= 1) ? endPoint : createdFigure.getPoint(pointCount - 2, 0);
      if (newPoint.equals(endPoint)) {
        // nothing to do
      } else if (pointCount > 1
          && Geom.lineContainsPoint(
              newPoint.x,
              newPoint.y,
              secondLastPoint.x,
              secondLastPoint.y,
              endPoint.x,
              endPoint.y,
              0.9f / getView().getScaleFactor())) {
        createdFigure.setPoint(pointCount - 1, 0, newPoint);
      } else {
        createdFigure.addNode(new BezierPath.Node(newPoint));
      }
    }
    createdFigure.changed();
  }

  @Override
  public void mouseClicked(MouseEvent evt) {
    if (createdFigure != null) {
      switch (evt.getClickCount()) {
        case 1:
          if (createdFigure.getNodeCount() > 2) {
            Rectangle r = new Rectangle(getView().drawingToView(createdFigure.getStartPoint()));
            r.grow(2, 2);
            if (r.contains(evt.getX(), evt.getY())) {
              createdFigure.setClosed(true);
              finishCreation(createdFigure, creationView);
              createdFigure = null;
              if (isToolDoneAfterCreation) {
                fireToolDone();
              }
            }
          }
          break;
        case 2:
          finishWhenMouseReleased = null;
          finishCreation(createdFigure, creationView);
          createdFigure = null;
          break;
      }
    }
  }

  protected void fireUndoEvent(Figure createdFigure, DrawingView creationView) {
    final Figure addedFigure = this.addedFigure;
    final Drawing addedDrawing = creationView.getDrawing();
    final DrawingView addedView = creationView;
    getDrawing().fireUndoableEditHappened(new AbstractUndoableEdit() {
      private static final long serialVersionUID = 1L;

      @Override
      public String getPresentationName() {
        return presentationName;
      }

      @Override
      public void undo() throws CannotUndoException {
        super.undo();
        addedDrawing.remove(addedFigure);
      }

      @Override
      public void redo() throws CannotRedoException {
        super.redo();
        addedView.clearSelection();
        addedDrawing.add(addedFigure);
        addedView.addToSelection(addedFigure);
      }
    });
  }

  @Override
  public void mouseReleased(MouseEvent evt) {
    isWorking = false;
    if (createdFigure.getNodeCount() > nodeCountBeforeDrag + 1) {
      createdFigure.willChange();
      BezierPath figurePath = createdFigure.getBezierPath();
      BezierPath digitizedPath = new BezierPath();
      for (int i = nodeCountBeforeDrag - 1, n = figurePath.size(); i < n; i++) {
        digitizedPath.add(figurePath.nodes().get(nodeCountBeforeDrag - 1));
        figurePath.remove(nodeCountBeforeDrag - 1);
      }
      BezierPath fittedPath = calculateFittedCurve(digitizedPath);
      // figurePath.addPolyline(digitizedPath);
      figurePath.addAll(fittedPath);
      createdFigure.setBezierPath(figurePath);
      createdFigure.changed();
      nodeCountBeforeDrag = createdFigure.getNodeCount();
    }
    if (finishWhenMouseReleased == Boolean.TRUE) {
      if (createdFigure.getNodeCount() > 1) {
        Rectangle r = new Rectangle(anchor.x, anchor.y, 0, 0);
        r.add(evt.getX(), evt.getY());
        maybeFireBoundsInvalidated(r);
        finishCreation(createdFigure, creationView);
        createdFigure = null;
        finishWhenMouseReleased = null;
        return;
      }
    } else if (finishWhenMouseReleased == null) {
      finishWhenMouseReleased = Boolean.FALSE;
    }
    // repaint dotted line
    Rectangle r = new Rectangle(anchor);
    r.add(mouseLocation);
    r.add(evt.getPoint());
    r.grow(1, 1);
    fireAreaInvalidated(r);
    anchor.x = evt.getX();
    anchor.y = evt.getY();
    mouseLocation = evt.getPoint();
  }

  protected void finishCreation(BezierFigure createdFigure, DrawingView creationView) {
    fireUndoEvent(createdFigure, creationView);
    creationView.addToSelection(createdFigure);
    if (isToolDoneAfterCreation) {
      fireToolDone();
    }
  }

  @Override
  public void mouseDragged(MouseEvent evt) {
    if (finishWhenMouseReleased == null) {
      finishWhenMouseReleased = Boolean.TRUE;
    }
    int x = evt.getX();
    int y = evt.getY();
    addPointToFigure(getView().viewToDrawing(new Point(x, y)));
  }

  @Override
  public void draw(Graphics2D g) {
    if (createdFigure != null
        && anchor != null
        && mouseLocation != null
        && getView() == creationView) {
      g.setColor(Color.BLACK);
      g.setStroke(new BasicStroke(
          1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0f, new float[] {1f, 5f}, 0f));
      g.drawLine(anchor.x, anchor.y, mouseLocation.x, mouseLocation.y);
      if (!isWorking && createdFigure.isClosed() && createdFigure.getNodeCount() > 1) {
        Point p = creationView.drawingToView(createdFigure.getStartPoint());
        g.drawLine(mouseLocation.x, mouseLocation.y, p.x, p.y);
      }
    }
  }

  @Override
  public void mouseMoved(MouseEvent evt) {
    if (createdFigure != null && anchor != null && mouseLocation != null) {
      if (creationView != null && evt.getSource() == creationView.getComponent()) {
        Rectangle r = new Rectangle(anchor);
        r.add(mouseLocation);
        r.add(evt.getPoint());
        if (createdFigure.isClosed() && createdFigure.getNodeCount() > 0) {
          r.add(creationView.drawingToView(createdFigure.getStartPoint()));
        }
        r.grow(1, 1);
        fireAreaInvalidated(r);
        mouseLocation = evt.getPoint();
      }
    }
  }

  protected BezierPath calculateFittedCurve(BezierPath path) {
    if (calculateFittedCurveAfterCreation) {
      return Bezier.fitBezierPath(path, 1.5d / getView().getScaleFactor());
    } else {
      return path;
    }
  }

  public void setToolDoneAfterCreation(boolean b) {
    isToolDoneAfterCreation = b;
  }

  public boolean isToolDoneAfterCreation() {
    return isToolDoneAfterCreation;
  }

  @Override
  public CoordinateData getConstrainerCoordinates(int before, int after) {
    if (this.createdFigure == null) {
      return null;
    }
    List<Point2D.Double> list = new ArrayList<>();
    for (int idx = 0; idx < this.createdFigure.getNodeCount(); idx++) {
      list.add(this.createFigure().getPoint(idx));
    }
    return new CoordinateData(list.toArray(Point2D.Double[]::new), list.size());
  }
}
