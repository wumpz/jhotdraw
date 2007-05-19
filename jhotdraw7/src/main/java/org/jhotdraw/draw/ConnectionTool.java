/*
 * @(#)ConnectionTool.java  4.0  2007-05-18
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

import javax.swing.undo.*;
import org.jhotdraw.undo.*;
import org.jhotdraw.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.*;
import java.awt.dnd.*;

/**
 * A tool to create a connection between two figures.
 * The  {@see ConnectionFigure} to be created is specified by a prototype.
 * The location of the start and end points are controlled by {@see Connector}s.
 * <p>
 * To create a connection using the ConnectionTool, the user does the following
 * mouse gestures on a DrawingView:
 * <ol>
 * <li>Press the mouse button inside of a Figure. If the ConnectionTool can
 * find a Connector at this location, it uses it as the starting point for
 * the connection.</li>
 * <li>Drag the mouse while keeping the mouse button pressed, and then release
 * the mouse button. This defines the end point of the connection.
 * If the ConnectionTool finds a Connector at this location, it uses it
 * as the end point of the connection and creates a ConnectionFigure.</li>
 * </ol>
 *
 * @author Werner Randelshofer
 * @version 4.0 2007-05 Reworked due to changes in ConnectionFigure interface.
 * Removed split/join functionality for connection points.
 * <br>3.1 2006-07-15 Added support for prototype class name.
 * <br>3.0 2006-06-07 Reworked.
 * <br>2.1 2006-03-15 When user is not pressing the mouse button, we use
 * the mouse over view as the current view.
 * <br>2.0.1 2006-02-14 Fixed drawing code.
 * <br>2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class ConnectionTool extends AbstractTool {
    private final static int ANCHOR_WIDTH = 6;
    
    /**
     * Attributes to be applied to the created ConnectionFigure.
     * These attributes override the default attributes of the
     * DrawingEditor.
     */
    private Map<AttributeKey, Object> prototypeAttributes;
    /**
     * The Connector at the start point of the connection.
     */
    private Connector startConnector;
    /**
     * The Connector at the end point of the connection.
     */
    private Connector endConnector;
    
    /**
     * The created figure.
     */
    protected ConnectionFigure createdFigure;
    
    /**
     * the prototypical figure that is used to create new
     * connections.
     */
    protected ConnectionFigure  prototype;
    
    /**
     * The figure for which we enabled drawing of connectors.
     */
    protected Figure targetFigure;
    
    
    /**
     * A localized name for this tool. The presentationName is displayed by the
     * UndoableEdit.
     */
    private String presentationName;
    
    /** Creates a new instance.
     */
    public ConnectionTool(ConnectionFigure prototype) {
        this(prototype, null, null);
    }
    public ConnectionTool(ConnectionFigure prototype, Map<AttributeKey, Object> attributes) {
        this(prototype, attributes, null);
    }
    public ConnectionTool(ConnectionFigure prototype, Map<AttributeKey, Object> attributes, String presentationName) {
        this.prototype = prototype;
        this.prototypeAttributes = attributes;
        if (presentationName == null) {
            ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
            presentationName = labels.getString("createConnectionFigure");
        }
        this.presentationName = presentationName;
    }
    public ConnectionTool(String prototypeClassName) {
        this(prototypeClassName, null, null);
    }
    public ConnectionTool(String prototypeClassName, Map<AttributeKey, Object> attributes, String presentationName) {
        try {
            this.prototype = (ConnectionFigure) Class.forName(prototypeClassName).newInstance();
        } catch (Exception e) {
            InternalError error = new InternalError("Unable to create ConnectionFigure from "+prototypeClassName);
            error.initCause(e);
            throw error;
        }
        this.prototypeAttributes = attributes;
        if (presentationName == null) {
            ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
            presentationName = labels.getString("createConnectionFigure");
        }
        this.presentationName = presentationName;
    }
    public ConnectionFigure getPrototype() {
        return prototype;
    }
    
    public void mouseMoved(MouseEvent evt) {
        updateTarget(evt);
    }
    public void updateTarget(MouseEvent evt) {
        Point2D.Double targetPoint = viewToDrawing(new Point(evt.getX(), evt.getY()));
        Figure aFigure = getDrawing().findFigure(targetPoint);
        targetFigure = aFigure;
    }
    
    /**
     * Manipulates connections in a context dependent way. If the
     * mouse down hits a figure start a new connection. If the mousedown
     * hits a connection split a segment or join two segments.
     */
    public void mousePressed(MouseEvent evt) {
        super.mousePressed(evt);
        getView().clearSelection();
        
        Point2D.Double startPoint = viewToDrawing(anchor);
        Figure startFigure = getDrawing().findFigure(startPoint);
        startConnector = (startFigure == null) ?
            null :
            startFigure.findConnector(startPoint, prototype);
        
        if (startConnector != null && prototype.canConnect(startConnector)) {
            Point2D.Double anchor = startConnector.getAnchor();
            createdFigure = createFigure();
            createdFigure.setStartPoint(anchor);
            createdFigure.setEndPoint(anchor);
            getDrawing().add(createdFigure);
            Rectangle r = new Rectangle(getView().drawingToView(anchor));
            r.grow(ANCHOR_WIDTH,ANCHOR_WIDTH);
            fireAreaInvalidated(r);
        } else {
            startConnector = null;
            createdFigure = null;
        }
        
        endConnector = null;
    }
    
    /**
     * Adjust the created connection.
     */
    public void mouseDragged(java.awt.event.MouseEvent e) {
        updateTarget(e);
        if (createdFigure != null) {
            Point2D.Double endPoint = viewToDrawing(new Point(e.getX(), e.getY()));
            getView().getConstrainer().constrainPoint(endPoint);
            
            createdFigure.willChange();
            Figure endFigure = getDrawing().findFigureExcept(endPoint, createdFigure);
            if (endConnector != null) {
                Point2D.Double anchor = endConnector.getAnchor();
                Rectangle r = new Rectangle(getView().drawingToView(anchor));
                r.grow(ANCHOR_WIDTH,ANCHOR_WIDTH);
                fireAreaInvalidated(r);
            }
            endConnector = (endFigure == null) ?
                null :
                endFigure.findConnector(endPoint, prototype);
            
            if (endConnector != null && createdFigure.canConnect(startConnector, endConnector)) {
                Point2D.Double anchor = endConnector.getAnchor();
                createdFigure.setEndPoint(anchor);
                Rectangle r = new Rectangle(getView().drawingToView(anchor));
                r.grow(ANCHOR_WIDTH,ANCHOR_WIDTH);
                fireAreaInvalidated(r);
            } else {
                createdFigure.setEndPoint(endPoint);
            }
            createdFigure.changed();
        }
    }
    
    /**
     * Connects the figures if the mouse is released over another
     * figure.
     */
    public void mouseReleased(MouseEvent e) {
        if (createdFigure != null) {
            createdFigure.willChange();
            createdFigure.setStartConnector(startConnector);
            createdFigure.setEndConnector(endConnector);
            createdFigure.updateConnection();
            createdFigure.changed();
            
            final Figure addedFigure = createdFigure;
            final Drawing addedDrawing = getDrawing();
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
                    addedDrawing.add(addedFigure);
                }
            });
            targetFigure = null;
            Point2D.Double anchor = startConnector.getAnchor();
            Rectangle r = new Rectangle(getView().drawingToView(anchor));
            r.grow(ANCHOR_WIDTH,ANCHOR_WIDTH);
            fireAreaInvalidated(r);
            anchor = endConnector.getAnchor();
            r = new Rectangle(getView().drawingToView(anchor));
            r.grow(ANCHOR_WIDTH,ANCHOR_WIDTH);
            fireAreaInvalidated(r);
            startConnector = endConnector = null;
            createdFigure = null;
            creationFinished(createdFigure);
        } else {
            fireToolDone();
        }
    }
    public void activate(DrawingEditor editor) {
        super.activate(editor);
    }
    public void deactivate(DrawingEditor editor) {
        if (createdFigure != null) {
            getDrawing().remove(createdFigure);
            createdFigure = null;
        }
        targetFigure = null;
        startConnector = endConnector = null;
        super.deactivate(editor);
    }
    /**
     * Creates the ConnectionFigure. By default the figure prototype is
     * cloned.
     */
    protected ConnectionFigure createFigure() {
        ConnectionFigure f = (ConnectionFigure) prototype.clone();
        getEditor().applyDefaultAttributesTo(f);
        if (prototypeAttributes != null) {
            for (Map.Entry<AttributeKey, Object> entry : prototypeAttributes.entrySet()) {
                f.setAttribute((AttributeKey) entry.getKey(), entry.getValue());
            }
        }
        return f;
    }
    
    public void draw(Graphics2D g) {
        if (createdFigure != null) {
            Graphics2D gg = (Graphics2D) g.create();
            gg.transform(getView().getDrawingToViewTransform());
            createdFigure.draw(gg);
            if (startConnector != null) {
                Point p = getView().drawingToView(startConnector.getAnchor());
                Ellipse2D.Double e = new Ellipse2D.Double(
                        p.x - ANCHOR_WIDTH / 2, p.y - ANCHOR_WIDTH / 2,
                        ANCHOR_WIDTH, ANCHOR_WIDTH
                        );
                g.setColor(Color.GREEN);
                g.fill(e);
                g.setColor(Color.BLACK);
                g.draw(e);
            }
            if (endConnector != null) {
                Point p = getView().drawingToView(endConnector.getAnchor());
                Ellipse2D.Double e = new Ellipse2D.Double(
                        p.x - ANCHOR_WIDTH / 2, p.y - ANCHOR_WIDTH / 2,
                        ANCHOR_WIDTH, ANCHOR_WIDTH
                        );
                g.setColor(Color.GREEN);
                g.fill(e);
                g.setColor(Color.BLACK);
                g.draw(e);
            }
            gg.dispose();
            
        }
    }
    /**
     * This method allows subclasses to do perform additonal user interactions
     * after the new figure has been created.
     * The implementation of this class just invokes fireToolDone.
     */
    protected void creationFinished(Figure createdFigure) {
        fireToolDone();
    }
}
