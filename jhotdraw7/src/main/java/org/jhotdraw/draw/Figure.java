/*
 * @(#)Figure.java  8.0  2009-04-19
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
import javax.swing.*;
import java.io.*;
import org.jhotdraw.geom.*;
import org.jhotdraw.xml.DOMStorable;

/**
 * A Figure knows its bounds and it can draw itself. A figure is an element of a
 * {@link Drawing}. 
 * <p>
 * A figure has a set of {@link Handle}s to manipulate its shape or attributes. A figure
 * has one or more {@link Connector}s that define how to locate a connection point.
 * <p>
 * Figures can have an open ended set of attributes. An attribute is identified
 * by an {@link AttributeKey}.
 * <p>
 * Specialized subinterfaces of Figure allow to compose a figure from
 * several figures, to connect a figure to other figures, to hold text or
 * an image, and to layout a figure.
 * <p>
 * Design pattern:<br>
 * Name: Composite.<br>
 * Role: Component.<br>
 * Partners: {@link CompositeFigure} as Composite. 
 * <p>
 * Design pattern:<br>
 * Name: Decorator.<br>
 * Role: Decorator.<br>
 * Partners: {@link DecoratedFigure} as Component. 
 * <p>
 * Design pattern:<br>
 * Name: Model-View-Controller.<br>
 * Role: Model.<br>
 * Partners: {@link DrawingView} as View, {@link Tool} as Controller.
 * <p>
 * Design pattern:<br>
 * Name: Observer.<br>
 * Role: Subject.<br>
 * Partners: {@link FigureListener} as Observer.
 * <p>
 * Design pattern:<br>
 * Name: Prototype.<br>
 * Role: Prototype.<br>
 * Partners: {@link CreationTool} as Client.
 * 
 * 
 * @author Werner Randelshofer
 * @version 8.0 2009-04-18 Made set/getAttribute methods type safe.
 * <br>7.1 2008-05-17 Added support for mouse hover handles.
 * <br>7.0.1 2008-02-13 Fixed comments on
 * setAttribute and getAttribute methods.
 * <br>7.0 2008-02-13 Huw Jones: Added method isTransformable.
 * <br>6.0 2007-12-19 Removed method invalidate. 
 * <br>5.0 2007-07-24 Removed method isSelectable and added
 * isSelectable and isRemovable instead.
 * <br>4.2 2007-05-19 Removed setConnectorsVisible, isConnectorsVisible
 * method due to changes in Connector interface. 
 * <br>4.1 2007-05-18 Removed addUndoableEditListener, 
 * removeUndoableEditListener methods. They are not needed anymore, due to
 * the removal of the basicSet methods for undoable attributes. 
 * <br>4.0 2007-05-12 Replaced set.../basicSet... design for undoable attributes 
 * by setAttribute/getAttributesRestoreData/restoreAttributesTo design.
 * <br>3.1 2007-04-14 Method handleMouseClick is now required to consume
 * an event, if it returns true. 
 * <br>3.0 2006-01-20 Reworked for J2SE 1.5.
 * @see Drawing
 * @see Handle
 * @see Connector
 */
public interface Figure extends Cloneable, Serializable, DOMStorable {
    // DRAWING
    /**
     * Draws the figure.
     *
     * @param g The Graphics2D to draw to.
     */
    public void draw(Graphics2D g);

    /**
     * Gets the layer number of the figure.
     * The layer is used to determine the z-ordering of a figure inside of a
     * drawing. Figures with a higher layer number are drawn after figures
     * with a lower number.
     * The z-order of figures within the same layer is determined by the 
     * sequence the figures were added to a drawing. Figures added later to
     * a drawn after figures which have been added before.
     * If a figure changes its layer, it must fire a 
     * <code>FigureListener.figureChanged</code> event to
     * its figure listeners.
     */
    public int getLayer();

    /**
     * A Figure is only drawn by a CompositeFigure, if it is visible.
     * Layouter's should ignore invisible figures too.
     */
    public boolean isVisible();


    // BOUNDS
    /**
     * Sets the logical and untransformed bounds of the figure.
     * <p>
     * This is used by Tool's which create a new Figure and by Tool's which
     * connect a Figure to another Figure.
     * <p>
     * This is a basic operation which does not fire events. Use the following
     * code sequence, if you need event firing:
     * <pre>
     * aFigure.willChange();
     * aFigure.setBounds(...);
     * aFigure.changed();
     * </pre>
     * 
     * 
     * @param start the start point of the bounds
     * @param end the end point of the bounds
     * @see #getBounds
     */
    public void setBounds(Point2D.Double start, Point2D.Double end);

    /**
     * Returns the untransformed logical start point of the bounds.
     * 
     * 
     * 
     * @see #setBounds
     */
    public Point2D.Double getStartPoint();

    /**
     * Returns the untransformed logical end point of the bounds.
     * 
     * 
     * 
     * @see #setBounds
     */
    public Point2D.Double getEndPoint();

    /**
     * Returns the untransformed logicalbounds of the figure as a Rectangle.
     * The handle bounds are used by Handle objects for adjusting the 
     * figure and for aligning the figure on a grid.
     */
    public Rectangle2D.Double getBounds();

    /**
     * Returns the drawing area of the figure as a Rectangle.
     * The drawing area is used to improve the performance of GraphicView, for
     * example for clipping of repaints and for clipping of mouse events.
     * <p>
     * The drawing area needs to be large enough, to take line width, line caps
     * and other decorations into account that exceed the bounds of the Figure.
     */
    public Rectangle2D.Double getDrawingArea();

    /**
     * The preferred size is used by Layouter to determine the preferred
     * size of a Figure. For most Figure's this is the same as the 
     * dimensions returned by getBounds.
     */
    public Dimension2DDouble getPreferredSize();
    
    /**
     * Checks if a point is contained by the figure.
     * <p>
     * This is used for hit testing by Tool's. 
     */
    boolean contains(Point2D.Double p);


    // TRANSFORMING
    /**
     * Gets data which can be used to restore the transformation of the figure 
     * without loss of precision, after a transform has been applied to it.
     * 
     * 
     * @see #transform(AffineTransform)
     */
    public Object getTransformRestoreData();

    /**
     * Restores the transform of the figure to a previously stored state.
     */
    public void restoreTransformTo(Object restoreData);

    /**
     * Transforms the shape of the Figure. Transformations using double
     * precision arithmethics are inherently lossy operations. Therefore it is 
     * recommended to use getTransformRestoreData() restoreTransformTo() to 
     * provide lossless undo/redo functionality.
     * <p>
     * This is a basic operation which does not fire events. Use the following
     * code sequence, if you need event firing:
     * <pre>
     * aFigure.willChange();
     * aFigure.transform(...);
     * aFigure.changed();
     * </pre>
     * 
     * 
     * @param tx The transformation.
     * @see #getTransformRestoreData
     * @see #restoreTransformTo
     */
    public void transform(AffineTransform tx);

    // ATTRIBUTES
    /**
     * Sets an attribute of the figure and calls attributeChanged on all
     * registered FigureListener's.
     * <p>
     * For efficiency reasons, the drawing is not automatically repainted.
     * If you want the drawing to be repainted when the attribute is changed,
     * you can either use {@code key.set(figure, value); } or
     * {@code figure.willChange(); figure.setAttribute(key, value);
     * figure.changed(); }.
     * 
     * @see AttributeKey#set
     */
    public <T> void setAttribute(AttributeKey<T> key, T value);

    /**
     * Gets an attribute from the Figure.
     * 
     * @see AttributeKey#get
     *
     * @return Returns the attribute value. If the Figure does not have an
     * attribute with the specified key, returns key.getDefaultValue().
     */
    public <T> T getAttribute(AttributeKey<T> key);

    /**
     * Returns a view to all attributes of this figure.
     * By convention, an unmodifiable map is returned.
     */
    public Map<AttributeKey, Object> getAttributes();

    /**
     * Gets data which can be used to restore the attributes of the figure 
     * after a setAttribute has been applied to it.
     */
    public Object getAttributesRestoreData();

    /**
     * Restores the attributes of the figure to a previously stored state.
     */
    public void restoreAttributesTo(Object restoreData);

    // EDITING
    /**
     * Returns true, if the user may select this figure.
     * If this operation returns false, Tool's should not select this
     * figure on behalf of the user.
     * <p>
     * Please note, that even if this method returns false, the Figure
     * may become part of a selection for other reasons. For example,
     * if the Figure is part of a GroupFigure, then the Figure is 
     * indirectly part of the selection, when the user selects the
     * GroupFigure. 
     */
    public boolean isSelectable();

    /**
     * Returns true, if the user may remove this figure.
     * If this operation returns false, Tool's should not remove this
     * figure on behalf of the user.
     * <p>
     * Please note, that even if this method returns false, the Figure
     * may be removed from the Drawing for other reasons. For example,
     * if the Figure is used to display a warning message, the Figure
     * can be removed from the Drawing, when the warning message is
     * no longer relevant.
     */
    public boolean isRemovable();

    /**
     * Returns true, if the user may transform this figure.
     * If this operation returns false, Tool's should not transform this
     * figure on behalf of the user.
     * <p>
     * Please note, that even if this method returns false, the Figure
     * may be transformed for other reasons. For example, if the Figure takes 
     * part in an animation.
     * 
     * @see #transform
     */
    public boolean isTransformable();

    /**
     * Creates handles used to manipulate the figure.
     *
     * @param detailLevel The detail level of the handles. Usually this is 0 for
     * bounding box handles and 1 for point handles. The value -1 is used 
     * by the SelectAreaTracker and the HandleTracker to highlight figures, over which the mouse
     * pointer is hovering.
     * @return a Collection of handles
     * @see Handle
     */
    public Collection<Handle> createHandles(int detailLevel);

    /**
     * Returns a cursor for the specified location.
     */
    public Cursor getCursor(Point2D.Double p);

    /**
     * Returns a collection of Action's for the specified location on the figure.
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
     * Returns a tooltip for the specified location on the figure.
     */
    public String getToolTipText(Point2D.Double p);

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
     * Returns all connectors of this Figure for the specified prototype of
     * a ConnectionFigure.
     * <p>
     * This is used by connection tools and connection handles
     * to visualize the connectors when the user is about to
     * create a ConnectionFigure to this Figure.
     * 
     * @param prototype The prototype used to create a connection or null if 
     * unknown. This allows for specific connectors for different 
     * connection figures.
     */
    public Collection<Connector> getConnectors(ConnectionFigure prototype);

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
     * Returns a clone of the figure, with clones of all aggregated figures,
     * such as chilrend and decorators. The cloned figure does not clone
     * the list of FigureListeners from its original. 
     */
    public Object clone();

    /**
     * After cloning a collection of figures, the ConnectionFigures contained
     * in this collection still connect to the original figures instead of
     * to the clones.
     * Using This operation and providing a map, which maps from the original
     * collection of figures to the new collection, connections can be remapped
     * to the new figures.
     */
    public void remap(Map<Figure, Figure> oldToNew, boolean disconnectIfNotInMap);

    // EVENT HANDLING
    /**
     * Informs a figure, that it has been added to a drawing.
     * The figure must inform all FigureListeners that it has been added.
     */
    public void addNotify(Drawing d);

    /**
     * Informs a figure, that it has been removed from a drawing.
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
     * @return Returns true, if the event was consumed.
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
}
