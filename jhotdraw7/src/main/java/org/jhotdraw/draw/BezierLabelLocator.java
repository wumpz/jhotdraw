/*
 * @(#)PolyLineDecorationLocator.java  1.0  3. Februar 2004
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

import org.jhotdraw.geom.Dimension2DDouble;
import org.jhotdraw.geom.Geom;
import org.jhotdraw.util.*;
import java.awt.*;
import java.awt.geom.*;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;
import org.jhotdraw.xml.DOMStorable;

/**
 * This locator locates a position relative to a polyline.
 * The position is chosen in a way, which is suitable for labeling the polyline.
 * The preferredSize of the label is used to determine its location.
 *
 * @author  Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precison coordinates.
 * <br>1.0 3. Februar 2004  Created.
 */
public class BezierLabelLocator implements Locator, DOMStorable {
    private double relativePosition;
    private double angle;
    private double distance;
    
    /**
     * Creates a new instance.
     * This constructor is for use by DOMStorable only.
     */
    public BezierLabelLocator() {
    }
    /** Creates a new locator.
     *
     * @param relativePosition The relative position of the label on the polyline.
     * 0.0 specifies the start of the polyline, 1.0 the
     * end of the polyline. Values between 0.0 and 1.0 are relative positions
     * on the polyline.
     * @param angle The angle of the distance vector.
     * @param distance The length of the distance vector.
     */
    public BezierLabelLocator(double relativePosition, double angle, double distance) {
        this.relativePosition = relativePosition;
        this.angle = angle;
        this.distance = distance;
    }
    
    public Point2D.Double locate(Figure owner) {
        return getRelativePoint((BezierFigure) owner);
    }
    public Point2D.Double locate(Figure owner, Figure label) {
        Point2D.Double relativePoint = getRelativeLabelPoint((BezierFigure) owner, label);
        return relativePoint;
    }
    
    /**
     * Returns a Point2D.Double on the polyline that is at the provided relative position.
     */
    public Point2D.Double getRelativePoint(BezierFigure owner) {
        Point2D.Double point = owner.getPointOnPath((float) relativePosition, 3);
        Point2D.Double nextPoint = owner.getPointOnPath(
                (relativePosition < 0.5) ? (float) relativePosition + 0.1f : (float) relativePosition - 0.1f,
                3);
        
        double dir = Math.atan2(nextPoint.y - point.y, nextPoint.x - point.x);
        if (relativePosition >= 0.5) {
            dir += Math.PI;
        }
        double alpha = dir + angle;
        
        Point2D.Double p = new Point2D.Double(
                point.x + distance * Math.cos(alpha),
                point.y + distance * Math.sin(alpha)
                );
        
        if (Double.isNaN(p.x)) p = point;
        
        return p;
    }
    
    
    /**
     * Returns a Point2D.Double on the polyline that is at the provided relative position.
     * XXX - Implement this and move it to BezierPath
     */
    public Point2D.Double getRelativeLabelPoint(BezierFigure owner, Figure label) {
        // Get a point on the path an the next point on the path
        Point2D.Double point = owner.getPointOnPath((float) relativePosition, 3);
        if (point == null) {
            return new Point2D.Double(0,0);
        }
        Point2D.Double nextPoint = owner.getPointOnPath(
                (relativePosition < 0.5) ? (float) relativePosition + 0.1f : (float) relativePosition - 0.1f,
                3);
        
        double dir = Math.atan2(nextPoint.y - point.y, nextPoint.x - point.x);
        if (relativePosition >= 0.5) {
            dir += Math.PI;
        }
        double alpha = dir + angle;
        
        Point2D.Double p = new Point2D.Double(
                point.x + distance * Math.cos(alpha),
                point.y + distance * Math.sin(alpha)
                );
        if (Double.isNaN(p.x)) p = point;
        
        Dimension2DDouble labelDim = label.getPreferredSize();
        if (relativePosition == 0.5 && 
                p.x >= point.x - distance / 2 && 
                p.x <= point.x + distance / 2) {
            if (p.y >= point.y) {
                // South East
                return new Point2D.Double(p.x - labelDim.width / 2, p.y);
            } else {
                // North East
                return new Point2D.Double(p.x - labelDim.width / 2, p.y - labelDim.height);
            }
        } else {
            if (p.x >= point.x) {
                if (p.y >= point.y) {
                    // South East
                    return new Point2D.Double(p.x, p.y);
                } else {
                    // North East
                    return new Point2D.Double(p.x, p.y - labelDim.height);
                }
            } else {
                if (p.y >= point.y) {
                    // South West
                    return new Point2D.Double(p.x - labelDim.width,  p.y);
                } else {
                    // North West
                    return new Point2D.Double(p.x - labelDim.width, p.y - labelDim.height);
                }
            }
        }
/*
        int percentage = (int) (relativePosition * 100);
 
        int segment; // relative segment
        Point2D.Double segPoint; // relative Point2D.Double on the segment
        int nPoints = owner.getPointCount();
        Point2D.Double[] Points = owner.getPoints();
 
        if (nPoints < 2) return new Point2D.Double(0, 0);
 
        switch (percentage) {
            case 0 :
                segment = 0;
                segPoint = owner.getStartPoint();
                break;
            case 100 :
                segment = owner.getPointCount() - 2;
                segPoint = owner.getEndPoint();
                break;
            default :
                double totalLength = 0d;
                double[] segLength = new double[nPoints - 1];
                for (int i=1; i < nPoints; i++) {
                    segLength[i-1] = Geom.length(Points[i-1].x, Points[i-1].y, Points[i].x, Points[i].y);
                    totalLength += segLength[i-1];
                }
                double relativeProgress = percentage * totalLength / 101d;
                segment = 0;
                double segMin = 0d;
                for (segment=0; segment < segLength.length - 1; segment++) {
                    if (segMin + segLength[segment] > relativeProgress) break;
                    segMin += segLength[segment];
                }
 
                // Compute the relative Point2D.Double on the line
                segPoint = new Point2D.Double();
                relativeProgress -= segMin;
                segPoint.x = (int) ((Points[segment].x * (segLength[segment] - relativeProgress) + Points[segment + 1].x * relativeProgress) / segLength[segment] +.5);
                segPoint.y = (int) ((Points[segment].y * (segLength[segment] - relativeProgress) + Points[segment + 1].y * relativeProgress) / segLength[segment] +.5);
 
                break;
        }
 
        Dimension2DDouble labelDim = label.getPreferredSize();
 
        Line2D.Double line = new Line2D.Double(Points[segment].x, Points[segment].y, Points[segment + 1].x, Points[segment + 1].y);
        double dir = Math.atan2(Points[segment + 1].y - Points[segment].y, Points[segment + 1].x - Points[segment].x);
        double alpha = dir + angle;
 
        Point2D.Double p = new Point2D.Double(
        (int) (segPoint.x + distance * Math.cos(alpha)),
        (int) (segPoint.y + distance * Math.sin(alpha))
        );
 
        if (p.x >= segPoint.x) {
            if (p.y >= segPoint.y) {
                // South East
                return new Point2D.Double(p.x, p.y);
            } else {
                // North East
                return new Point2D.Double(p.x, p.y - labelDim.height);
            }
        } else {
            if (p.y >= segPoint.y) {
                // South West
                return new Point2D.Double(p.x - labelDim.width,  p.y);
            } else {
                // North West
                return new Point2D.Double(p.x - labelDim.width, p.y - labelDim.height);
            }
        }*/
    }
    
    public void read(DOMInput in) {
        relativePosition = in.getAttribute("relativePosition", 0d);
        angle = in.getAttribute("angle", 0d);
        distance = in.getAttribute("distance", 0);
        
    }
    
    public void write(DOMOutput out) {
        out.addAttribute("relativePosition", relativePosition);
        out.addAttribute("angle", angle);
        out.addAttribute("distance", distance);
        
    }
    
}
