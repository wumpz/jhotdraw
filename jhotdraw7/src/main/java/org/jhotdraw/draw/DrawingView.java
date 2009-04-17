/*
 * @(#)DrawingView.java  6.0  2009-04-18
 *
 * Copyright (c) 1996-2009 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
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
 * <p>
 * Design pattern:<br>
 * Name: Mediator.<br>
 * Role: Colleague.<br>
 * Partners: {@link DrawingEditor} as Mediator, {@link Tool} as
 * Colleague.
 * <p>
 * Design pattern:<br>
 * Name: Model-View-Controller.<br>
 * Role: View.<br>
 * Partners: {@link Tool} as Controller, {@link Figure} as Model.
 * <p>
 * Design pattern:<br>
 * Name: Observer.<br>
 * Role: Subject.<br>
 * Partners: {@link FigureSelectionListener} as Observer.
 *
 * 
 * @author Werner Randelshofer
 * @version 6.0 2009-04-18 Added method repaintHandles.
 * <br>5.0 2008-05-11 Added methods setEditor, getEditor and setActiveHandle,
 * getActiveHandle. 
 * <br>4.3 2007-12-25 Renamed property names from PROP_… to …_PROPERTY. 
 * <br>4.2 2007-09-12 The DrawingView is now responsible for
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
    public final static String DRAWING_PROPERTY = "drawing";
    /**
     * This constant is used to identify the cursor property of the DrawingView.
     */
    public final static String CURSOR_PROPERTY = "cursor";
    /**
     * This constant is used to identify the constrainer property of the DrawingView.
     */
    public final static String CONSTRAINER_PROPERTY = "constrainer";
    /**
     * This constant is used to identify the visible constrainer property of the DrawingView.
     */
    public final static String VISIBLE_CONSTRAINER_PROPERTY = "visibleConstrainer";
    /**
     * This constant is used to identify the invisible constrainer property of the DrawingView.
     */
    public final static String INVISIBLE_CONSTRAINER_PROPERTY = "invisibleConstrainer";
    /**
     * This constant is used to identify the constrainer visible property of the DrawingView.
     */
    public final static String CONSTRAINER_VISIBLE_PROPERTY = "constrainerVisible";
    /**
     * This constant is used to identify the scale factor property of the DrawingView.
     */
    public final static String SCALE_FACTOR_PROPERTY = "scaleFactor";
    /**
     * This constant is used to identify the handle detail level property of the DrawingView.
     */
    public final static String HANDLE_DETAIL_LEVEL_PROPERTY = "handleDetailLevel";
    /**
     * This constant is used to identify the enabled property of the DrawingView.
     */
    public final static String ENABLED_PROPERTY = "enabled";
    /**
     * This constant is used to identify the activeHandle property of the DrawingView.
     */
    public final static String ACTIVE_HANDLE_PROPERTY = "activeHandle";

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
     * Sets the active handle.
     */
    public void setActiveHandle(Handle newValue);
    /**
     * Gets the active handle.
     */
    public Handle getActiveHandle();
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

    /**
     * Gets the drawing editor associated to the DrawingView.
     * This is a bound property.
     */
    public DrawingEditor getEditor();

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

    /** Repaints the handles of the view. */
    public void repaintHandles();

    /**
     * Adds a property change listener to the drawing view.
     * 
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener);


    /**
     * Removes a property change listener to the drawing view.
     * 
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener);


    /**
     * Adds a mouse listener to the drawing view.
     * 
     * @param l the listener.
     */
    public void addMouseListener(MouseListener l);

    /**
     * Removes a mouse listener to the drawing view.
     * 
     * @param l the listener.
     */
    public void removeMouseListener(MouseListener l);


    /**
     * Adds a key listener to the drawing view.
     * 
     * @param l the listener.
     */
    public void addKeyListener(KeyListener l);

    /**
     * Removes a key listener to the drawing view.
     * 
     * @param l the listener.
     */
    public void removeKeyListener(KeyListener l);

    /**
     * Adds a mouse motion  listener to the drawing view.
     * 
     * @param l the listener.
     */
    public void addMouseMotionListener(MouseMotionListener l);

    /**
     * Removes a mouse motion listener to the drawing view.
     * 
     * @param l the listener.
     */
    public void removeMouseMotionListener(MouseMotionListener l);
}
