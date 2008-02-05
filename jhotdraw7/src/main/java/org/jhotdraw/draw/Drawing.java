/*
 * @(#)Drawing.java  3.0  2007-07-17
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
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
 * A drawing holds figures. It can draw its figures, and it can find
 * them on its drawing area.
 * <br>
 * A drawing notifies listeners when a figure is added or removed,
 * and when its drawing area needs to be repainted.
 * <p>
 * The drawing object is used by figure handles and editing tools
 * to fire undoable edit events. This way, undoable edit listeners only need to 
 * register on a drawing in order to receive all undoable edit
 * events related to a drawing.
 * <p>
 * A drawing can have a number of input formats and output formats,
 * allowing to load and save the drawing, and to copy and paste figures
 * into the clipboard.
 *
 * @author Werner Randelshofer
 * @version 3.0 2007-07-17 Refactored Drawing from an independent interface
 * into an interface that extends from CompositeFigure. 
 * <br>2.4 2007-05-21 Added add-methods with index to the interface.
 * <br>2.3 2007-05-16 Added method findFigureBehind. 
 * <br>2.2 2007-04-09 Methods setCanvasSize, getCanvasSize added.
 * <br>2.1 2006-12-31 Changed to return lists instead of collections.
 * <br>2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public interface Drawing extends CompositeFigure, Serializable, DOMStorable {
    /**
     * Adds a figure to the drawing.
     * The drawing sends an {@code addNotify} message to the figure
     * after it has been added.
     *
     * @see Figure#addNotify
     *
     * @param figure to be added to the drawing
     */
    public boolean add(Figure figure);
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
    public boolean remove(Figure figure);
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
     * @see #basicAdd(Figure)
     * 
     * @param figure that is part of the drawing and should be removed
     */
    public int basicRemove(Figure figure);
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
     * {@code basicAdd(size(), figure)}.
     * 
     * @param figure that is part of the drawing and should be removed
     * @see #basicRemove(Figure)
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
     * 
     * @param index The insertion index.
     * @param figures A collection of figures which are part of the drawing
     * and should be reinserted.
     * @see #basicRemoveAll(Collection)
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
    public List<Figure> getChildren();
    
    /**
     * Returns the number of figures in this drawing.
     */
    public int getChildCount();
    
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
     * Adds an input format to the drawing.
     */
    public void addInputFormat(InputFormat format);
    /**
     * Adds an output format to the drawing.
     */
    public void addOutputFormat(OutputFormat format);
    
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

