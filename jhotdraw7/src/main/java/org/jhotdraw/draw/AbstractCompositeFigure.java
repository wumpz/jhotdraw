/*
 * @(#)AbstractCompositeFigure.java  2.2 2006-07-08
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

import java.io.IOException;
import org.jhotdraw.util.*;
import org.jhotdraw.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.event.*;
import javax.swing.undo.*;
import org.jhotdraw.geom.*;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;
/**
 * A Figure that is composed of several children. A AbstractCompositeFigure
 * doesn't define any layout behavior. It is up to subclassers to
 * arrange the contained children.
 *
 *
 * @author Werner Randelshofer
 * @version 2.2 2006-07-08 Minor changes. 
 * <br>2.1 2006-03-15 Fire undoable edit on attribute change.
 * <br>2.0.1 2006-02-06 Fixed ConcurrentModificationException in method
 * removeNotify.
 * <br>2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public abstract class AbstractCompositeFigure
        extends AbstractFigure
        implements CompositeFigure {
    /**
     * The children that this figure is composed of
     *
     * @see #add
     * @see #removeChild
     */
    private LinkedList<Figure> children = new LinkedList<Figure>();
    
    /**
     * Cached draw bounds.
     */
    private Rectangle2D.Double drawBounds;
    /**
     * Cached layout bounds.
     */
    private Rectangle2D.Double bounds;
    
    /**
     * A Layouter determines how the AbstractCompositeFigure should
     * be laid out graphically.
     */
    private Layouter layouter;
    
    /**
     * Handles figure changes in the children.
     */
    private ChildHandler childHandler = new ChildHandler(this);
    private static class ChildHandler implements FigureListener, UndoableEditListener {
        private AbstractCompositeFigure owner;
        private ChildHandler(AbstractCompositeFigure owner) {
            this.owner = owner;
        }
        public void figureRequestRemove(FigureEvent e) {
            owner.remove(e.getFigure());
        }
        
        public void figureRemoved(FigureEvent evt) {
          //  owner.remove(evt.getFigure());
        }
        
        public void figureChanged(FigureEvent e) {
            if (! owner.isChanging()) {
                owner.willChange();
                owner.fireFigureChanged(e);
                owner.changed();
            }
        }
        
        public void figureAdded(FigureEvent e) {
        }
        
        public void figureAttributeChanged(FigureEvent e) {
        }
        
        public void figureAreaInvalidated(FigureEvent e) {
            if (! owner.isChanging()) {
                owner.fireAreaInvalidated(e.getInvalidatedArea());
            }
        }
        
        public void undoableEditHappened(UndoableEditEvent e) {
            owner.fireUndoableEditHappened(e.getEdit());
        }
    };
    
    /** Creates a new instance. */
    public AbstractCompositeFigure() {
    }
    
    public Collection<Handle> createHandles(int detailLevel) {
        if (detailLevel == 0) {
            return super.createHandles(0);
        } /*else {
            LinkedList<Handle> handles = new LinkedList<Handle>();
            for (Figure child : children) {
                handles.addAll(child.createHandles(detailLevel - 1));
            }
            return handles;
        }*/
        
        LinkedList<Handle> handles = new LinkedList<Handle>();
        return handles;
    }
    
    public void add(Figure figure) {
        add(getChildCount(), figure);
    }
    public void add(final int index, final Figure figure) {
        willChange();
        basicAdd(index, figure);
        if (getDrawing() != null) {
            figure.addNotify(getDrawing());
        }
        changed();
    }
    public void addAll(Collection<Figure> newFigures) {
        willChange();
        for (Figure f: newFigures) {
            basicAdd(getChildCount(), f);
            if (getDrawing() != null) {
                f.addNotify(getDrawing());
            }
        }
        changed();
    }
    public void basicAdd(Figure figure) {
        basicAdd(getChildCount(), figure);
    }
    public void basicAdd(int index, Figure figure) {
        children.add(index, figure);
        figure.addFigureListener(childHandler);
        figure.addUndoableEditListener(childHandler);
        
    }
    public void basicAddAll(Collection<Figure> newFigures) {
        for (Figure f: newFigures) {
            basicAdd(getChildCount(), f);
        }
    }
    public void addNotify(Drawing drawing) {
        super.addNotify(drawing);
        for (Figure child : children) {
            child.addNotify(drawing);
        }
    }
    public void removeNotify(Drawing drawing) {
        // Copy children collection to avoid concurrent modification exception
        for (Figure child : new LinkedList<Figure>(children)) {
            child.removeNotify(drawing);
        }
        super.removeNotify(drawing);
    }
    
    public boolean remove(final Figure figure) {
        int index = children.indexOf(figure);
        if (index == -1) {
            return false;
        } else {
            willChange();
            basicRemoveChild(index);
            if (getDrawing() != null) {
                figure.removeNotify(getDrawing());
            }
            changed();
            return true;
        }
    }
    public Figure removeChild(int index) {
        willChange();
        Figure removed = basicRemoveChild(index);
        if (getDrawing() != null) {
            removed.removeNotify(getDrawing());
        }
        changed();
        return removed;
    }
    public boolean basicRemove(final Figure figure) {
        int index = children.indexOf(figure);
        if (index == -1) {
            return false;
        } else {
            basicRemoveChild(index);
            return true;
        }
    }
    public Figure basicRemoveChild(int index) {
        Figure figure = children.remove(index);
        figure.removeFigureListener(childHandler);
        figure.removeUndoableEditListener(childHandler);
        
        return figure;
    }
    
    /**
     * Removes all children.
     *
     * @see #add
     */
    public void removeAllChildren() {
        willChange();
        while (children.size() > 0) {
            Figure f = basicRemoveChild(children.size() - 1);
            if (getDrawing() != null) {
                f.addNotify(getDrawing());
            }
        }
        changed();
    }
    public void basicRemoveAllChildren() {
        while (children.size() > 0) {
            Figure f = basicRemoveChild(children.size() - 1);
        }
    }
    
    
    /**
     * Sends a figure to the back of the composite figure.
     *
     * @param figure that is part of this composite figure
     */
    public synchronized void sendToBack(Figure figure) {
        if (children.remove(figure)) {
            children.add(0, figure);
            figure.invalidate();
        }
    }
    
    /**
     * Sends a figure to the front of the drawing.
     *
     * @param figure that is part of the drawing
     */
    public synchronized void sendToFront(Figure figure) {
        if (children.remove(figure)) {
            children.add(figure);
            figure.invalidate();
        }
    }
    /**
     * Transforms the figure.
     */
    public void basicTransform(AffineTransform tx) {
        for (Figure f : children) {
            f.basicTransform(tx);
        }
        invalidateBounds();
    }
    
    public void basicSetBounds(Point2D.Double anchor, Point2D.Double lead) {
        Rectangle2D.Double oldBounds = getBounds();
        Rectangle2D.Double newBounds = new Rectangle2D.Double(
                Math.min(anchor.x, lead.x),
                Math.min(anchor.y, lead.y),
                Math.abs(anchor.x - lead.x),
                Math.abs(anchor.y - lead.y)
                );
        
        double sx = newBounds.width / oldBounds.width;
        double sy = newBounds.height / oldBounds.height;
        
        AffineTransform tx = new AffineTransform();
        tx.translate(-oldBounds.x, -oldBounds.y);
        if (! Double.isNaN(sx) && ! Double.isNaN(sy) &&
                (sx != 1d || sy != 1d) &&
                ! (sx < 0.0001) && ! (sy < 0.0001)) {
            basicTransform(tx);
            tx.setToIdentity();
            tx.scale(sx, sy);
            basicTransform(tx);
            tx.setToIdentity();
        }
        tx.translate(newBounds.x, newBounds.y);
        basicTransform(tx);
    }
    
    public void undoableEditHappened(UndoableEditEvent e) {
        fireUndoableEditHappened(e.getEdit());
    }
    
    public java.util.List<Figure> getChildren() {
        return Collections.unmodifiableList(children);
    }
    public int getChildCount() {
        return children.size();
    }
    public Figure getChild(int index) {
        return children.get(index);
    }
    /**
     * Returns an iterator to iterate in
     * Z-order front to back over the children.
     */
    public java.util.List<Figure> getChildrenFrontToBack() {
        return children ==  null ?
            new LinkedList<Figure>() :
            new ReversedList<Figure>(children);
    }
    
    public void setAttribute(AttributeKey name, Object value) {
        willChange();
        for (Figure child : children) {
            child.setAttribute(name, value);
        }
        changed();
    }
    public void basicSetAttribute(AttributeKey name, Object value) {
        for (Figure child : children) {
            child.basicSetAttribute(name, value);
        }
    }
    public Object getAttribute(AttributeKey name) {
        return null;
    }
    
    
    public boolean contains(Point2D.Double p) {
        if (getDrawBounds().contains(p)) {
            for (Figure child : getChildrenFrontToBack()) {
                if (child.isVisible() && child.contains(p)) return true;
            }
        }
        return false;
    }
    
    public Figure findFigureInside(Point2D.Double p) {
        if (getDrawBounds().contains(p)) {
            Figure found = null;
            for (Figure child : getChildrenFrontToBack()) {
                if (child.isVisible()) {
                    found = child.findFigureInside(p);
                    if (found != null) {
                        return found;
                    }
                }
            }
        }
        return null;
    }
    
    public Figure findChild(Point2D.Double p) {
        if (getBounds().contains(p)) {
            Figure found = null;
            for (Figure child : getChildrenFrontToBack()) {
                if (child.isVisible() && child.contains(p)) {
                    return child;
                }
            }
        }
        return null;
    }
    public int findChildIndex(Point2D.Double p) {
        Figure child = findChild(p);
        return (child == null) ? -1 : children.indexOf(child);
    }
    /**
     * Get a Layouter object which encapsulated a layout
     * algorithm for this figure. Typically, a Layouter
     * accesses the child components of this figure and arranges
     * their graphical presentation.
     *
     *
     * @return layout strategy used by this figure
     */
    public Layouter getLayouter() {
        return layouter;
    }
    
    /**
     * A layout algorithm is used to define how the child components
     * should be laid out in relation to each other. The task for
     * layouting the child components for presentation is delegated
     * to a Layouter which can be plugged in at runtime.
     */
    public void layout() {
        if (getLayouter() != null) {
            Rectangle2D.Double bounds = getBounds();
            Point2D.Double p = new Point2D.Double(bounds.x, bounds.y);
            Rectangle2D.Double r = getLayouter().layout(
                    this, p, p
                    );
            basicSetBounds(new Point2D.Double(r.x, r.y), new Point2D.Double(r.x + r.width, r.y + r.height));
            invalidateBounds();
        }
    }
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
     * @param newLayouter	encapsulation of a layout algorithm.
     */
    public void setLayouter(Layouter newLayouter) {
        this.layouter = newLayouter;
    }
    
    public Dimension2DDouble getPreferredSize() {
        if (this.layouter != null) {
            Rectangle2D.Double r = layouter.calculateLayout(this, getStartPoint(), getEndPoint());
            return new Dimension2DDouble(r.width, r.height);
        } else {
            return super.getPreferredSize();
        }
    }
    
    public Rectangle2D.Double getFigureDrawBounds() {
        if (drawBounds == null) {
            for (Figure child : getChildrenFrontToBack()) {
                if (child.isVisible()) {
                    Rectangle2D.Double childBounds = child.getDrawBounds();
                    if (! childBounds.isEmpty()) {
                        if (drawBounds == null) {
                            drawBounds = childBounds;
                        } else {
                            drawBounds.add(childBounds);
                        }
                    }
                }
            }
        }
        return (drawBounds == null) ? new Rectangle2D.Double(0, 0, -1, -1) : (Rectangle2D.Double) drawBounds.clone();
    }
    public Rectangle2D.Double getBounds() {
        if (bounds == null) {
            for (Figure child : getChildrenFrontToBack()) {
                if (child.isVisible()) {
                    if (bounds == null) {
                        bounds = child.getBounds();
                    } else {
                        bounds.add(child.getBounds());
                    }
                }
            }
        }
        return (bounds == null) ? new Rectangle2D.Double(0, 0, -1, -1) : (Rectangle2D.Double) bounds.clone();
    }
    public void drawFigure(Graphics2D g) {
        for (Figure child : children) {
            if (child.isVisible()) {
                child.draw(g);
            }
        }
        if (isConnectorsVisible()) {
            drawConnectors(g);
        }
    }
    
    protected void drawConnectors(Graphics2D g) {
    }
    public AbstractCompositeFigure clone() {
        AbstractCompositeFigure that = (AbstractCompositeFigure) super.clone();
        that.childHandler = new ChildHandler(that);
        that.children = new LinkedList<Figure>();
        for (Figure thisChild : this.children) {
            Figure thatChild = (Figure) thisChild.clone();
            that.children.add(thatChild);
            thatChild.addFigureListener(that.childHandler);
            thatChild.addUndoableEditListener(that.childHandler);
        }
        return that;
    }
    
    protected void invalidateBounds() {
        bounds = null;
        drawBounds = null;
    }
    
    public Collection<Figure> getDecomposition() {
        LinkedList<Figure> list = new LinkedList<Figure>();
        list.add(this);
        list.addAll(getChildren());
        return list;
    }
    
    public void read(DOMInput in) throws IOException {
        in.openElement("children");
        for (int i=0; i < in.getElementCount(); i++) {
            add((Figure) in.readObject(i));
        }
        in.closeElement();
    }
    
    public void write(DOMOutput out) throws IOException {
        out.openElement("children");
        for (Figure child : getChildren()) {
            out.writeObject(child);
        }
        out.closeElement();
    }
    
    public Map<AttributeKey, Object> getAttributes() {
        return new HashMap<AttributeKey,Object>();
    }
    
    public void restoreTo(Object geometry) {
        LinkedList list = (LinkedList) geometry;
        int index = 0;
        for (Object geom : list) {
            getChild(index).restoreTo(geom);
            index++;
        }
        invalidateBounds();
    }
    
    public Object getRestoreData() {
        LinkedList<Object> list = new LinkedList<Object>();
        for (Figure child : children) {
            list.add(child.getRestoreData());
        }
        return list;
    }
    
    public void willChange() {
        super.willChange();
        if (getChangingDepth() == 1) {
            for (Figure child : children) {
                child.willChange();
            }
        }
    }
    public void changed() {
        if (getChangingDepth() == 1) {
            for (Figure child : children) {
                child.changed();
            }
        }
        super.changed();
    }
    
    
    public void invalidate() {
        super.invalidate();
        invalidateBounds();
    }
    
    protected void validate() {
        super.validate();
        layout();
        invalidateBounds();
    }

    public void removeAttribute(AttributeKey key) {
        // do nothing
    }

    public boolean hasAttribute(AttributeKey key) {
        return false;
    }
}