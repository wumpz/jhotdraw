/*
 * @(#)DrawingView.java  4.2  2007-09-12
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */


package org.jhotdraw.draw;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.*;
import java.beans.*;
import javax.swing.*;
/**
 * A DrawingView paints a {@link Drawing} on a JComponent.
 * <p>
 * To support editing, a DrawingView can paint {@link Handle}s and
 * the current {@link Tool} of the {@link DrawingEditor} on top of the
 * drawing. It can render a {@link Constrainer} below the drawing.
 * <p>
 * Tools can register mouse and key listeners on the DrawingView.
 * <p>
 * A DrawingView can paint the drawing with a scale factor. It supports
 * conversion between scaled view coordinates and drawing coordinates.
 *
 * 
 * @author Werner Randelshofer
 * @version 4.2 2007-09-12 The DrawingView is now responsible for
 * holding the Constrainer objects which affect editing on this view.
 * <br>4.1 2007-05-15 getSelectedFigures returns a Set instead of a
 * Collection.
 * <br>4.0 2006-12-03 Replaced operation getContainer by getComponent. 
 * <br>3.1 2006-03-15 Support for enabled state added.
 * <br>3.0 2006-02-20 Changed to share a single DrawingEditor by multiple 
 * views.
 * <br>2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public interface DrawingView {
    /**
     * This constant is used to identify the drawing property of the DrawingView.
     */
    public final static String PROP_DRAWING = "drawing";
    /**
     * This constant is used to identify the cursor property of the DrawingView.
     */
    public final static String PROP_CURSOR = "cursor";
    /**
     * This constant is used to identify the constrainer property of the DrawingView.
     */
    public final static String PROP_CONSTRAINER = "constrainer";
    /**
     * This constant is used to identify the visible constrainer property of the DrawingView.
     */
    public final static String PROP_VISIBLE_CONSTRAINER = "visibleConstrainer";
    /**
     * This constant is used to identify the invisible constrainer property of the DrawingView.
     */
    public final static String PROP_INVISIBLE_CONSTRAINER = "invisibleConstrainer";
    /**
     * This constant is used to identify the constrainer visible property of the DrawingView.
     */
    public final static String PROP_CONSTRAINER_VISIBLE = "constrainerVisible";
    /**
     * This constant is used to identify the scale factor property of the DrawingView.
     */
    public final static String PROP_SCALE_FACTOR = "scaleFactor";
    /**
     * This constant is used to identify the handle detail level property of the DrawingView.
     */
    public final static String PROP_HANDLE_DETAIL_LEVEL = "handleDetailLevel";
    /**
     * This constant is used to identify the enabled property of the DrawingView.
     */
    public final static String PROP_ENABLED = "enabled";
    
    /**
     * Gets the drawing.
     * This is a bound property.
     */
    public Drawing getDrawing();
    
    /**
     * Sets and installs another drawing in the view.
     * This is a bound property.
     */
    public void setDrawing(Drawing d);
    
    /**
     * Sets the cursor of the DrawingView.
     * This is a bound property.
     */
    public void setCursor(Cursor c);
    
    /**
     * Test whether a given figure is selected.
     */
    public boolean isFigureSelected(Figure checkFigure);
    
    /**
     * Adds a figure to the current selection.
     */
    public void addToSelection(Figure figure);
    
    /**
     * Adds a collection of figures to the current selection.
     */
    public void addToSelection(Collection<Figure> figures);
    
    /**
     * Removes a figure from the selection.
     */
    public void removeFromSelection(Figure figure);
    
    /**
     * If a figure isn't selected it is added to the selection.
     * Otherwise it is removed from the selection.
     */
    public void toggleSelection(Figure figure);
    
    /**
     * Clears the current selection.
     */
    public void clearSelection();
    /**
     * Selects all figures.
     */
    public void selectAll();
    
    /**
     * Gets the selected figures. Returns an empty set, if no figures are selected. 
     */
    public Set<Figure> getSelectedFigures();
    /**
     * Gets the number of selected figures.
     */
    public int getSelectionCount();
    
    /**
     * Finds a handle at the given coordinates.
     * @return A handle, null if no handle is found.
     */
    public Handle findHandle(Point p);
    
    /**
     * Gets compatible handles.
     * @return A collection containing the handle and all compatible handles.
     */
    public Collection<Handle> getCompatibleHandles(Handle handle);
    /**
     * Finds a figure at the given point.
     * @return A figure, null if no figure is found.
     */
    public Figure findFigure(Point p);
    /**
     * Returns all figures that lie within or intersect the specified
     * bounds. The figures are returned in Z-order from back to front.
     */
    public Collection<Figure> findFigures(Rectangle r);
    /**
     * Returns all figures that lie within the specified
     * bounds. The figures are returned in Z-order from back to front.
     */
    public Collection<Figure> findFiguresWithin(Rectangle r);
    
    /**
     * Informs the view that it has been added to the specified editor.
     * The view must draw the tool of the editor, if getActiveView() of the
     * editor returns the view.
     */
    public void addNotify(DrawingEditor editor);
    /**
     * Informs the view that it has been removed from the specified editor.
     * The view must not draw the tool from the editor anymore.
     */
    public void removeNotify(DrawingEditor editor);
    
    void addMouseListener(MouseListener l);
    void removeMouseListener(MouseListener l);
    void addKeyListener(KeyListener l);
    void removeKeyListener(KeyListener l);
    void addMouseMotionListener(MouseMotionListener l);
    void removeMouseMotionListener(MouseMotionListener l);
    /**
     * Add a listener for selection changes in this DrawingView.
     * @param fsl jhotdraw.framework.FigureSelectionListener
     */
    public void addFigureSelectionListener(FigureSelectionListener fsl);
    
    /**
     * Remove a listener for selection changes in this DrawingView.
     * @param fsl jhotdraw.framework.FigureSelectionListener
     */
    public void removeFigureSelectionListener(FigureSelectionListener fsl);
    
    public void requestFocus();
    
    /**
     * Converts drawing coordinates to view coordinates.
     */
    public Point drawingToView(Point2D.Double p);
    /**
     * Converts view coordinates to drawing coordinates.
     */
    public Point2D.Double viewToDrawing(Point p);
    /**
     * Converts drawing coordinates to view coordinates.
     */
    public Rectangle drawingToView(Rectangle2D.Double p);
    /**
     * Converts view coordinates to drawing coordinates.
     */
    public Rectangle2D.Double viewToDrawing(Rectangle p);
    
    /**
     * Gets the current constrainer of this view. 
     * If isConstrainerVisible is true, this method returns getVisibleConstrainer,
     * otherwise it returns getInvisibleConstrainer.
     * This is a bound property.
     */
    public Constrainer getConstrainer();
    /**
     * Sets the editor's constrainer for this view, for use, when the
     * visible constrainer is turned on.
     * This is a bound property.
     */
    public void setVisibleConstrainer(Constrainer constrainer);
    /**
     * Gets the editor's constrainer for this view, for use, when the
     * visible constrainer is turned on.
     * This is a bound property.
     */
    public Constrainer getVisibleConstrainer();
    /**
     * Sets the editor's constrainer for this view, for use, when the
     * visible constrainer is turned off.
     * This is a bound property.
     */
    public void setInvisibleConstrainer(Constrainer constrainer);
    /**
     * Gets the editor's constrainer for this view, for use, when the
     * visible constrainer is turned off.
     * This is a bound property.
     */
    public Constrainer getInvisibleConstrainer();

    /**
     * Changes between a visible Constrainer and an invisible Constrainer.
     * This is a bound property.
     */
    public void setConstrainerVisible(boolean newValue);
    
    /**
     * Returns true, if the visible Constrainer is in use, returns false,
     * if the invisible Constrainer is in use.
     * This is a bound property.
     */
    public boolean isConstrainerVisible();
    
    /**
     * Returns the JComponent of the drawing view.
     */
    public JComponent getComponent();
    
    /**
     * Gets an transform which can be used to convert
     * drawing coordinates to view coordinates.
     */
    public AffineTransform getDrawingToViewTransform();
    
    /**
     * Gets the scale factor of the drawing view.
     * This is a bound property.
     */
    public double getScaleFactor();
    /**
     * Sets the scale factor of the drawing view.
     * This is a bound property.
     */
    public void setScaleFactor(double newValue);
    
    /**
     * The detail level of the handles.
     * This is a bound property.
     */
    public void setHandleDetailLevel(int newValue);
    /**
     * Returns the detail level of the handles.
     * This is a bound property.
     */
    public int getHandleDetailLevel();
     /**
      * Sets the enabled state of the drawing view.
      * This is a bound property.
      */
     public void setEnabled(boolean newValue);
     /**
      * Gets the enabled state of the drawing view.
     * This is a bound property.
      */
     public boolean isEnabled();
     
     public void addPropertyChangeListener(PropertyChangeListener listener);
     public void removePropertyChangeListener(PropertyChangeListener listener);
}
