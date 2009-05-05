/*
 * @(#)Drawing.java  3.2  2009-05-05
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
 * @version 3.2 2009-05-15 Methods taking figure collections as parameters
 * now take collections of any extensions of figures as parameters.
 * <br>3.1 2009-04-15 Factored canvasSize out into an attribute.
 * <br>3.0 2007-07-17 Refactored Drawing from an independent interface
 * into an interface that extends from CompositeFigure. 
 * <br>2.4 2007-05-21 Added add-methods with index to the interface.
 * <br>2.3 2007-05-16 Added method findFigureBehind. 
 * <br>2.2 2007-04-09 Methods setCanvasSize, getCanvasSize added.
 * <br>2.1 2006-12-31 Changed to return lists instead of collections.
 * <br>2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public interface Drawing extends CompositeFigure, Serializable, DOMStorable {
    public final static String CANVAS_SIZE_PROPERTY="canvasSize";
    /**
     * Adds a figure to the drawing.
     * The drawing sends an {@code addNotify} message to the figure
     * after it has been added.
     *
     * @see Figure#addNotify
     *
     * @param figure to be added to the drawing
     */
    boolean add(Figure figure);
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
    void add(int index, Figure figure);
    /**
     * Adds a collection of figures to the drawing.
     * The drawing sends an {@code addNotify}  message to each figure
     * after it has been added.
     *
     * @see Figure#addNotify
     *
     * @param figures to be added to the drawing
     */
    void addAll(Collection<? extends Figure> figures);
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
    void addAll(int index, Collection<? extends Figure> figures);
    
    /**
     * Removes a figure from the drawing.
     * The drawing sends a {@code removeNotify} message to the figure
     * before it is removed.
     *
     * @see Figure#removeNotify
     *
     * @param figure that is part of the drawing and should be removed
     */
    boolean remove(Figure figure);
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
    void removeAll(Collection<? extends Figure> figures);
    
    /**
     * Removes a figure temporarily from the drawing.
     *
     * @see #basicAdd(Figure)
     * 
     * @param figure that is part of the drawing and should be removed
     */
    int basicRemove(Figure figure);
    /**
     * Removes the specified figures temporarily from the drawing.
     *
     * @see #basicAddAll(int, Collection)
     * @param figures A collection of figures which are part of the drawing
     * and should be removed
     */
    void basicRemoveAll(Collection<? extends Figure> figures);
    /**
     * Reinserts a figure which was temporarily removed using basicRemove.
     * <p>
     * This is a convenience method for calling 
     * {@code basicAdd(size(), figure)}.
     * 
     * @param figure that is part of the drawing and should be removed
     * @see #basicRemove(Figure)
     */
    void basicAdd(Figure figure);
    /**
     * Reinserts a figure which was temporarily removed using basicRemove.
     *
     * @see #basicRemove(Figure)
     * @param figure that is part of the drawing and should be removed
     */
    void basicAdd(int index, Figure figure);
    
    /**
     * Returns the index of the specified figure.
     *
     * Returns -1 if the Figure is not directly contained in this Drawing, for
     * example if the Figure is a child of a CompositeFigure.
     */
    int indexOf(Figure figure);
    
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
    void basicAddAll(int index, Collection<? extends Figure> figures);
    
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
    List<Figure> findFigures(Rectangle2D.Double bounds);
    /**
     * Returns all figures that lie within the specified
     * bounds. The figures are returned in Z-order from back to front.
     */
    List<Figure> findFiguresWithin(Rectangle2D.Double bounds);
    /**
     * Returns the figures of the drawing.
     * @return A Collection of Figure's.
     */
    List<Figure> getChildren();
    
    /**
     * Returns the number of figures in this drawing.
     */
    int getChildCount();
    
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
    Figure findFigureExcept(Point2D.Double p, Collection<? extends Figure> ignore);
    /**
     * Finds a top level Figure which is behind the specified Figure.
     */
    Figure findFigureBehind(Point2D.Double p, Figure figure);
    /**
     * Finds a top level Figure which is behind the specified Figures.
     */
    Figure findFigureBehind(Point2D.Double p, Collection<? extends Figure> figures);
    
    
    /**
     * Returns a list of the figures in Z-Order from front to back.
     */
    List<Figure> getFiguresFrontToBack();
    /**
     * Finds a figure but descends into a figure's
     * children. Use this method to implement <i>click-through</i>
     * hit detection, that is, you want to detect the inner most
     * figure containing the given point.
     */
    Figure findFigureInside(Point2D.Double p);
    
    /**
     * Sends a figure to the back of the drawing.
     *
     * @param figure that is part of the drawing
     */
    void sendToBack(Figure figure);
    
    /**
     * Brings a figure to the front.
     *
     * @param figure that is part of the drawing
     */
    void bringToFront(Figure figure);
    
    /**
     * Returns a copy of the provided collection which is sorted
     * in z order from back to front.
     */
    List<Figure> sort(Collection<? extends Figure> figures);
    
    /**
     * Adds a listener for undooable edit events.
     */
    void addUndoableEditListener(UndoableEditListener l);
    
    /**
     * Removes a listener for undoable edit events.
     */
    void removeUndoableEditListener(UndoableEditListener l);
    /**
     * Notify all listenerList that have registered interest for
     * notification on this event type.
     */
    void fireUndoableEditHappened(UndoableEdit edit);
    
    /**
     * Returns the font render context used to do text leyout and text drawing.
     */
    FontRenderContext getFontRenderContext();
    /**
     * Sets the font render context used to do text leyout and text drawing.
     */
    void setFontRenderContext(FontRenderContext frc);
    
    /**
     * Returns the lock object on which all threads acting in Figures in this
     * drawing synchronize to prevent race conditions.
     */
    Object getLock();
    
    /**
     * Adds an input format to the drawing.
     */
    void addInputFormat(InputFormat format);
    /**
     * Adds an output format to the drawing.
     */
    void addOutputFormat(OutputFormat format);
    
    /**
     * Sets input formats for the Drawing in order of preferred formats.
     * <p>
     * The input formats are used for loading the Drawing from a file and for
     * pasting Figures from the clipboard into the Drawing.
     */
    void setInputFormats(List<InputFormat> formats);
    /**
     * Gets input formats for the Drawing in order of preferred formats.
     */
    List<InputFormat> getInputFormats();
    /**
     * Sets output formats for the Drawing in order of preferred formats.
     * <p>
     * The output formats are used for saving the Drawing into a file and for
     * cutting and copying Figures from the Drawing into the clipboard.
     */
    void setOutputFormats(List<OutputFormat> formats);
    /**
     * Gets output formats for the Drawing in order of preferred formats.
     */
    List<OutputFormat> getOutputFormats();
    
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

