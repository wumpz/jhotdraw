/*
 * @(#)Drawing.java  2.4  2007-05-21
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

import org.jhotdraw.geom.*;
import org.jhotdraw.io.*;
import org.jhotdraw.xml.*;

import java.awt.Graphics2D;
import java.awt.font.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.undo.*;
import javax.swing.event.*;
import java.io.*;
/**
 * Drawing is a container for figures.
 * <p>
 * Drawing sends out DrawingChanged events to DrawingChangeListeners
 * whenever a part of its area was invalidated.
 *
 * @author Werner Randelshofer
 * @version 2.4 2007-05-21 Added add-methods with index to the interface.
 * <br>2.3 2007-05-16 Added method findFigureBehind. 
 * <br>2.2 2007-04-09 Methods setCanvasSize, getCanvasSize added.
 * <br>2.1 2006-12-31 Changed to return lists instead of collections.
 * <br>2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public interface Drawing extends Serializable, DOMStorable {
    /**
     * Removes all figures from the drawing.
     */
    public void clear();
    /**
     * Adds a figure to the drawing.
     * The drawing sends an {@code addNotify} message to the figure
     * after it has been added.
     *
     * @see Figure#addNotify
     *
     * @param figure to be added to the drawing
     */
    public void add(Figure figure);
    /**
     * Adds a figure to the drawing.
     * The drawing sends an {@code addNotify} message to the figure
     * after it has been added.
     *
     * @see Figure#addNotify
     *
     * @param index The z-index of the figure.
     * @param figure to be added to the drawing
     */
    public void add(int index, Figure figure);
    /**
     * Adds a collection of figures to the drawing.
     * The drawing sends an {@code addNotify}  message to each figure
     * after it has been added.
     *
     * @see Figure#addNotify
     *
     * @param figures to be added to the drawing
     */
    public void addAll(Collection<Figure> figures);
    /**
     * Adds a collection of figures to the drawing.
     * The drawing sends an {@code addNotify}  message to each figure
     * after it has been added.
     *
     * @see Figure#addNotify
     *
     * @param index The z-index of the figure.
     * @param figures to be added to the drawing
     */
    public void addAll(int index, Collection<Figure> figures);
    
    /**
     * Removes a figure from the drawing.
     * The drawing sends a {@code removeNotify} message to the figure
     * before it is removed.
     *
     * @see Figure#removeNotify
     *
     * @param figure that is part of the drawing and should be removed
     */
    public void remove(Figure figure);
    /**
     * Removes the specified figures from the drawing.
     * The drawing sends a {@code removeNotify}  message to each figure
     * before it is removed.
     *
     * @see Figure#removeNotify
     *
     * @param figures A collection of figures which are part of the drawing
     * and should be removed
     */
    public void removeAll(Collection<Figure> figures);
    
    /**
     * Removes a figure temporarily from the drawing.
     *
     * @see #basicAdd(Figure
     * 
     * @param figure that is part of the drawing and should be removed
     */
    public void basicRemove(Figure figure);
    /**
     * Removes the specified figures temporarily from the drawing.
     *
     * @see #basicAddAll(int, Collection)
     * @param figures A collection of figures which are part of the drawing
     * and should be removed
     */
    public void basicRemoveAll(Collection<Figure> figures);
    /**
     * Reinserts a figure which was temporarily removed using basicRemove.
     * <p>
     * This is a convenience method for calling 
     * {@code basicAdd(getFigureCount(), figure)}.
     *
     * @see #basicRemove(Figure)
     * @param index The z-index of the figure.
     * @param figure that is part of the drawing and should be removed
     */
    public void basicAdd(Figure figure);
    /**
     * Reinserts a figure which was temporarily removed using basicRemove.
     *
     * @see #basicRemove(Figure)
     * @param figure that is part of the drawing and should be removed
     */
    public void basicAdd(int index, Figure figure);
    
    /**
     * Returns the index of the specified figure.
     *
     * Returns -1 if the Figure is not directly contained in this Drawing, for
     * example if the Figure is a child of a CompositeFigure.
     */
    public int indexOf(Figure figure);
    
    /**
     * Reinserts the specified figures which were temporarily removed from
     * the drawing.
     *
     * @see #basicRemoveAll(Collection)
     * @param index The insertion index.
     * @param figures A collection of figures which are part of the drawing
     * and should be reinserted.
     */
    public void basicAddAll(int index, Collection<Figure> figures);
    
    /**
     * Draws all the figures from back to front.
     */
    void draw(Graphics2D g);
    
    /**
     * Draws only the figures in the supplied set.
     * /
     * void draw(Graphics2D g, ArrayList figures);
     */
    
    /**
     * Returns all figures that lie within or intersect the specified
     * bounds. The figures are returned in Z-order from back to front.
     */
    public List<Figure> findFigures(Rectangle2D.Double bounds);
    /**
     * Returns all figures that lie within the specified
     * bounds. The figures are returned in Z-order from back to front.
     */
    public List<Figure> findFiguresWithin(Rectangle2D.Double bounds);
    /**
     * Returns the figures of the drawing.
     * @return A Collection of Figure's.
     */
    public List<Figure> getFigures();
    
    /**
     * Returns the number of figures in this drawing.
     */
    public int getFigureCount();
    
    /**
     * Finds a top level Figure. Use this call for hit detection that
     * should not descend into the figure's children.
     */
    Figure findFigure(Point2D.Double p);
    
    /**
     * Finds a top level Figure. Use this call for hit detection that
     * should not descend into the figure's children.
     */
    Figure findFigureExcept(Point2D.Double p, Figure ignore);
    /**
     * Finds a top level Figure. Use this call for hit detection that
     * should not descend into the figure's children.
     */
    Figure findFigureExcept(Point2D.Double p, Collection<Figure> ignore);
    /**
     * Finds a top level Figure which is behind the specified Figure.
     */
    Figure findFigureBehind(Point2D.Double p, Figure figure);
    /**
     * Finds a top level Figure which is behind the specified Figures.
     */
    Figure findFigureBehind(Point2D.Double p, Collection<Figure> figures);
    
    /**
     * Returns true if this drawing contains the specified figure.
     */
    boolean contains(Figure f);
    
    /**
     * Returns a list of the figures in Z-Order from front to back.
     */
    public List<Figure> getFiguresFrontToBack();
    /**
     * Finds a figure but descends into a figure's
     * children. Use this method to implement <i>click-through</i>
     * hit detection, that is, you want to detect the inner most
     * figure containing the given point.
     */
    public Figure findFigureInside(Point2D.Double p);
    
    /**
     * Sends a figure to the back of the drawing.
     *
     * @param figure that is part of the drawing
     */
    public void sendToBack(Figure figure);
    
    /**
     * Brings a figure to the front.
     *
     * @param figure that is part of the drawing
     */
    public void bringToFront(Figure figure);
    
    /**
     * Returns a copy of the provided collection which is sorted
     * in z order from back to front.
     */
    public List<Figure> sort(Collection<Figure> figures);
    
    /**
     * Adds a listener for this drawing.
     */
    void addDrawingListener(DrawingListener listener);
    
    /**
     * Removes a listener from this drawing.
     */
    void removeDrawingListener(DrawingListener listener);
    /**
     * Adds a listener for undooable edit events.
     */
    public void addUndoableEditListener(UndoableEditListener l);
    
    /**
     * Removes a listener for undoable edit events.
     */
    public void removeUndoableEditListener(UndoableEditListener l);
    /**
     * Notify all listenerList that have registered interest for
     * notification on this event type.
     */
    public void fireUndoableEditHappened(UndoableEdit edit);
    
    /**
     * Returns the font render context used to do text leyout and text drawing.
     */
    public FontRenderContext getFontRenderContext();
    /**
     * Sets the font render context used to do text leyout and text drawing.
     */
    public void setFontRenderContext(FontRenderContext frc);
    
    /**
     * Returns the lock object on which all threads acting in Figures in this
     * drawing synchronize to prevent race conditions.
     */
    public Object getLock();
    
    /**
     * Sets input formats for the Drawing in order of preferred formats.
     * <p>
     * The input formats are used for loading the Drawing from a file and for
     * pasting Figures from the clipboard into the Drawing.
     */
    public void setInputFormats(List<InputFormat> formats);
    /**
     * Gets input formats for the Drawing in order of preferred formats.
     */
    public List<InputFormat> getInputFormats();
    /**
     * Sets output formats for the Drawing in order of preferred formats.
     * <p>
     * The output formats are used for saving the Drawing into a file and for
     * cutting and copying Figures from the Drawing into the clipboard.
     */
    public void setOutputFormats(List<OutputFormat> formats);
    /**
     * Gets output formats for the Drawing in order of preferred formats.
     */
    public List<OutputFormat> getOutputFormats();
    
    /**
     * Sets the canvas size for this drawing.
     * <p>
     * If <code>canvasSize</code> is </code>null</code>, the size of the canvas 
     * is expected to be adjusted dynamically to fit the drawing areas of all 
     * figures contained in the drawing.
     * <p>
     * This is a bound property.
     *
     * @param canvasSize The canvas size, or null.
     */
    public void setCanvasSize(Dimension2DDouble canvasSize);
    
    /**
     * Gets the canvas size of this drawing.
     * If null is returned, the canvas size needs to be adjusted dynamically
     * to fit the drawing areas of all figures contained in the drawing.
     *
     * @return The canvas size, or null.
     */
    public Dimension2DDouble getCanvasSize();
    
    // ATTRIBUTES
    /**
     * Sets an attribute of the Drawing without firing events.
     * AttributeKey name and semantics are defined by the class implementing
     * the Drawing interface.
     * <p>
     * Use <code>AttributeKey.set</code> for typesafe access to this 
     * method.
     * /
    public void setAttribute(AttributeKey key, Object value);
    /**
     * Gets an attribute from the Drawing.
     * <p>
     * Use <code>AttributeKey.get()</code> for typesafe access to this method.
     * 
     * @see AttributeKey#get
     *
     * @return Returns the attribute value. If the Drawing does not have an
     * attribute with the specified key, returns key.getDefaultValue().
     * / 
    public Object getAttribute(AttributeKey key);
    /**
     * Returns a view to all attributes of this drawing.
     * By convention, an unmodifiable map is returned.
     * /
    public Map<AttributeKey, Object> getAttributes();
    
    /**
     * Gets data which can be used to restore the attributes of the drawing 
     * after a setAttribute has been applied to it.
     * 
     * @see #basicSetAttribue(AttributeKey,Object)
     * /
    public Object getAttributesRestoreData();
    /**
     * Restores the attributes of the drawing to a previously stored state.
     * /
    public void restoreAttributesTo(Object restoreData);
    */
}

