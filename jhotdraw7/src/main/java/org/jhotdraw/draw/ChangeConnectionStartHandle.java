/*
 * @(#)ChangeConnectionStartHandle.java  2.0  2006-01-14
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

import org.jhotdraw.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
/**
 * Handle to reconnect the
 * start of a connection to another figure.
 *
 * @author Werner Randelshofer
 * @version 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class ChangeConnectionStartHandle extends ChangeConnectionHandle {
    
    /**
     * Constructs the connection handle for the given start figure.
     */
    public ChangeConnectionStartHandle(Figure owner) {
        super(owner);
    }
    
    /**
     * Sets the start of the connection.
     */
    protected void connect(Connector c) {
        getConnection().setStartConnector(c);
    }
    
    /**
     * Disconnects the start figure.
     */
    protected void disconnect() {
        getConnection().setStartConnector(null);
    }
    
    
    protected Connector getTarget() {
        return getConnection().getStartConnector();
    }
    
    /**
     * Sets the start point of the connection.
     */
    protected void setLocation(Point2D.Double p) {
        getConnection().willChange();
        getConnection().setStartPoint(p);
        getConnection().changed();
    }
    
    /**
     * Returns the start point of the connection.
     */
    protected Point2D.Double getLocation() {
        return getConnection().getStartPoint();
    }
    
    protected boolean canConnect(Figure existingEnd, Figure targetEnd) {
        return getConnection().canConnect(targetEnd, existingEnd);
    }
    
}