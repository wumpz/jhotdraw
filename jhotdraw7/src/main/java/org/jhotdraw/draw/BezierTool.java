/*
 * @(#)BezierTool.java  1.1  2006-07-12
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
import org.jhotdraw.undo.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.*;
import org.jhotdraw.geom.*;
/**
 * Tool to scribble a BezierFigure
 *
 * @author  Werner Randelshofer
 * @version 1.1 2006-07-12 Extended support for subclassing.
 * <br>1.0 2006-01-21 Created.
 */
public class BezierTool extends AbstractTool {
    private Boolean finishWhenMouseReleased;
    protected Map<AttributeKey, Object> attributes;
    /**
     * The prototype for new figures.
     */
    private BezierFigure prototype;
    /**
     * The created figure.
     */
    protected BezierFigure createdFigure;
    
    private CompositeEdit creationEdit;
    
    private int nodeCountBeforeDrag;
    
    /** Creates a new instance. */
    public BezierTool(BezierFigure prototype) {
        this(prototype, null);
    }
    /** Creates a new instance. */
    public BezierTool(BezierFigure prototype, Map attributes) {
        this.prototype = prototype;
        this.attributes = attributes;
    }
    /*
    public void draw(Graphics2D g) {
        if (createdFigure != null
        && mouseLocation != null) {
            Point2D.Double p = createdFigure.getEndPoint();
            g.setColor(Color.black);
            g.setStroke(new BasicStroke());
            g.drawLine(p.x, p.y, mouseLocation.x, mouseLocation.y);
        }
     
    }
     */
    public void activate(DrawingEditor editor) {
        super.activate(editor);
        getView().clearSelection();
        getView().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }
    
    public void deactivate(DrawingEditor editor) {
        super.deactivate(editor);
        getView().setCursor(Cursor.getDefaultCursor());
        if (createdFigure != null) {
            finishCreation(createdFigure);
            createdFigure = null;
            getDrawing().fireUndoableEditHappened(creationEdit);
        }
    }
    
    public void mousePressed(MouseEvent evt) {
        super.mousePressed(evt);
        if (createdFigure == null) {
            finishWhenMouseReleased = null;
            
            creationEdit = new CompositeEdit("Figur erstellen");
            getDrawing().fireUndoableEditHappened(creationEdit);
            createdFigure = createFigure();
            createdFigure.addNode(new BezierPath.Node(
                    getView().getConstrainer().constrainPoint(
                    getView().viewToDrawing(anchor)
                    )));
            getDrawing().add(createdFigure);
            nodeCountBeforeDrag = createdFigure.getNodeCount();
        } else {
            if (evt.getClickCount() == 1) {
                addPointToFigure(getView().getConstrainer().constrainPoint(
                        getView().viewToDrawing(anchor)));
            }
            nodeCountBeforeDrag = createdFigure.getNodeCount();
        }
    }
    
    protected BezierFigure createFigure() {
        BezierFigure f = (BezierFigure) prototype.clone();
        getEditor().applyDefaultAttributesTo(f);
        if (attributes != null) {
            for (Map.Entry<AttributeKey, Object> entry : attributes.entrySet()) {
                f.setAttribute(entry.getKey(), entry.getValue());
            }
        }
        return f;
    }
    
    protected Figure getCreatedFigure() {
        return createdFigure;
    }
    protected Figure getAddedFigure() {
        return createdFigure;
    }
    
    protected void addPointToFigure(Point2D.Double newPoint) {
        int pointCount = createdFigure.getNodeCount();
        
        createdFigure.willChange();
        if (pointCount < 2) {
            createdFigure.basicAddNode(new BezierPath.Node(newPoint));
        } else {
            Point2D.Double endPoint = createdFigure.getEndPoint();
            Point2D.Double secondLastPoint = (pointCount <= 1) ?
                endPoint :
                createdFigure.getPoint(pointCount - 2, 0);
            if (newPoint.equals(endPoint)) {
                // nothing to do
            } else if (pointCount > 1 && Geom.lineContainsPoint(newPoint.x, newPoint.y, secondLastPoint.x, secondLastPoint.y, endPoint.x, endPoint.y, 0.9f / getView().getScaleFactor())) {
                createdFigure.basicSetPoint(pointCount - 1, 0, newPoint);
            } else {
                createdFigure.basicAddNode(new BezierPath.Node(newPoint));
            }
        }
        createdFigure.changed();
    }
    
    public void mouseClicked(MouseEvent evt) {
        if (createdFigure != null) {
            switch (evt.getClickCount()) {
                case 1 :
                    if (createdFigure.getNodeCount() > 2) {
                        Rectangle r = new Rectangle(getView().drawingToView(createdFigure.getStartPoint()));
                        r.grow(2,2);
                        if (r.contains(evt.getX(), evt.getY())) {
                            createdFigure.setClosed(true);
                            // getView().addToSelection(createdFigure);
                            finishCreation(createdFigure);
                            getDrawing().fireUndoableEditHappened(creationEdit);
                            createdFigure = null;
                            fireToolDone();
                        }
                    }
                    break;
                case 2 :
                    finishWhenMouseReleased = null;
                    finishCreation(createdFigure);
                    /*
                    getView().addToSelection(createdFigure);
                     */
                    getDrawing().fireUndoableEditHappened(creationEdit);
                    createdFigure = null;
                    fireToolDone();
                    break;
            }
        }
    }
    public void mouseReleased(MouseEvent evt) {
        if (finishWhenMouseReleased == Boolean.TRUE) {
            if (createdFigure.getNodeCount() > 2) {
                BezierPath fittedPath = Bezier.fitBezierCurve(createdFigure.getBezierPath(), 1);
                createdFigure.willChange();
                createdFigure.basicSetBezierPath(fittedPath);
                createdFigure.changed();
                finishCreation(createdFigure);
                createdFigure = null;
                getDrawing().fireUndoableEditHappened(creationEdit);
                finishWhenMouseReleased = null;
                fireToolDone();
                return;
            }
        } else if (finishWhenMouseReleased == null) {
            finishWhenMouseReleased = Boolean.FALSE;
        }
        
        if (createdFigure.getNodeCount() > nodeCountBeforeDrag + 1) {
            createdFigure.willChange();
            BezierPath fittedPath = new BezierPath();
            for (int i=nodeCountBeforeDrag, n = createdFigure.getNodeCount(); i < n; i++) {
                fittedPath.add(createdFigure.getNode(nodeCountBeforeDrag));
                createdFigure.basicRemoveNode(nodeCountBeforeDrag);
            }
            fittedPath = Bezier.fitBezierCurve(fittedPath, 1);
            for (BezierPath.Node node : fittedPath) {
                createdFigure.basicAddNode(node);
            }
            nodeCountBeforeDrag = createdFigure.getNodeCount();
            createdFigure.changed();
        }
        
    }
    
    protected void finishCreation(BezierFigure createdFigure) {
        getView().addToSelection(createdFigure);
    }
    
    public void mouseDragged(MouseEvent evt) {
        if (createdFigure != null) {
            if (finishWhenMouseReleased == null) {
                finishWhenMouseReleased = Boolean.TRUE;
            }
            
            int x = evt.getX();
            int y = evt.getY();
            
            addPointToFigure(getView().viewToDrawing(new Point(x, y)));
        }
    }
    public void mouseMoved(MouseEvent evt) {
        /*
        if (createdFigure != null) {
        }*/
    }
}
