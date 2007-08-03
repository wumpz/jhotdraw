/*
 * @(#)AbstractCompositeFigure.java  2.3 2007-04-22
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
import static org.jhotdraw.draw.AttributeKeys.*;
/**
 * A Figure that is composed of several children. A AbstractCompositeFigure
 * doesn't define any layout behavior. It is up to subclassers to
 * arrange the contained children.
 *
 *
 * @author Werner Randelshofer
 * @version 2.3 2007-04-22 Take TRANSFORM attribute into account.
 * <br>2.2 2006-07-08 Minor changes.
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
     * Cached draw cachedBounds.
     */
    private Rectangle2D.Double cachedDrawingArea;
    /**
     * Cached layout cachedBounds.
     */
    private Rectangle2D.Double cachedBounds;
    
    /**
     * A Layouter determines how the AbstractCompositeFigure should
     * be laid out graphically.
     */
    private Layouter layouter;
    
    /**
     * Handles figure changes in the children.
     */
    private ChildHandler childHandler = new ChildHandler(this);
    private static class ChildHandler extends FigureAdapter implements UndoableEditListener {
        private AbstractCompositeFigure owner;
        private ChildHandler(AbstractCompositeFigure owner) {
            this.owner = owner;
        }
        @Override public void figureRequestRemove(FigureEvent e) {
            // If the CompositeFigure is removed from a drawing, we get
            // lots of figureRequestRemove events from our children. In this case
            // we must not remove them from ourselves, because we are not
            // part of a drawing anyway.
            if (owner.getDrawing() != null) {
                owner.remove(e.getFigure());
            }
        }
        
        @Override public void figureChanged(FigureEvent e) {
            if (! owner.isChanging()) {
                owner.willChange();
                owner.fireFigureChanged(e);
                owner.changed();
            }
        }
        
        @Override public void figureAreaInvalidated(FigureEvent e) {
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
    
    @Override public Collection<Handle> createHandles(int detailLevel) {
        LinkedList<Handle> handles = new LinkedList<Handle>();
        if (detailLevel == 0) {
            TransformHandleKit.addScaleMoveTransformHandles(this, handles);
        }
        return handles;
    }
    
    public void add(Figure figure) {
        add(getChildCount(), figure);
    }
    public void add(final int index, final Figure figure) {
        basicAdd(index, figure);
        if (getDrawing() != null) {
            figure.addNotify(getDrawing());
        }
        invalidate();
    }
    public void addAll(Collection<Figure> newFigures) {
        for (Figure f: newFigures) {
            basicAdd(getChildCount(), f);
            if (getDrawing() != null) {
                f.addNotify(getDrawing());
            }
        }
        invalidate();
    }
    public void basicAdd(Figure figure) {
        basicAdd(getChildCount(), figure);
    }
    public void basicAdd(int index, Figure figure) {
        children.add(index, figure);
        figure.addFigureListener(childHandler);
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
        super.removeNotify(drawing);
        // Copy children collection to avoid concurrent modification exception
        for (Figure child : new LinkedList<Figure>(children)) {
            child.removeNotify(drawing);
        }
    }
    
    public boolean remove(final Figure figure) {
        int index = children.indexOf(figure);
        if (index == -1) {
            return false;
        } else {
            basicRemoveChild(index);
            if (getDrawing() != null) {
                figure.removeNotify(getDrawing());
            }
            return true;
        }
    }
    public Figure removeChild(int index) {
        Figure removed = basicRemoveChild(index);
        if (getDrawing() != null) {
            removed.removeNotify(getDrawing());
        }
        return removed;
    }
    public boolean basicRemove(final Figure figure) {
        int index = children.indexOf(figure);
        if (index == -1) {
            return false;
        } else {
            basicRemoveChild(index);
            invalidate();
            return true;
        }
    }
    public Figure basicRemoveChild(int index) {
        Figure figure = children.remove(index);
        figure.removeFigureListener(childHandler);
        invalidate();
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
    public void transform(AffineTransform tx) {
        for (Figure f : children) {
            f.transform(tx);
        }
        invalidateBounds();
    }
    
    public void setBounds(Point2D.Double anchor, Point2D.Double lead) {
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
            transform(tx);
            tx.setToIdentity();
            tx.scale(sx, sy);
            transform(tx);
            tx.setToIdentity();
        }
        tx.translate(newBounds.x, newBounds.y);
        transform(tx);
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
    
    public void setAttribute(AttributeKey key, Object value) {
        for (Figure child : children) {
            child.setAttribute(key, value);
        }
        invalidate();
    }
    public Object getAttribute(AttributeKey name) {
        return null;
    }
    public Map<AttributeKey, Object> getAttributes() {
        return new HashMap<AttributeKey,Object>();
    }
    public Object getAttributesRestoreData() {
        LinkedList<Object> data = new LinkedList<Object>();
        for (Figure child : children) {
            data.add(child.getAttributesRestoreData());
        }
        return data;
    }
    public void restoreAttributesTo(Object newData) {
        Iterator<Object> data = ((LinkedList<Object>) newData).iterator();
        for (Figure child : children) {
            child.restoreAttributesTo(data.next());
        }
    }
    
    
    
    public boolean contains(Point2D.Double p) {
        if (TRANSFORM.get(this) != null) {
            try {
                p = (Point2D.Double) TRANSFORM.get(this).inverseTransform(p, new Point2D.Double());
            } catch (NoninvertibleTransformException ex) {
                InternalError error = new InternalError(ex.getMessage());
                error.initCause(ex);
                throw error;
            }
        };
        if (getDrawingArea().contains(p)) {
            for (Figure child : getChildrenFrontToBack()) {
                if (child.isVisible() && child.contains(p)) return true;
            }
        }
        return false;
    }
    
    public Figure findFigureInside(Point2D.Double p) {
        if (getDrawingArea().contains(p)) {
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
            setBounds(new Point2D.Double(r.x, r.y), new Point2D.Double(r.x + r.width, r.y + r.height));
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
    
    public Rectangle2D.Double getDrawingArea() {
        if (cachedDrawingArea == null) {
            for (Figure child : getChildren()) {
                if (child.isVisible()) {
                    Rectangle2D.Double childBounds = child.getDrawingArea();
                    if (! childBounds.isEmpty()) {
                        if (cachedDrawingArea == null) {
                            cachedDrawingArea = childBounds;
                        } else {
                            cachedDrawingArea.add(childBounds);
                        }
                    }
                }
            }
            if (cachedDrawingArea == null) {
                cachedDrawingArea = new Rectangle2D.Double(0, 0, -1, -1);
            }
        }
        return (Rectangle2D.Double) cachedDrawingArea.clone();
    }
    public Rectangle2D.Double getBounds() {
        if (cachedBounds == null) {
            for (Figure child : getChildrenFrontToBack()) {
                if (child.isVisible()) {
                    Rectangle2D r = child.getBounds();
                    if (AttributeKeys.TRANSFORM.get(child) != null) {
                        r = AttributeKeys.TRANSFORM.get(child).createTransformedShape(r).getBounds2D();
                    }
                    if (cachedBounds == null) {
                        cachedBounds = new Rectangle2D.Double(r.getX(), r.getY(), r.getWidth(), r.getHeight());
                    } else {
                        cachedBounds.add(r);
                    }
                }
            }
        }
        return (cachedBounds == null) ? new Rectangle2D.Double(0, 0, -1, -1) : (Rectangle2D.Double) cachedBounds.clone();
    }
    public void draw(Graphics2D g) {
        Rectangle2D clipBounds = g.getClipBounds();
        if (clipBounds != null) {
            for (Figure child : children) {
                if (child.isVisible() && child.getDrawingArea().intersects(clipBounds)) {
                    child.draw(g);
                }
            }
        } else {
            for (Figure child : children) {
                if (child.isVisible()) {
                    child.draw(g);
                }
            }
        }
    }
    
    public AbstractCompositeFigure clone() {
        AbstractCompositeFigure that = (AbstractCompositeFigure) super.clone();
        that.childHandler = new ChildHandler(that);
        that.children = new LinkedList<Figure>();
        for (Figure thisChild : this.children) {
            Figure thatChild = (Figure) thisChild.clone();
            that.children.add(thatChild);
            thatChild.addFigureListener(that.childHandler);
        }
        return that;
    }
    
    protected void invalidateBounds() {
        cachedBounds = null;
        cachedDrawingArea = null;
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
            basicAdd((Figure) in.readObject(i));
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
    
    public void restoreTransformTo(Object geometry) {
        LinkedList list = (LinkedList) geometry;
        Iterator i = list.iterator();
        for (Figure child : children) {
            child.restoreTransformTo(i.next());
        }
        invalidateBounds();
    }
    
    public Object getTransformRestoreData() {
        LinkedList<Object> list = new LinkedList<Object>();
        for (Figure child : children) {
            list.add(child.getTransformRestoreData());
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