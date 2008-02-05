/*
 * @(#)BezierTool.java  1.2  2007-11-30
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

import javax.swing.undo.*;
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
 * @version 1.2 2007-11-30 Huw Jones: Factored calls to Bezier.fitBezierCurve out
 * into method calculateFittedPath.
 * <br>1.1 2006-07-12 Werner Randelshofer: Extended support for subclassing.
 * <br>1.0 2006-01-21 Werner Randelshofer: Created.
 */
public class BezierTool extends AbstractTool {

    /**
     * Set this to true to turn on debugging output on System.out.
     */
    private final static boolean DEBUG = false;
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
    private int nodeCountBeforeDrag;
    /**
     * A localized name for this tool. The presentationName is displayed by the
     * UndoableEdit.
     */
    private String presentationName;

    /** Creates a new instance. */
    public BezierTool(BezierFigure prototype) {
        this(prototype, null);
    }

    /** Creates a new instance. */
    public BezierTool(BezierFigure prototype, Map attributes) {
        this(prototype, attributes, null);
    }

    public BezierTool(BezierFigure prototype, Map attributes, String name) {
        this.prototype = prototype;
        this.attributes = attributes;
        if (name == null) {
            ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
            name = labels.getString("createFigure");
        }
        this.presentationName = name;
    }

    public String getPresentationName() {
        return presentationName;
    }

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
        }
    }

    public void mousePressed(MouseEvent evt) {
        if (DEBUG) {
            System.out.println("BezierTool.mousePressed " + evt);
        }
        super.mousePressed(evt);
        if (createdFigure == null) {
            finishWhenMouseReleased = null;

            createdFigure = createFigure();
            createdFigure.addNode(new BezierPath.Node(
                    getView().getConstrainer().constrainPoint(
                    getView().viewToDrawing(anchor))));
            getDrawing().add(createdFigure);
        } else {
            if (evt.getClickCount() == 1) {
                addPointToFigure(getView().getConstrainer().constrainPoint(
                        getView().viewToDrawing(anchor)));
            }
        }
        nodeCountBeforeDrag = createdFigure.getNodeCount();
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
            createdFigure.addNode(new BezierPath.Node(newPoint));
        } else {
            Point2D.Double endPoint = createdFigure.getEndPoint();
            Point2D.Double secondLastPoint = (pointCount <= 1) ? endPoint : createdFigure.getPoint(pointCount - 2, 0);
            if (newPoint.equals(endPoint)) {
            // nothing to do
            } else if (pointCount > 1 && Geom.lineContainsPoint(newPoint.x, newPoint.y, secondLastPoint.x, secondLastPoint.y, endPoint.x, endPoint.y, 0.9f / getView().getScaleFactor())) {
                createdFigure.setPoint(pointCount - 1, 0, newPoint);
            } else {
                createdFigure.addNode(new BezierPath.Node(newPoint));
            }
        }
        createdFigure.changed();
    }

    public void mouseClicked(MouseEvent evt) {
        if (createdFigure != null) {
            switch (evt.getClickCount()) {
                case 1:
                    if (createdFigure.getNodeCount() > 2) {
                        Rectangle r = new Rectangle(getView().drawingToView(createdFigure.getStartPoint()));
                        r.grow(2, 2);
                        if (r.contains(evt.getX(), evt.getY())) {
                            createdFigure.setClosed(true);

                            finishCreation(createdFigure);
                            createdFigure = null;
                            fireToolDone();
                        }
                    }
                    break;
                case 2:
                    finishWhenMouseReleased = null;

                    finishCreation(createdFigure);
                    /*
                    getView().addToSelection(createdFigure);
                     */
                    createdFigure = null;
                    fireToolDone();
                    break;
            }
        }
    }

    protected void fireUndoEvent(Figure createdFigure) {
        final Figure addedFigure = createdFigure;
        final Drawing addedDrawing = getDrawing();
        final DrawingView addedView = getView();
        getDrawing().fireUndoableEditHappened(new AbstractUndoableEdit() {

            public String getPresentationName() {
                return presentationName;
            }

            public void undo() throws CannotUndoException {
                super.undo();
                addedDrawing.remove(addedFigure);
            }

            public void redo() throws CannotRedoException {
                super.redo();
                addedView.clearSelection();
                addedDrawing.add(addedFigure);
                addedView.addToSelection(addedFigure);
            }
        });
    }

    public void mouseReleased(MouseEvent evt) {
        if (DEBUG) {
            System.out.println("BezierTool.mouseReleased " + evt);
        }
        if (createdFigure.getNodeCount() > nodeCountBeforeDrag + 1) {
            createdFigure.willChange();
            BezierPath figurePath = createdFigure.getBezierPath();
            BezierPath fittedPath = new BezierPath();
            for (int i = nodeCountBeforeDrag,  n = figurePath.size(); i < n; i++) {
                fittedPath.add(figurePath.get(nodeCountBeforeDrag));
                figurePath.remove(nodeCountBeforeDrag);
            }
            fittedPath = calculateFittedCurve(fittedPath);
            figurePath.addAll(fittedPath);
            createdFigure.setBezierPath(figurePath);
            createdFigure.changed();
            nodeCountBeforeDrag = createdFigure.getNodeCount();
        }
        
        if (finishWhenMouseReleased == Boolean.TRUE) {
            if (createdFigure.getNodeCount() > 2) {
                finishCreation(createdFigure);
                createdFigure = null;
                finishWhenMouseReleased = null;
                fireToolDone();
                return;
            }
        } else if (finishWhenMouseReleased == null) {
            finishWhenMouseReleased = Boolean.FALSE;
        }

    }

    protected void finishCreation(BezierFigure createdFigure) {
        getView().addToSelection(createdFigure);
        fireUndoEvent(createdFigure);
    }

    public void mouseDragged(MouseEvent evt) {
            if (finishWhenMouseReleased == null) {
                finishWhenMouseReleased = Boolean.TRUE;
            }

            int x = evt.getX();
            int y = evt.getY();
            addPointToFigure(getView().viewToDrawing(new Point(x, y)));
    }

    public void mouseMoved(MouseEvent evt) {
    }

    protected BezierPath calculateFittedCurve(BezierPath path) {
        return Bezier.fitBezierCurve(path, 1);
    }
}
