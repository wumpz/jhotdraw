/*
 * @(#)ConnectionStartHandle.java  3.0  2007-05-18
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
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
/**
 * Handle to reconnect the
 * start of a connection to another figure.
 *
 * @author Werner Randelshofer
 * @version 3.0 2007-05-18 Changed due to changes in the canConnect methods
 * of the ConnectionFigure interface. Shortened the name from 
 * ChangeConnectionStartHandle to ConnectionStartHandle.
 * <br>2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class ConnectionStartHandle extends AbstractConnectionHandle {
    
    /**
     * Constructs the connection handle for the given start figure.
     */
    public ConnectionStartHandle(ConnectionFigure owner) {
        super(owner);
    }
    
    /**
     * Sets the start of the connection.
     */
    protected void connect(Connector c) {
        getOwner().setStartConnector(c);
    }
    
    /**
     * Disconnects the start figure.
     */
    protected void disconnect() {
        getOwner().setStartConnector(null);
    }
    
    
    protected Connector getTarget() {
        return getOwner().getStartConnector();
    }
    
    /**
     * Sets the start point of the connection.
     */
    protected void setLocation(Point2D.Double p) {
        getOwner().willChange();
        getOwner().setStartPoint(p);
        getOwner().changed();
    }
    
    /**
     * Returns the start point of the connection.
     */
    protected Point2D.Double getLocation() {
        return getOwner().getStartPoint();
    }
    
    protected boolean canConnect(Connector existingEnd, Connector targetEnd) {
        return getOwner().canConnect(targetEnd, existingEnd);
    }

    protected int getBezierNodeIndex() {
        return 0;
    }
    
}