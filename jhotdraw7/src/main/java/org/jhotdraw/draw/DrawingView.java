/*
 * @(#)DrawingView.java  3.1  2006-03-15
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
�
 */


package org.jhotdraw.draw;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.*;
import java.beans.*;
/**
 * DrawingView renders a Drawing and listens to its changes.
 * It receives user input and forwards it to registered listeners.
 *
 * @author Werner Randelshofer
 * @version 3.1 2006-03-15 Support for enabled state added.
 * <br>3.0 2006-02-20 Changed to share a single DrawingEditor by multiple 
 * views.
 * <br>2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public interface DrawingView {
    /**
     * Gets the tools.
     */
    public Set getTools();
    
    /**
     * Gets the drawing.
     */
    public Drawing getDrawing();
    
    /**
     * Sets and installs another drawing in the view.
     */
    public void setDrawing(Drawing d);
    
    /**
     * Sets the cursor of the DrawingView
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
     * Gets the current selection as a FigureSelection. A FigureSelection
     * can be cut, copied, pasted.
     */
    public Collection<Figure> getSelectedFigures();
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
     * Sets the editor's constrainer.
     */
    void setConstrainer(Constrainer constrainer);
    /**
     * Gets the editor's constrainer.
     */
    public Constrainer getConstrainer();
    /**
     * Returns the container of the drawing view.
     */
    public Container getContainer();
    
    /**
     * Gets an transform which can be used to convert
     * drawing coordinates to view coordinates.
     */
    public AffineTransform getDrawingToViewTransform();
    
    /**
     * Gets the scale factor of the drawing view.
     */
    public double getScaleFactor();
    /**
     * Sets the scale factor of the drawing view.
     * This is a bound property.
     */
    public void setScaleFactor(double newValue);
    
    /**
     * The detail level of the handles.
     */
    public void setHandleDetailLevel(int newValue);
    /**
     * Returns the detail level of the handles.
     */
    public int getHandleDetailLevel();
     /**
      * Sets the enabled state of the drawing view.
      * This is a bound property.
      */
     public void setEnabled(boolean newValue);
     /**
      * Gets the enabled state of the drawing view.
      */
     public boolean isEnabled();
     
     public void addPropertyChangeListener(PropertyChangeListener listener);
     public void removePropertyChangeListener(PropertyChangeListener listener);
}
