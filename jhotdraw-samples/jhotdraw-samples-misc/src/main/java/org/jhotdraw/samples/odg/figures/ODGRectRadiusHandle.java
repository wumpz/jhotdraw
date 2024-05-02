/*
 * @(#)ODGRectRadiusHandle.java
 *
 * Copyright (c) 2007 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.odg.figures;

import static org.jhotdraw.draw.AttributeKeys.TRANSFORM;

import java.awt.*;
import java.awt.geom.*;
import java.util.logging.Logger;
import javax.swing.undo.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.handle.AbstractHandle;
import org.jhotdraw.geom.Dimension2DDouble;
import org.jhotdraw.undo.CompositeEdit;
import org.jhotdraw.util.*;

/** A Handle to manipulate the radius of a round lead rectangle. */
public class ODGRectRadiusHandle extends AbstractHandle {

  private static final int OFFSET = 6;
  private Dimension2DDouble originalArc2D;
  CompositeEdit edit;

  public ODGRectRadiusHandle(Figure owner) {
    super(owner);
  }

  /** Draws this handle. */
  @Override
  public void draw(Graphics2D g) {
    drawDiamond(g, Color.yellow, Color.black);
  }

  @Override
  protected Rectangle basicGetBounds() {
    Rectangle r = new Rectangle(locate());
    r.grow(getHandlesize() / 2 + 1, getHandlesize() / 2 + 1);
    return r;
  }

  private Point locate() {
    ODGRectFigure owner = (ODGRectFigure) getOwner();
    Rectangle2D.Double r = owner.getBounds();
    Point2D.Double p = new Point2D.Double(r.x + owner.getArcWidth(), r.y + owner.getArcHeight());
    if (owner.attr().get(TRANSFORM) != null) {
      owner.attr().get(TRANSFORM).transform(p, p);
    }
    return view.drawingToView(p);
  }

  @Override
  public void trackStart(Point anchor, int modifiersEx) {
    ODGRectFigure odgRect = (ODGRectFigure) getOwner();
    originalArc2D = odgRect.getArc();
  }

  @Override
  public void trackStep(Point anchor, Point lead, int modifiersEx) {
    ODGRectFigure odgRect = (ODGRectFigure) getOwner();
    odgRect.willChange();
    Point2D.Double p = view.viewToDrawing(lead);
    if (odgRect.attr().get(TRANSFORM) != null) {
      try {
        odgRect.attr().get(TRANSFORM).inverseTransform(p, p);
      } catch (NoninvertibleTransformException ex) {
        LOG.throwing(ODGRectRadiusHandle.class.getName(), "trackStep", ex);
      }
    }
    Rectangle2D.Double r = odgRect.getBounds();
    odgRect.setArc(p.x - r.x, p.y - r.y);
    odgRect.changed();
  }

  private static final Logger LOG = Logger.getLogger(ODGRectRadiusHandle.class.getName());

  @Override
  public void trackEnd(Point anchor, Point lead, int modifiersEx) {
    final ODGRectFigure odgRect = (ODGRectFigure) getOwner();
    final Dimension2DDouble oldValue = originalArc2D;
    final Dimension2DDouble newValue = odgRect.getArc();
    view.getDrawing().fireUndoableEditHappened(new AbstractUndoableEdit() {
      private static final long serialVersionUID = 1L;

      @Override
      public String getPresentationName() {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.samples.odg.Labels");
        return labels.getString("arc");
      }

      @Override
      public void undo() throws CannotUndoException {
        super.undo();
        odgRect.willChange();
        odgRect.setArc(oldValue);
        odgRect.changed();
      }

      @Override
      public void redo() throws CannotRedoException {
        super.redo();
        odgRect.willChange();
        odgRect.setArc(newValue);
        odgRect.changed();
      }
    });
  }

  @Override
  public String getToolTipText(Point p) {
    return ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels")
        .getString("handle.roundRectangleRadius.toolTipText");
  }
}
