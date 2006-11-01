/*
 * @(#)AbstractConnector.java  2.1  2006-06-05
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
import org.jhotdraw.geom.*;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;
/**
 * AbstractConnector provides default implementation for
 * the Connector interface.
 *
 * @see Connector
 *
 * @author Werner Randelshofer
 * @version 2.1 2006-06-05 Support connection to decorator.
 * <br>2.0 2006-01-14 Changed to support doubl precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class AbstractConnector implements Connector {
    /**
     * The owner of the connector
     */
    private Figure owner;
    /**
     * Whether we should connect to the figure or to its decorator.
     */
    private boolean isConnectToDecorator;
    /**
     * Whether we are visible.
     */
    private boolean isVisible;
    
    /**
     * Whether the state of this connector is persistent.
     * Set this to true only, when the user interface allows to change the
     * state of the connector.
     */
    private boolean isStatePersistent;
    
    
    /**
     * Constructs a connector that has no owner. It is only
     * used internally to resurrect a connectors from a
     * StorableOutput. It should never be called directly.
     */
    public AbstractConnector() {
        owner = null;
    }
    /**
     * Constructs a connector with the given owner figure.
     */
    public AbstractConnector(Figure owner) {
        this.owner = owner;
    }
    
    public void setConnectToDecorator(boolean newValue) {
        isConnectToDecorator = newValue;
    }
    public boolean isConnectToDecorator() {
        return isConnectToDecorator;
    }
    public void setVisible(boolean newValue) {
        isVisible = newValue;
    }
    public boolean isVisible() {
        return isVisible;
    }
    protected final Figure getConnectorTarget(Figure f) {
        return (isConnectToDecorator && f.getDecorator() != null) ? f.getDecorator() : f;
    }
    
    
    /**
     * Tests if a point is contained in the connector.
     */
    public boolean contains(Point2D.Double p) {
        return getOwner().contains(p);
    }
    
    /**
     * Draws this connector. By default connectors are invisible.
     */
    public void draw(Graphics2D g) {
        if (isVisible) {
            Rectangle2D.Double bounds = getBounds();
            Ellipse2D.Double circle = new Ellipse2D.Double(bounds.x + bounds.width / 2 - 3, bounds.y + bounds.height / 2 - 3, 6, 6);
            g.setColor(Color.blue);
            g.fill(circle);
            }
    }
    
    public Point2D.Double findStart(ConnectionFigure connection) {
        return findPoint(connection);
    }
    
    public Point2D.Double findEnd(ConnectionFigure connection) {
        return findPoint(connection);
    }
    
    /**
     * Gets the connection point. Override when the connector
     * does not need to distinguish between the start and end
     * point of a connection.
     */
    protected Point2D.Double findPoint(ConnectionFigure connection) {
        return Geom.center(getBounds());
    }
    
    public Rectangle2D.Double getBounds() {
        return getOwner().getBounds();
    }
    
    /**
     * Gets the connector's owner.
     */
    public Figure getOwner() {
        return owner;
    }
    /**
     * Sets the connector's owner.
     */
    protected void setOwner(Figure newValue) {
        owner = newValue;
    }
    
    public Object clone() {
        try {
            AbstractConnector that = (AbstractConnector) super.clone();
            return that;
        } catch (CloneNotSupportedException e) {
            InternalError error = new InternalError(e.toString());
            //error.initCause(e); <- requires JDK 1.4
            throw error;
        }
    }
    /**
     * This is called, when the start location of the connection has been
     * moved by the user. The user has this probably done, to adjust the layout.
     * The connector may use this as a hint to improve the results for the next
     * call to findEnd.
     */
    public void updateStartLocation(Point2D.Double p) {
    }
    /**
     * This is called, when the end location of the connection has been
     * moved by the user. The user has this probably done, to adjust the layout.
     * The connector may use this as a hint to improve the results for the next
     * call to findStart.
     */
    public void updateEndLocation(Point2D.Double p) {
    }
    
    public Point2D.Double getAnchor() {
        return Geom.center(getBounds());
    }
    
    public void updateAnchor(Point2D.Double p) {
    }
    
    public void read(DOMInput in) throws IOException {
        if (isStatePersistent) {
        isConnectToDecorator = in.getAttribute("connectToDecorator", false);
        isVisible = in.getAttribute("visible", false);
         }
        in.openElement("owner");
        this.owner = (Figure) in.readObject(0);
        in.closeElement();
    }
    
    public void write(DOMOutput out) throws IOException {
        if (isStatePersistent) {
        if (isConnectToDecorator) {
            out.addAttribute("connectToDecorator", true);
        }
        if (isVisible) {
            out.addAttribute("visible", true);
        }
        }
        out.openElement("owner");
        out.writeObject(getOwner());
        out.closeElement();
    }
    
}
