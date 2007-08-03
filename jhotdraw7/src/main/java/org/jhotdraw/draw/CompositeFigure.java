/*
 * @(#)CompositeFigure.java  2.0  2007-07-17
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

/**
 * A CompositeFigure is composed of several child Figures.
 * A CompositeFigure can be laid out using a Layouter.
 *
 * @author Werner Randelshofer
 * @version 2.0 2007-07-17 Added support for CompositeFigureListener.
 * CompositeFigure is now streamlined with the java.util.List<Figure>
 * interface. 
 * <br>1.0 27. Januar 2006 Created.
 */
public interface CompositeFigure extends Figure {
    /**
     * The value of this attribute is a Insets2D.Double object.
     */
    public final static AttributeKey<Insets2D.Double> LAYOUT_INSETS = new AttributeKey<Insets2D.Double>("layoutInsets", new Insets2D.Double());
    
    /**
     * Adds a child to the figure.
     * This is a convenience method for {@code add(getChildCount(), child);}
     * 
     * @return {@code true} if this CompositeFigure changed as a result of the
     *         call
     */
    public boolean add(Figure child);
    /**
     * Adds a child to the figure at the specified index.
     * Calls {@code figureAdded} on all registered
     * {@code CompositeFigureListener}'s.
     */
    public void add(int index, Figure child);
    /**
     * Adds a child to the figure without firing events.
     * This is a convenience method for basicAdd(getChildCount(), child);
     */
    public void basicAdd(Figure child);
    /**
     * Adds a child to the figure at the specified index without
     * firing events.
     */
    public void basicAdd(int index, Figure child);
    /**
     * Removes the specified child.
     * Returns true, if the Figure contained the removed child.
     * Calls {@code figureRemoved} on all registered
     * {@code CompositeFigureListener}'s.
     */
    public boolean remove(Figure child);
    /**
     * Removes the child at the specified index.
     * Returns the removed child figure.
     * Calls {@code figureRemoved} on all registered
     * {@code CompositeFigureListener}'s.
     */
    public Figure removeChild(int index);
    /**
     * Removes all children from the composite figure.
     */
    public void removeAllChildren();
    /**
     * Removes the specified child without firing events.
     * Returns the index of the removed figure. Returns -1 if the
     * figure was not a child of this CompositeFigure.
     */
    public int basicRemove(Figure child);
    /**
     * Removes the child at the specified index without firing events.
     * Returns the removed child figure.
     */
    public Figure basicRemoveChild(int index);
    /**
     * Removes all children from the composite figure without firing events.
     */
    public void basicRemoveAllChildren();
    /**
     * Returns an unchangeable list view on the children.
     */
    public java.util.List<Figure> getChildren();
    /**
     * Returns the number of children.
     */
    public int getChildCount();
    /**
     * Returns the child figure at the specified index.
     */
    public Figure getChild(int index);
    /**
     * Returns true if this composite figure contains the specified figure.
     */
    public boolean contains(Figure f);
    /**
     * Get a Layouter object which encapsulated a layout
     * algorithm for this figure. Typically, a Layouter
     * accesses the child components of this figure and arranges
     * their graphical presentation.
        *
     * @return layout strategy used by this figure
     */
    public Layouter getLayouter();
    /**
     * A layout algorithm is used to define how the child components
     * should be laid out in relation to each other. The task for
     * layouting the child components for presentation is delegated
     * to a Layouter which can be plugged in at runtime.
     */
    public void layout();
    /**
     * Set a Layouter object which encapsulated a layout
     * algorithm for this figure. Typically, a Layouter
     * accesses the child components of this figure and arranges
     * their graphical presentation. It is a good idea to set
     * the Layouter in the protected initialize() method
     * so it can be recreated if a GraphicalCompositeFigure is
     * read and restored from a StorableInput stream.
     *
     *
     * @param newValue	encapsulation of a layout algorithm.
     */
    public void setLayouter(Layouter newValue);
    
    /**
     * Adds a listener for this composite figure.
     */
    public void addCompositeFigureListener(CompositeFigureListener listener);
    
    /**
     * Removes a listener from this composite figure.
     */
    public void removeCompositeFigureListener(CompositeFigureListener listener);
}
