/*
 * @(#)CreationTool.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.tool;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.jhotdraw.draw.figure.AbstractAttributedFigure;
import org.jhotdraw.draw.figure.CompositeFigure;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.utils.util.ResourceBundleUtil;

/**
 * A {@link Tool} to create a new figure by drawing its bounds. The figure to be created is
 * specified by a prototype.
 *
 * <p>To create a figure using the {@code CreationTool}, the user does the following mouse gestures
 * on a DrawingView:
 *
 * <ol>
 *   <li>Press the mouse button over the DrawingView. This defines the start point of the Figure
 *       bounds.
 *   <li>Drag the mouse while keeping the mouse button pressed, and then release the mouse button.
 *       This defines the end point of the Figure bounds.
 * </ol>
 *
 * The CreationTool works well with most figures that fit into a rectangular shape or that concist
 * of a single straight line. For figures that need additional editing after these mouse gestures,
 * the use of a specialized creation tool is recommended. For example the TextTool allows to enter
 * the text into a TextFigure after the user has performed the mouse gestures.
 *
 * <p>Alltough the mouse gestures might be fitting for the creation of a connection, the
 * CreationTool is not suited for the creation of a ConnectionFigure. Use the ConnectionTool for
 * this type of figures instead.
 *
 * <p><hr> <b>Design Patterns</b>
 *
 * <p><em>Prototype</em><br>
 * The creation tool creates new figures by cloning a prototype figure object. That's the reason why
 * {@code Figure} extends the {@code Cloneable} interface. <br>
 * Prototype: {@link Figure}; Client: {@link CreationTool}. <hr>
 */
public class CreationTool extends AbstractTool implements CoordinateDataSupplier {

  private static final long serialVersionUID = 1L;

  /**
   * Attributes to be applied to the created ConnectionFigure. These attributes override the default
   * attributes of the DrawingEditor.
   */
  protected Map<AttributeKey<?>, Object> prototypeAttributes;

  /** A localized name for this tool. The presentationName is displayed by the UndoableEdit. */
  protected String presentationName;

  /** Treshold for which we create a larger shape of a minimal size. */
  protected Dimension minimalSizeTreshold = new Dimension(2, 2);

  /** We set the figure to this minimal size, if it is smaller than the minimal size treshold. */
  protected Dimension minimalSize = new Dimension(40, 40);

  /** The prototype for new figures. */
  protected Figure prototype;

  /** The created figure. */
  protected Figure createdFigure;

  private boolean selectFigureAfterCreation = true;

  private boolean anchorIsCenterOfBoundingBox = false;

  /**
   * If this is set to false, the CreationTool does not fire toolDone after a new Figure has been
   * created. This allows to create multiple figures consecutively.
   */
  private boolean isToolDoneAfterCreation = true;

  public CreationTool(String prototypeClassName) {
    this(prototypeClassName, null, null);
  }

  public CreationTool(String prototypeClassName, Map<AttributeKey<?>, Object> attributes) {
    this(prototypeClassName, attributes, null);
  }

  public CreationTool(
      String prototypeClassName, Map<AttributeKey<?>, Object> attributes, String name) {
    try {
      this.prototype =
          (Figure) Class.forName(prototypeClassName).getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      InternalError error = new InternalError("Unable to create Figure from " + prototypeClassName);
      error.initCause(e);
      throw error;
    }
    this.prototypeAttributes = attributes;
    if (name == null) {
      ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
      name = labels.getString("edit.createFigure.text");
    }
    this.presentationName = name;
  }

  /**
   * Bounding box is now constructed around anchor as center and lead as path to one edge. The main calculation is
   * done within setFigureBounds to make more specific adaptions.
   */
  public CreationTool withAchorIsCenterOfBoundingBox(boolean flag) {
    this.anchorIsCenterOfBoundingBox = flag;
    return this;
  }

  /**
   * Creates a new instance with the specified prototype but without an attribute set. The
   * CreationTool clones this prototype each time a new Figure needs to be created. When a new
   * Figure is created, the CreationTool applies the default attributes from the DrawingEditor to
   * it.
   *
   * @param prototype The prototype used to create a new Figure.
   */
  public CreationTool(Figure prototype) {
    this(prototype, null, null);
  }

  /**
   * Creates a new instance with the specified prototype but without an attribute set. The
   * CreationTool clones this prototype each time a new Figure needs to be created. When a new
   * Figure is created, the CreationTool applies the default attributes from the DrawingEditor to
   * it, and then it applies the attributes to it, that have been supplied in this constructor.
   *
   * @param prototype The prototype used to create a new Figure.
   * @param attributes The CreationTool applies these attributes to the prototype after having
   *     applied the default attributes from the DrawingEditor.
   */
  public CreationTool(Figure prototype, Map<AttributeKey<?>, Object> attributes) {
    this(prototype, attributes, null);
  }

  /**
   * Creates a new instance with the specified prototype and attribute set.
   *
   * @param prototype The prototype used to create a new Figure.
   * @param attributes The CreationTool applies these attributes to the prototype after having
   *     applied the default attributes from the DrawingEditor.
   * @param name The name parameter is currently not used.
   * @deprecated This constructor might go away, because the name parameter is not used.
   */
  @Deprecated
  public CreationTool(Figure prototype, Map<AttributeKey<?>, Object> attributes, String name) {
    this.prototype = prototype;
    this.prototypeAttributes = attributes;
    if (name == null) {
      ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
      name = labels.getString("edit.createFigure.text");
    }
    this.presentationName = name;
  }

  public CreationTool withSelectFigureAfterCreation(boolean selectFigures) {
    this.selectFigureAfterCreation = selectFigures;
    return this;
  }

  public Figure getPrototype() {
    return prototype;
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
      if (createdFigure instanceof CompositeFigure) {
        ((CompositeFigure) createdFigure).layout(getView().getScaleFactor());
      }
      createdFigure = null;
    }
    if (getView().getConstrainer() != null
        && getView().getConstrainer() instanceof CoordinateDataReceiver receiver) {
      receiver.clearCoordinateSupplier();
    }
  }

  /**
   * Sets the bounding box for a figure. Here it should be possible to recalculate the new bounding box to
   * e.g. anchor is center and lead is distance center to one border point.
   * @param figure
   * @param anchor
   * @param lead
   */
  protected void setFigureBounds(Figure figure, Point2D.Double anchor, Point2D.Double lead) {
    if (anchorIsCenterOfBoundingBox) {
      Point2D.Double newAnchor =
          new Point2D.Double(anchor.x - (lead.x - anchor.x), anchor.y - (lead.y - anchor.y));
      figure.setBounds(newAnchor, lead);
    } else {
      figure.setBounds(anchor, lead);
    }
  }

  @Override
  public void mousePressed(MouseEvent evt) {
    super.mousePressed(evt);
    if (getView() == null) {
      return;
    }
    getView().clearSelection();
    createdFigure = createFigure();
    Point2D.Double p = constrainPoint(viewToDrawing(anchor), createdFigure);
    anchor.x = evt.getX();
    anchor.y = evt.getY();
    setFigureBounds(createdFigure, p, p);
    //    getDrawing().add(createdFigure);
    //    fireFigureCreated(createdFigure);
    fireAreaInvalidated(createdFigure.getDrawingArea());
  }

  @Override
  public void mouseDragged(MouseEvent evt) {
    if (createdFigure != null) {
      Point2D.Double p = constrainPoint(new Point(evt.getX(), evt.getY()), createdFigure);
      var areaBefore = createdFigure.getDrawingArea();
      createdFigure.willChange();
      setFigureBounds(
          createdFigure, constrainPoint(new Point(anchor.x, anchor.y), createdFigure), p);
      createdFigure.changed();
      var areaAfter = createdFigure.getDrawingArea();
      areaAfter.add(areaBefore);
      fireAreaInvalidated(areaAfter);
    }
  }

  @Override
  public void draw(Graphics2D g) {
    if (createdFigure != null) {
      g.setColor(Color.red);
      g.drawRect(anchor.x - 2, anchor.y - 2, 4, 4);

      Graphics2D gDrawing = (Graphics2D) g.create();
      try {
        gDrawing.transform(getView().getDrawingToViewTransform());
        createdFigure.draw(gDrawing);
      } catch (Exception ex) {
        LOG.log(Level.FINE, "unable to draw figure {0}", ex.toString());
      } finally {
        gDrawing.dispose();
      }
    }
  }

  private static final Logger LOG = Logger.getLogger(CreationTool.class.getName());

  @Override
  public void mouseReleased(MouseEvent evt) {
    if (createdFigure != null) {
      if (createdFigure instanceof AbstractAttributedFigure abstrFigure)
        abstrFigure.setDrawing(getDrawing());
      Rectangle2D.Double bounds = createdFigure.getBounds();
      if (bounds.width == 0 && bounds.height == 0) {
        getDrawing().remove(createdFigure);
        if (isToolDoneAfterCreation()) {
          fireToolDone();
        }
      } else {
        if (Math.abs(anchor.x - evt.getX()) < minimalSizeTreshold.width
            && Math.abs(anchor.y - evt.getY()) < minimalSizeTreshold.height) {
          createdFigure.willChange();
          setFigureBounds(
              createdFigure,
              constrainPoint(new Point(anchor.x, anchor.y), createdFigure),
              constrainPoint(
                  new Point(
                      anchor.x + (int) Math.max(bounds.width, minimalSize.width),
                      anchor.y + (int) Math.max(bounds.height, minimalSize.height)),
                  createdFigure));
          createdFigure.changed();
        }
        if (createdFigure instanceof CompositeFigure) {
          ((CompositeFigure) createdFigure).layout(getView().getScaleFactor());
        }

        processCreatedFigureBeforeAddingToDocument(createdFigure);

        final Figure addedFigure = createdFigure;
        final Drawing addedDrawing = getDrawing();

        // added to drawing
        getDrawing().add(createdFigure);
        fireFigureCreated(createdFigure);

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
            addedDrawing.add(addedFigure);
          }
        });
        //        Rectangle r = new Rectangle(anchor.x, anchor.y, 0, 0);
        //        r.add(evt.getX(), evt.getY());
        //        maybeFireBoundsInvalidated(r);
        creationFinished(createdFigure);
        createdFigure = null;
      }
    } else {
      if (isToolDoneAfterCreation()) {
        fireToolDone();
      }
    }
  }

  @SuppressWarnings("unchecked")
  protected Figure createFigure() {
    Figure f = prototype.clone();
    getEditor().applyDefaultAttributesTo(f);
    if (prototypeAttributes != null) {
      f.attr().setAttributes(prototypeAttributes);
    }
    return f;
  }

  protected Figure getCreatedFigure() {
    return createdFigure;
  }

  protected Figure getAddedFigure() {
    return createdFigure;
  }

  /**
   * This method allows subclasses to do perform additonal user interactions after the new figure
   * has been created. The implementation of this class just invokes fireToolDone.
   */
  protected void creationFinished(Figure createdFigure) {
    if (createdFigure.isSelectable() && selectFigureAfterCreation) {
      getView().addToSelection(createdFigure);
    }
    if (isToolDoneAfterCreation()) {
      fireToolDone();
    }
  }

  /**
   * If this is set to false, the CreationTool does not fire toolDone after a new Figure has been
   * created. This allows to create multiple figures consecutively.
   */
  public void setToolDoneAfterCreation(boolean newValue) {
    boolean oldValue = isToolDoneAfterCreation;
    isToolDoneAfterCreation = newValue;
  }

  /** Returns true, if this tool fires toolDone immediately after a new figure has been created. */
  public boolean isToolDoneAfterCreation() {
    return isToolDoneAfterCreation;
  }

  @Override
  public void updateCursor(DrawingView view, Point p) {
    if (view.isEnabled()) {
      view.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    } else {
      view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }
  }

  @Override
  public CoordinateData getConstrainerCoordinates(int before, int after) {
    if (this.createdFigure == null) {
      return null;
    }
    var b = this.createdFigure.getBounds();

    List<Point2D.Double> list = new ArrayList<>();
    list.add(new Point2D.Double(b.x, b.y));
    if (b.width != 0 || b.height != 0) {
      list.add(new Point2D.Double(b.x + b.width, b.y + b.height));
    }

    return new CoordinateData(list.toArray(Point2D.Double[]::new), list.size());
  }
}
