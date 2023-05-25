/*
 * @(#)GraphicalCompositeFigure.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.figure;

import static org.jhotdraw.draw.AttributeKeys.*;
import static org.jhotdraw.draw.AttributeKeys.StrokePlacement.CENTER;
import static org.jhotdraw.draw.AttributeKeys.StrokePlacement.INSIDE;
import static org.jhotdraw.draw.AttributeKeys.StrokePlacement.OUTSIDE;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.event.FigureEvent;
import org.jhotdraw.draw.event.FigureListenerAdapter;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.MoveHandle;
import org.jhotdraw.geom.Geom;

/**
 * The GraphicalCompositeFigure fills in the gap between a CompositeFigure and other figures which
 * mainly have a presentation purpose. The GraphicalCompositeFigure can be configured with any
 * Figure which takes over the task for rendering the graphical presentation for a CompositeFigure.
 * Therefore, the GraphicalCompositeFigure manages contained figures like a CompositeFigure does,
 * but delegates its graphical presentation to another (graphical) figure which purpose it is to
 * draw the container for all contained figures.
 *
 * <p>The GraphicalCompositeFigure adds to the {@link CompositeFigure} by containing a presentation
 * figure by default which can not be removed. Normally, the {@code CompositeFigure} can not be seen
 * without containing a figure because it has no mechanism to draw itself. It instead relies on its
 * contained figures to draw themselves thereby giving the {@code CompositeFigure} its appearance.
 * However, the <b>GraphicalCompositeFigure</b>'s presentation figure can draw itself even when the
 * <b>GraphicalCompositeFigure</b> contains no other figures. The <b>GraphicalCompositeFigure</b>
 * also uses a {@link org.jhotdraw.draw.layouter.Layouter} to lay out its child figures.
 *
 * @author Wolfram Kaiser (original code), Werner Randelshofer (this derived version)
 * @version $Id$
 */
public class GraphicalCompositeFigure extends AbstractAttributedCompositeFigure {

  private static final long serialVersionUID = 1L;

  /**
   * Figure which performs all presentation tasks for this BasicCompositeFigure as CompositeFigures
   * usually don't have an own presentation but present only the sum of all its children.
   */
  private Figure presentationFigure;
  /** Handles figure changes in the children. */
  private PresentationFigureHandler presentationFigureHandler = new PresentationFigureHandler(this);

  private static class PresentationFigureHandler extends FigureListenerAdapter
      implements UndoableEditListener, Serializable {

    private static final long serialVersionUID = 1L;
    private GraphicalCompositeFigure owner;

    private PresentationFigureHandler(GraphicalCompositeFigure owner) {
      this.owner = owner;
    }

    @Override
    public void figureRequestRemove(FigureEvent e) {
      owner.remove(e.getFigure());
    }

    @Override
    public void figureChanged(FigureEvent e) {
      if (!owner.isChanging()) {
        owner.willChange();
        owner.fireFigureChanged(e);
        owner.changed();
      }
    }

    @Override
    public void areaInvalidated(FigureEvent e) {
      if (!owner.isChanging()) {
        owner.fireAreaInvalidated(e.getInvalidatedArea());
      }
    }

    @Override
    public void undoableEditHappened(UndoableEditEvent e) {
      owner.fireUndoableEditHappened(e.getEdit());
    }
  }
  ;

  /**
   * Default constructor which uses nothing as presentation figure. This constructor is needed by
   * the Storable mechanism.
   */
  public GraphicalCompositeFigure() {
    this(null);
  }

  /**
   * Constructor which creates a GraphicalCompositeFigure with a given graphical figure for
   * presenting it.
   *
   * @param newPresentationFigure figure which renders the container
   */
  public GraphicalCompositeFigure(Figure newPresentationFigure) {
    super();
    setPresentationFigure(newPresentationFigure);
  }

  /**
   * Return the logcal display area. This method is delegated to the encapsulated presentation
   * figure.
   */
  @Override
  public Rectangle2D.Double getBounds() {
    if (getPresentationFigure() == null) {
      return super.getBounds();
    }
    return getPresentationFigure().getBounds();
  }

  @Override
  public boolean contains(Point2D.Double p, double scaleDenominator) {
    boolean contains = super.contains(p, scaleDenominator);
    if (!contains && getPresentationFigure() != null) {
      contains = getPresentationFigure().contains(p, scaleDenominator);
    }
    return contains;
  }

  @Override
  public void addNotify(Drawing drawing) {
    super.addNotify(drawing);
    if (getPresentationFigure() != null) {
      getPresentationFigure().addNotify(drawing);
    }
  }

  @Override
  public void removeNotify(Drawing drawing) {
    super.removeNotify(drawing);
    if (getPresentationFigure() != null) {
      getPresentationFigure().removeNotify(drawing);
    }
  }

  /** Return the draw area. This method is delegated to the encapsulated presentation figure. */
  @Override
  public Rectangle2D.Double getDrawingArea() {
    Rectangle2D.Double r = super.getDrawingArea();
    if (getPresentationFigure() != null) {
      r.add(getPresentationFigure().getDrawingArea());
    }
    return r;
  }

  /**
   * Moves the figure. This is the method that subclassers override. Clients usually call
   * displayBox.
   */
  @Override
  public void setBounds(Point2D.Double anchor, Point2D.Double lead) {
    if (getLayouter() == null) {
      super.setBounds(anchor, lead);
      basicSetPresentationFigureBounds(anchor, lead);
    } else {
      Rectangle2D.Double r = getLayouter().layout(this, anchor, lead);
      basicSetPresentationFigureBounds(
          new Point2D.Double(r.getX(), r.getY()),
          new Point2D.Double(
              Math.max(lead.x, (int) r.getMaxX()), Math.max(lead.y, (int) r.getMaxY())));
      invalidate();
    }
  }

  protected void superBasicSetBounds(Point2D.Double anchor, Point2D.Double lead) {
    super.setBounds(anchor, lead);
  }

  protected void basicSetPresentationFigureBounds(Point2D.Double anchor, Point2D.Double lead) {
    if (getPresentationFigure() != null) {
      getPresentationFigure().setBounds(anchor, lead);
    }
  }

  /**
   * Standard presentation method which is delegated to the encapsulated presentation figure. The
   * presentation figure is moved as well as all contained figures.
   */
  @Override
  public void transform(AffineTransform tx) {
    super.transform(tx);
    if (getPresentationFigure() != null) {
      getPresentationFigure().transform(tx);
    }
  }

  /** Draw the figure. This method is delegated to the encapsulated presentation figure. */
  @Override
  public void draw(Graphics2D g) {
    drawPresentationFigure(g);
    super.draw(g);
  }

  protected void drawPresentationFigure(Graphics2D g) {
    if (getPresentationFigure() != null) {
      getPresentationFigure().draw(g);
    }
  }

  /** Return default handles from the presentation figure. */
  @Override
  public Collection<Handle> createHandles(int detailLevel) {
    List<Handle> handles = new ArrayList<>();
    if (detailLevel == 0) {
      MoveHandle.addMoveHandles(this, handles);
    }
    return handles;
    // return getPresentationFigure().getHandles();
  }

  /**
   * Set a figure which renders this BasicCompositeFigure. The presentation tasks for the
   * BasicCompositeFigure are delegated to this presentation figure.
   *
   * @param newPresentationFigure figure takes over the presentation tasks
   */
  public void setPresentationFigure(Figure newPresentationFigure) {
    if (this.presentationFigure != null) {
      this.presentationFigure.removeFigureListener(presentationFigureHandler);
      if (getDrawing() != null) {
        this.presentationFigure.removeNotify(getDrawing());
      }
    }
    this.presentationFigure = newPresentationFigure;
    if (this.presentationFigure != null) {
      this.presentationFigure.addFigureListener(presentationFigureHandler);
      if (getDrawing() != null) {
        this.presentationFigure.addNotify(getDrawing());
      }
    }
    // FIXME: We should calculate the layout here.
  }

  /**
   * Get a figure which renders this BasicCompositeFigure. The presentation tasks for the
   * BasicCompositeFigure are delegated to this presentation figure.
   *
   * @return figure takes over the presentation tasks
   */
  public Figure getPresentationFigure() {
    return presentationFigure;
  }

  @Override
  @SuppressWarnings("unchecked")
  public GraphicalCompositeFigure clone() {
    GraphicalCompositeFigure that = (GraphicalCompositeFigure) super.clone();
    that.presentationFigure =
        (this.presentationFigure == null) ? null : this.presentationFigure.clone();
    if (that.presentationFigure != null) {
      that.presentationFigure.addFigureListener(that.presentationFigureHandler);
    }
    return that;
  }

  public void remap(HashMap<Figure, Figure> oldToNew, boolean disconnectIfNotInMap) {
    super.remap(oldToNew, disconnectIfNotInMap);
    if (presentationFigure != null) {
      presentationFigure.remap(oldToNew, disconnectIfNotInMap);
    }
  }

  /**
   * Sets an attribute of the figure. AttributeKey name and semantics are defined by the class
   * implementing the figure interface.
   */
  //  @Override
  //  public <T> void set(AttributeKey<T> key, T newValue) {
  //    if (forbiddenAttributes == null || !forbiddenAttributes.contains(key)) {
  //      if (getPresentationFigure() != null) {
  //        getPresentationFigure().set(key, newValue);
  //      }
  //      T oldValue = key.put(attributes, newValue);
  //      fireAttributeChanged(key, oldValue, newValue);
  //    }
  //  }
  //
  //  public void setAttributeEnabled(AttributeKey<?> key, boolean b) {
  //    if (forbiddenAttributes == null) {
  //      forbiddenAttributes = new HashSet<>();
  //    }
  //    if (b) {
  //      forbiddenAttributes.remove(key);
  //    } else {
  //      forbiddenAttributes.add(key);
  //    }
  //  }
  //
  //  /** Gets an attribute from the figure. */
  //  @Override
  //  public <T> T get(AttributeKey<T> key) {
  //    if (getPresentationFigure() != null) {
  //      return getPresentationFigure().get(key);
  //    } else {
  //      return (!attributes.containsKey(key)) ? key.getDefaultValue() : key.get(attributes);
  //    }
  //  }
  //
  //  /** Applies all attributes of this figure to that figure. */
  //  @SuppressWarnings("unchecked")
  //  protected void applyAttributesTo(Figure that) {
  //    for (Map.Entry<AttributeKey<?>, Object> entry : attributes.entrySet()) {
  //      that.set((AttributeKey<Object>) entry.getKey(), entry.getValue());
  //    }
  //  }
  //
  //  protected AttributeKey<?> getAttributeKey(String name) {
  //    return AttributeKeys.SUPPORTED_ATTRIBUTES_MAP.get(name);
  //  }
  //
  //  @Override
  //  public Map<AttributeKey<?>, Object> getAttributes() {
  //    return new HashMap<>(attributes);
  //  }

  /**
   * This is a default implementation that chops the point at the rectangle returned by getBounds()
   * of the figure.
   *
   * <p>Figures which have a non-rectangular shape need to override this method.
   *
   * <p>This method takes the following attributes into account: AttributeKeys.STROKE_COLOR,
   * AttributeKeys.STROKE_PLACEMENT, and AttributeKeys.StrokeTotalWidth.
   */
  public Point2D.Double chop(Point2D.Double from) {
    Rectangle2D.Double r = getBounds();
    if (attr().get(STROKE_COLOR) != null) {
      double grow;
      switch (attr().get(STROKE_PLACEMENT)) {
        case CENTER:
        default:
          grow = AttributeKeys.getStrokeTotalWidth(this, 1.0);
          break;
        case OUTSIDE:
          grow = AttributeKeys.getStrokeTotalWidth(this, 1.0);
          break;
        case INSIDE:
          grow = 0d;
          break;
      }
      Geom.grow(r, grow, grow);
    }
    return Geom.angleToPoint(r, Geom.pointToAngle(r, from));
  }
}
