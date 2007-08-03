/*
 * @(#)Direction.java  2.0  2007-07-31
 *
 * Copyright (c) 2007 by the original authors of JHotDraw
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

import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * Enumeration of directions.
 *
 * @author Werner Randelshofer
 * @version 2.0 2007-07-31 Added directions NORTH_WEST, SOUTH_WEST,
 * NORTH_EAST and SOUTH_EAST. Added method getDirection.
 * <br>1.0 2007-04-29 Created.
 */
public enum Direction {
    NORTH,
    WEST,
    SOUTH,
    EAST,
    NORTH_WEST,
    SOUTH_WEST,
    NORTH_EAST,
    SOUTH_EAST;
    
    /**
     * Returns the direction from the provided start point to the end point.
     * Returns null, if both points are at the same location.
     */
    public static Direction getDirection(Point startPoint, Point endPoint) {
        int dx = endPoint.x - startPoint.x;
        int dy = endPoint.y - startPoint.y;
        
        if (dx == 0) {
            if (dy == 0) {
                return null;
            } else if (dy > 0) {
                return SOUTH;
            } else {
                return NORTH;
            }
        } else if (dx > 0) {
            if (dy == 0) {
                return EAST;
            } else if (dy > 0) {
                return SOUTH_EAST;
            } else {
                return NORTH_EAST;
            }
        } else {
            if (dy == 0) {
                return WEST;
            } else if (dy > 0) {
                return SOUTH_WEST;
            } else {
                return NORTH_WEST;
            }
        }
    }
    /**
     * Returns the direction from the provided start point to the end point.
     * Returns null, if both points are at the same location.
     */
    public static Direction getDirection(Point2D.Double startPoint, Point2D.Double endPoint) {
        double dx = endPoint.x - startPoint.x;
        double dy = endPoint.y - startPoint.y;
        
        if (dx == 0) {
            if (dy == 0) {
                return null;
            } else if (dy > 0) {
                return SOUTH;
            } else {
                return NORTH;
            }
        } else if (dx > 0) {
            if (dy == 0) {
                return EAST;
            } else if (dy > 0) {
                return SOUTH_EAST;
            } else {
                return NORTH_EAST;
            }
        } else {
            if (dy == 0) {
                return WEST;
            } else if (dy > 0) {
                return SOUTH_WEST;
            } else {
                return NORTH_WEST;
            }
        }
    }
}
