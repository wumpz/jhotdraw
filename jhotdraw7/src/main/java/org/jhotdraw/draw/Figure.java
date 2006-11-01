/*
 * @(#)Figure.java  3.0  2006-01-20
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
 */


package org.jhotdraw.draw;

import org.jhotdraw.util.*;
import java.beans.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.undo.*;
import javax.swing.event.*;
import java.io.*;
import org.jhotdraw.geom.*;
import org.jhotdraw.xml.DOMStorable;
/**
 * The interface of a graphical figure.
 * <p>
 * A Figure knows its display box and can draw itself. A figure can be composed
 * of several figures. To interact and manipulate with a figure it can provide
 * Handles and Connectors.
 * <p>
 * A figure has a set of handles to manipulate its shape or attributes. A figure
 * has one or more connectors that define how to locate a connection point.
 * <p>
 * Figures can have an open ended set of attributes. An attribute is identified
 * by an AttributeKey.
 * <p>
 * Default implementations for the Figure interface are provided by
 * AbstractFigure.
 *
 * @author Werner Randelshofer
 * @version 3.0 2006-01-20 Reworked for J2SE 1.5.
 */
public interface Figure extends Cloneable, Serializable, DOMStorable {
    // DRAWING
    /**
     * Draws the figure and its decorator figure.
     *
     * @param g The Graphics2D to draw to.
     */
    public void draw(Graphics2D g);
    
     /**
     * Gets the layer of the figure.
     * The layer is used to determine the z-ordering of a figure inside of a
     * drawing. Figures with a higher layer number are drawn after figures
     * with a lower number.
     * The z-order of figures within the same layer is determined by the 
     * sequence the figures were added to a drawing. Figures added later to
     * a drawn after figures which have been added before.
     * If a figure changes its layer, it must fire a 
      * <code>FigureListener.figureChanged</code> event to
     * its figure listeners.
      *
      * FIXME - Replace int value by a Layer object.
     */
    public int getLayer();    
    
    /**
     * A Figure is only drawn by a Drawing and by CompositeFigure, if it is visible.
     * Layouter's should ignore invisible figures too.
     */
    public boolean isVisible();
    /**
     * Changes the visible state of the Figure.
     * <p>
     * The Figure fires <code>FigureListener.figureChanged</code> and 
     * <code>UndoableEditListener.undoableEditHappened</code>,
     * if this operation changed its visible state.</li>
     * </ul>
     */
    public void setVisible(boolean newValue);
   
    
    // BOUNDS
    /**
     * Sets the logical bounds of the figure and of its decorator figure.
     * <p>
     * This is used by Tool's which create a new Figure and by Tool's which
     * connect a Figure to another Figure.
     * <p>
     * This is a basic operation which does not fire events.
     *
     * @param start the start point of the bounds
     * @param end the end point of the bounds
     * @see #getBounds
     */
    public void basicSetBounds(Point2D.Double start, Point2D.Double end);
    /**
     * Returns the start point of the bounds.
     * 
     * 
     * @see #basicSetBounds
     */
    public Point2D.Double getStartPoint();
    /**
     * Returns the end point of the bounds.
     * 
     * 
     * @see #basicSetBounds
     */
    public Point2D.Double getEndPoint();
    /**
     * Returns the logical bounds of the figure as a Rectangle.
     * The logical bounds are used by some Handle objects for adjusting the 
     * figure.
     */
    public Rectangle2D.Double getBounds();
    /**
     * Returns the drawing bounding box of the figure and of its decorator figure.
     * This takes line width, line caps and other decorations into account.
     * Note: getDrawBounds must include the logical bounds of the figure.
     */
    public Rectangle2D.Double getDrawBounds();
    /**
     * The preferred size is used by Layouter to determine the preferred
     * size of a Figure. For most Figure's this is the same as the 
     * dimensions returned by getBounds.
     */
    public Dimension2DDouble getPreferredSize();
    /**
     * Gets data which can be used to restore the shape of the figure after a 
     * basicTransform has been applied to it.
     * 
     * @see #basicTransform(AffineTransform)
     */
    public Object getRestoreData();
    /**
     * Restores the shape of the figure to a previously stored state.
     */
    public void restoreTo(Object restoreData);
    /**
     * Transforms the shape of the Figure. Transformations using double
     * precision arithmethics are inherently lossy operations. Therefore it is 
     * recommended to use getRestoreData() restoreTo() to provide lossless 
     * undo/redo functionality.
     * After the transform has finished, the bounds of the decorator figure
     * are changed to match the transformed bounds of the figure.
     * <p>
     * This is a basic operation which does not fire events.
     * 
     * @param tx The transformation.
     */
    public void basicTransform(AffineTransform tx);
    
    // ATTRIBUTES
    /**
     * Sets an attribute of the figure.
     * AttributeKey name and semantics are defined by the class implementing
     * the Figure interface.
     * <p>
     * On an attribute change, the Figure fires
     * <code>FigureListener.figureAttributeChanged</code>, 
     * <code>UndoableEditListener.undoableEditHappened</code>. If
     * the shape is affected by an attribute change, 
     * <code>FigureListener.figureChanged</code> is fired too.
     */
    public void setAttribute(AttributeKey key, Object value);
    /**
     * Sets an attribute of the figure without firing events.
     * AttributeKey name and semantics are defined by the class implementing
     * the Figure interface.
     * <p>
     * Use <code>AttributeKey.set()</code> for typesafe access to this method.
     * 
     * @see AttributeKey#set
     */
    public void basicSetAttribute(AttributeKey key, Object value);
    /**
     * Gets an attribute from the Figure.
     * <p>
     * Use <code>AttributeKey.get()</code> for typesafe access to this method.
     * 
     * @see AttributeKey#get
     *
     * @return Returns the attribute value. If the Figure does not have an
     * attribute with the specified key, returns key.getDefaultValue().
     */
    public Object getAttribute(AttributeKey key);
    /**
     * Returns a view to all attributes of this figure.
     * By convention, an unmodifiable map is returned.
     */
    public Map<AttributeKey, Object> getAttributes();
    
    // EDITING
    /**
     * Returns true, if the user can manipulate this figure.
     * If this operation returns false, Tool's should not interact with this
     * figure.
     */
    public boolean isInteractive();
    /**
     * Checks if a point is contained by the figure.
     * <p>
     * This is used for hit testing by Tool's. 
     */
    boolean contains(Point2D.Double p);
    /**
     * Creates handles used to manipulate the figure.
     *
     * @param detailLevel The detail level of the handles. Usually this is 0 for
     * bounding box handles and 1 for point handles. 
     * @return a Collection of handles
     * @see Handle
     */
    public Collection<Handle> createHandles(int detailLevel);
    /**
     * Returns a cursor for the specified location.
     */
    public Cursor getCursor(Point2D.Double p);
    /**
     * Returns a collection of Action's for the specified location.
     *
     * <p>The collection may contain null entries. These entries are used
     * interpreted as separators in the popup menu.
     * <p>Actions can use the property Figure.ACTION_SUBMENU to specify a 
     * submenu.
     */
    public Collection<Action> getActions(Point2D.Double p);
    /**
     * Returns a specialized tool for the specified location.
     * <p>Returns null, if no specialized tool is available.
     */
    public Tool getTool(Point2D.Double p);
    /**
     * Returns a tooltip for the specified location.
     */
    public String getTooltip(Point2D.Double p);
    
    // CONNECTING 
    /**
     * Checks if this Figure can be connected.
     */
    public boolean canConnect();
    /**
     * Gets a connector for this figure at the given location.
     * A figure can have different connectors at different locations.
     *
     * @param p the location of the connector.
     * @param prototype The prototype used to create a connection or null if 
     * unknown. This allows for specific connectors for different 
     * connection figures.
     */
    public Connector findConnector(Point2D.Double p, ConnectionFigure prototype);
    /**
     * Gets a compatible connector.
     * If the provided connector is part of this figure, return the connector.
     * If the provided connector is part of another figure, return a connector
     * with the same semantics for this figure.
     * Return null, if no compatible connector is available.
     */
    public Connector findCompatibleConnector(Connector c, boolean isStartConnector);
    /**
     * Sets whether the connectors should be visible for the
     * specified courting connector.
     * Connectors can be optionally visible.
     */
    public void setConnectorsVisible(boolean isVisible, ConnectionFigure courtingConnection);
    /**
     * Returns true, if this figure draws its connectors.
     */
    public boolean isConnectorsVisible();

    // COMPOSITE FIGURES
    /**
     * Checks whether the given figure is contained in this figure.
     * A figure includes itself.
     */
    public boolean includes(Figure figure);
    /**
     * Returns the figure that contains the given point.
     */
    public Figure findFigureInside(Point2D.Double p);
    /**
     * Returns a decompositon of a figure into its parts.
     * A figure is considered as a part of itself.
     */
    public Collection<Figure> getDecomposition();    

    // CLONING
    /**
     * Returns a clone of the figure.
     */
    Object clone();
    /**
     * After cloning a collection of figures, the ConnectionFigures contained
     * in this collection still connect to the original figures instead of
     * to the clones.
     * Using This operation and providing a map, which maps from the original
     * collection of figures to the new collection, connections can be remapped
     * to the new figures.
     */
    public void remap(Map<Figure,Figure> oldToNew);   
    
    
    // EVENT HANDLING
    /**
     * Informs a figure, that it has been added to the specified drawing.
     * The figure must inform all FigureListeners that it has been added.
     */
    public void addNotify(Drawing d);
    /**
     * Informs a figure, that it has been removed from the specified drawing.
     * The figure must inform all FigureListeners that it has been removed.
     */
    public void removeNotify(Drawing d);
    /**
     * Informs that a Figure is about to change its shape.
     * <p>
     * <code>willChange</code> and <code>changed</code> are typically used 
     * as pairs before and after invoking one or multiple basic-methods on
     * the Figure.
     */
    public void willChange();
    /**
     * Informs that a Figure changed its shape. 
     * This fires a <code>FigureListener.figureChanged</code>
     * event for the current display bounds of the figure.
     * 
     * @see #willChange()
     */
    public void changed();
    /**
     * Informs that a Figure has invalidated its display area and needs to
     * be drawn. 
     * Fires a <code>FigureListener.areaInvalidated</code> event.
     */
    public void invalidate();
    /**
     * Fires a <code>FigureListener.figureRequestRemove</code> event.
     */
    public void requestRemove();
    /**
     * Handles a drop.
     * 
     * @param p The location of the mouse event.
     * @param droppedFigures The dropped figures.
     * @param view The drawing view which is the source of the mouse event.
     * @return Returns true, if the figures should snap back to the location
     * they were dragged from.
     */
    public boolean handleDrop(Point2D.Double p, Collection<Figure> droppedFigures, DrawingView view);
    /**
     * Handles a mouse click.
     *
     * @param p The location of the mouse event.
     * @param evt The mouse event.
     * @param view The drawing view which is the source of the mouse event.
     *
     * @return Returns true, if the figure consumed the mouse click.
      */
    public boolean handleMouseClick(Point2D.Double p, MouseEvent evt, DrawingView view);
    /**
     * Adds a listener for FigureEvent's.
     */
    public void addFigureListener(FigureListener l);
    /**
     * Removes a listener for FigureEvent's.
     */
    public void removeFigureListener(FigureListener l);
    /**
     * Adds a listener for UndoableEdit events.
     */
    public void addUndoableEditListener(UndoableEditListener l);
    /**
     * Removes a listener for UndoableEdit events.
     */
    public void removeUndoableEditListener(UndoableEditListener l);
    
    /**
     * Sets a decorator figure. Set this to null, if no decorator is desired.
     * The decorator will use the same logical bounds as this figure.
     * It will be drawn when the figure is drawn.
     */
    public void setDecorator(Figure newValue);
    /**
     * Gets the decorator figure.
     */
    public Figure getDecorator();
}
