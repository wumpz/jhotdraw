/*
 * @(#)ConnectionEndHandle.java  3.0  2007-05-18
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

import org.jhotdraw.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
/**
 * Handle to reconnect the
 * end of a connection to another figure.
 *
 * @author Werner Randelshofer
 * @version 3.0 2007-05-18 Changed due to changes in the canConnect methods
 * of the ConnectionFigure interface. Shortened the name from 
 * ChangeConnectionEndHandle to ConnectionEndHandle.
 * <br>2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class ConnectionEndHandle extends AbstractConnectionHandle {
    
    /**
     * Constructs the connection handle for the given start figure.
     */
    public ConnectionEndHandle(ConnectionFigure owner) {
        super(owner);
    }
    
    /**
     * Sets the start of the connection.
     */
    protected void connect(Connector c) {
        getOwner().setEndConnector(c);
    }
    
    /**
     * Disconnects the start figure.
     */
    protected void disconnect() {
        getOwner().setEndConnector(null);
    }
    
    
    protected Connector getTarget() {
        return getOwner().getEndConnector();
    }
    
    /**
     * Sets the start point of the connection.
     */
    protected void setLocation(Point2D.Double p) {
        getOwner().willChange();
        getOwner().setEndPoint(p);
        getOwner().changed();
    }
    
    /**
     * Returns the start point of the connection.
     */
    protected Point2D.Double getLocation() {
        return getOwner().getEndPoint();
    }
    
    protected boolean canConnect(Connector existingEnd, Connector targetEnd) {
        return getOwner().canConnect(existingEnd, targetEnd);
    }
    
    protected int getBezierNodeIndex() {
        return getBezierFigure().getNodeCount() - 1;
    }
}
