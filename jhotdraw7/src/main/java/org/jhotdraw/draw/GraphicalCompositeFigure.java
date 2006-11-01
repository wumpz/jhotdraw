/*
 * @(#)GraphicalCompositeFigure.java  2.0  2006-01-14
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

import java.io.IOException;
import org.jhotdraw.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.undo.*;
import javax.swing.event.*;

import static org.jhotdraw.draw.AttributeKeys.*;
import org.jhotdraw.geom.*;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;
/**
 * The GraphicalCompositeFigure fills in the gap between a AbstractCompositeFigure
 * and other figures which mainly have a presentation purpose. The
 * GraphicalCompositeFigure can be configured with any Figure which
 * takes over the task for rendering the graphical presentation for
 * a AbstractCompositeFigure. Therefore, the GraphicalCompositeFigure manages
 * contained figures like the AbstractCompositeFigure does, but delegates
 * its graphical presentation to another (graphical) figure which
 * purpose it is to draw the container for all contained figures.
 *
 * The GraphicalCompositeFigure adds to the {@link AbstractCompositeFigure AbstractCompositeFigure}
 * by containing a presentation figure by default which can not be removed.  Normally,
 * the {@link AbstractCompositeFigure AbstractCompositeFigure} can not be seen without containing a figure
 * because it has no mechanism to draw itself.  It instead relies on its contained
 * figures to draw themselves thereby giving the {@link AbstractCompositeFigure AbstractCompositeFigure} its
 * appearance.  However, the <b>GraphicalCompositeFigure</b>'s presentation figure
 * can draw itself even when the <b>GraphicalCompositeFigure</b> contains no other figures.
 * The <b>GraphicalCompositeFigure</b> also uses a {@link Layouter Layouter} or layout
 * its contained figures.
 *
 *
 *
 * @author Wolfram Kaiser (original code), Werner Randelshofer (this derived version)
 * @version 2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 1. Dezember 2003  Derived from JHotDraw 5.4b1.
 */
public class GraphicalCompositeFigure 
        extends AbstractCompositeFigure {
    protected HashMap<AttributeKey, Object> attributes = new HashMap<AttributeKey,Object>();
    private HashSet<AttributeKey> forbiddenAttributes;
    
    /**
     * Figure which performs all presentation tasks for this
     * AbstractCompositeFigure as CompositeFigures usually don't have
     * an own presentation but present only the sum of all its
     * children.
     */
    private Figure presentationFigure;
    
    /**
     * Handles figure changes in the children.
     */
    private PresentationFigureHandler presentationFigureHandler = new PresentationFigureHandler(this);
    private static class PresentationFigureHandler implements FigureListener, UndoableEditListener {
        private GraphicalCompositeFigure owner;
        private PresentationFigureHandler(GraphicalCompositeFigure owner) {
            this.owner = owner;
        }
        public void figureRequestRemove(FigureEvent e) {
            owner.remove(e.getFigure());
        }
        
        public void figureRemoved(FigureEvent evt) {
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
    
    /**
     * Default constructor which uses nothing as presentation
     * figure. This constructor is needed by the Storable mechanism.
     */
    public GraphicalCompositeFigure() {
        this(null);
    }
    /**
     * Constructor which creates a GraphicalCompositeFigure with
     * a given graphical figure for presenting it.
     *
     * @param	newPresentationFigure	figure which renders the container
     */
    public GraphicalCompositeFigure(Figure newPresentationFigure) {
        super();
        setPresentationFigure(newPresentationFigure);
    }
    
    /**
     * Return the logcal display area. This method is delegated to the encapsulated
     * presentation figure.
     */
    public Rectangle2D.Double getBounds() {
        if (getPresentationFigure() == null) return super.getBounds();
        return getPresentationFigure().getBounds();
    }
    
    public boolean contains(Point2D.Double p) {
        if (getPresentationFigure() != null) {
            return getPresentationFigure().contains(p);
        } else {
            return super.contains(p);
        }
    }
    
    public void addNotify(Drawing drawing) {
        super.addNotify(drawing);
        if (getPresentationFigure() != null) {
            getPresentationFigure().addNotify(drawing);
        }
    }
    public void removeNotify(Drawing drawing) {
        super.removeNotify(drawing);
        if (getPresentationFigure() != null) {
            getPresentationFigure().removeNotify(drawing);
        }
    }
    /**
     * Return the draw area. This method is delegated to the
     * encapsulated presentation figure.
     */
    public Rectangle2D.Double getFigureDrawBounds() {
        Rectangle2D.Double r;
        if (getPresentationFigure() != null) {
            Rectangle2D.Double presentationBounds = getPresentationFigure().getDrawBounds();
            r = super.getFigureDrawBounds();
            if (r.isEmpty()) {
                r = presentationBounds;
            } else {
                r.add(presentationBounds);
            }
        } else {
            r = super.getFigureDrawBounds();
        }
        return r;
    }
    /**
     * Moves the figure. This is the
     * method that subclassers override. Clients usually
     * call displayBox.
     */
    public void basicSetBounds(Point2D.Double anchor, Point2D.Double lead) {
        if (getLayouter() == null) {
            super.basicSetBounds(anchor, lead);
            basicSetPresentationFigureBounds(anchor, lead);
        } else {
            Rectangle2D.Double r = getLayouter().layout(this, anchor, lead);
            basicSetPresentationFigureBounds(new Point2D.Double(r.getX(), r.getY()),
                    new Point2D.Double(
                    Math.max(lead.x, (int) r.getMaxX()),
                    Math.max(lead.y, (int) r.getMaxY())
                    )
                    );
            invalidate();
        }
    }
    
    protected void superBasicSetBounds(Point2D.Double anchor, Point2D.Double lead) {
        super.basicSetBounds(anchor, lead);
    }
    protected void basicSetPresentationFigureBounds(Point2D.Double anchor, Point2D.Double lead) {
        if (getPresentationFigure() != null) {
            getPresentationFigure().basicSetBounds(anchor, lead);
        }
    }
    
    /**
     * Standard presentation method which is delegated to the encapsulated presentation figure.
     * The presentation figure is moved as well as all contained figures.
     */
    public void basicTransform(AffineTransform tx) {
        super.basicTransform(tx);
        if (getPresentationFigure() != null) {
            getPresentationFigure().basicTransform(tx);
        }
    }
    
    /**
     * Draw the figure. This method is delegated to the encapsulated presentation figure.
     */
    public void drawFigure(Graphics2D g) {
        drawPresentationFigure(g);
        super.drawFigure(g);
    }
    
    protected void drawPresentationFigure(Graphics2D g) {
        if (getPresentationFigure() != null) {
            getPresentationFigure().draw(g);
        }
    }
    
    /**
     * Return default handles from the presentation figure.
     */
    public Collection<Handle> createHandles(int detailLevel) {
        LinkedList<Handle> handles = new LinkedList<Handle>();
        if (detailLevel == 0) {
        MoveHandle.addMoveHandles(this, handles);
        }
        return handles;
        //return getPresentationFigure().getHandles();
    }
    /**
     * Set a figure which renders this AbstractCompositeFigure. The presentation
     * tasks for the AbstractCompositeFigure are delegated to this presentation
     * figure.
     *
     *
     * @param newPresentationFigure	figure takes over the presentation tasks
     */
    public void setPresentationFigure(Figure newPresentationFigure) {
        if (this.presentationFigure != null) {
            this.presentationFigure.removeFigureListener(presentationFigureHandler);
            this.presentationFigure.removeUndoableEditListener(presentationFigureHandler);
            if (getDrawing() != null) {
                this.presentationFigure.removeNotify(getDrawing());
            }
        }
        this.presentationFigure = newPresentationFigure;
        if (this.presentationFigure != null) {
            this.presentationFigure.addFigureListener(presentationFigureHandler);
            this.presentationFigure.addUndoableEditListener(presentationFigureHandler);
            if (getDrawing() != null) {
                this.presentationFigure.addNotify(getDrawing());
            }
        }
        // FIXME: We should calculate the layout here.
    }
    
    /**
     * Get a figure which renders this AbstractCompositeFigure. The presentation
     * tasks for the AbstractCompositeFigure are delegated to this presentation
     * figure.
     *
     *
     * @return figure takes over the presentation tasks
     */
    public Figure getPresentationFigure() {
        return presentationFigure;
    }
    
    public GraphicalCompositeFigure clone() {
        GraphicalCompositeFigure that = (GraphicalCompositeFigure) super.clone();
        that.presentationFigure = (this.presentationFigure == null) ?
            null :
            (Figure) this.presentationFigure.clone();
        if (that.presentationFigure != null) {
            that.presentationFigure.addFigureListener(that.presentationFigureHandler);
            that.presentationFigure.addUndoableEditListener(that.presentationFigureHandler);
        }
        return that;
    }
    public void remap(HashMap<Figure,Figure> oldToNew) {
        super.remap(oldToNew);
        if (presentationFigure != null) {
            presentationFigure.remap(oldToNew);
        }
    }
    /**
     * Sets an attribute of the figure.
     * AttributeKey name and semantics are defined by the class implementing
     * the figure interface.
     */
    public void setAttribute(AttributeKey key, Object newValue) {
        if (forbiddenAttributes == null
                || ! forbiddenAttributes.contains(key)) {
            willChange();
            if (getPresentationFigure() != null) {
                getPresentationFigure().setAttribute(key, newValue);
            }
            super.setAttribute(key, newValue);
            
            Object oldValue = attributes.put(key, newValue);
            fireAttributeChanged(key, oldValue, newValue);
            fireUndoableEditHappened(new AttributeChangeEdit(this, key, oldValue, newValue));
            changed();
        }
    }
    /**
     * Sets an attribute of the figure.
     * AttributeKey name and semantics are defined by the class implementing
     * the figure interface.
     */
    public void basicSetAttribute(AttributeKey key, Object newValue) {
        if (forbiddenAttributes == null
                || ! forbiddenAttributes.contains(key)) {
            if (getPresentationFigure() != null) {
                getPresentationFigure().basicSetAttribute(key, newValue);
            }
            super.basicSetAttribute(key, newValue);
            Object oldValue = attributes.put(key, newValue);
        }
    }
    public void setAttributeEnabled(AttributeKey key, boolean b) {
        if (forbiddenAttributes == null) {
            forbiddenAttributes = new HashSet<AttributeKey>();
        }
        if (b) {
            forbiddenAttributes.remove(key);
        } else {
            forbiddenAttributes.add(key);
        }
    }
    /**
     * Gets an attribute from the figure.
     */
    public Object getAttribute(AttributeKey key) {
        if (getPresentationFigure() != null) {
            return getPresentationFigure().getAttribute(key);
        } else {
            return (! attributes.containsKey(key)) ?
                key.getDefaultValue() :
                attributes.get(key);
        }
    }
    /**
     * Applies all attributes of this figure to that figure.
     */
    protected void applyAttributesTo(Figure that) {
        for (Map.Entry<AttributeKey, Object> entry : attributes.entrySet()) {
            that.setAttribute(entry.getKey(), entry.getValue());
        }
    }
    protected void writeAttributes(DOMOutput out) throws IOException {
        Figure prototype = (Figure) out.getPrototype();
        
        boolean isElementOpen = false;
        for (Map.Entry<AttributeKey, Object> entry : attributes.entrySet()) {
            AttributeKey key = entry.getKey();
            if (forbiddenAttributes == null
                    || ! forbiddenAttributes.contains(key)) {
                Object prototypeValue = key.get(prototype);
                Object attributeValue = key.get(this);
                if (prototypeValue != attributeValue ||
                        (prototypeValue != null && attributeValue != null &&
                        ! prototypeValue.equals(attributeValue))) {
                    if (! isElementOpen) {
                        out.openElement("a");
                        isElementOpen = true;
                    }
                    out.openElement(key.getKey());
                    out.writeObject(entry.getValue());
                    out.closeElement();
                }
            }
        }
        if (isElementOpen) {
            out.closeElement();
        }
    }
    protected void readAttributes(DOMInput in) throws IOException {
        if (in.getElementCount("a") > 0) {
            in.openElement("a");
            for (int i=in.getElementCount() - 1; i >= 0; i-- ) {
                in.openElement(i);
                String name = in.getTagName();
                Object value = in.readObject();
                AttributeKey key = getAttributeKey(name);
                if (key != null && key.isAssignable(value)) {
                    if (forbiddenAttributes == null
                            || ! forbiddenAttributes.contains(key)) {
                        setAttribute(key, value);
                    }
                }
                in.closeElement();
            }
            in.closeElement();
        }
    }
    
    protected AttributeKey getAttributeKey(String name) {
        return AttributeKeys.supportedAttributeMap.get(name);
    }
    public Map<AttributeKey, Object> getAttributes() {
        return new HashMap<AttributeKey,Object>(attributes);
    }
    /**
     * This is a default implementation that chops the point at the rectangle
     * returned by getBounds() of the figure.
     * <p>
     * Figures which have a non-rectangular shape need to override this method.
     * <p>
     * This method takes the following attributes into account:
     * AttributeKeys.STROKE_COLOR, AttributeKeys.STROKE_PLACEMENT, and 
     * AttributeKeys.StrokeTotalWidth.
     */
    public Point2D.Double chop(Point2D.Double from) {
        Rectangle2D.Double r = getBounds();
        if (STROKE_COLOR.get(this) != null) {
            double grow;
            switch (STROKE_PLACEMENT.get(this)) {
                case CENTER:
                default :
                    grow = AttributeKeys.getStrokeTotalWidth(this);
                    break;
                case OUTSIDE :
                    grow = AttributeKeys.getStrokeTotalWidth(this);
                    break;
                case INSIDE :
                    grow = 0d;
                    break;
            }
            Geom.grow(r, grow, grow);
        }
        return Geom.angleToPoint(r, Geom.pointToAngle(r, from));
    }
}
