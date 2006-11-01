/*
 * @(#)StickyChopConnector.java  2.0  2006-01-14
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

import java.io.IOException;
import org.jhotdraw.geom.Geom;
import org.jhotdraw.util.*;
import java.awt.*;
import java.awt.geom.*;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;

/**
 * A StickyChopConnector locates connection Points by
 * choping the connection between the centers of the
 * two figures at the display box.
 * 
 * The location of the connection Point2D.Double is computed once,
 * when the user connects the figure. Moving the figure
 * around will not change the location.
 * 
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precison coordinates.
 * <br>1.0 5. Februar 2004  Created.
 */
public class StickyChopConnector extends ChopBoxConnector {
    private float angle;
    
    /** Creates a new instance.
     * Only used for storable.
     */
    public StickyChopConnector() {
    }
    
    public void draw(Graphics2D g) {
        g.setColor(Color.blue);
        g.setStroke(new BasicStroke());
        g.draw(getBounds());
    }
    
    public void setAngle(float angle) {
        this.angle = angle;
    }
    public double getAngle() {
        return angle;
    }
    
    public StickyChopConnector(Figure owner, Point2D.Double p) {
        super(owner);
        this.angle = (float) Geom.pointToAngle(owner.getBounds(), p);
    }
    
    public void updateAnchor(Point2D.Double p) {
        this.angle = (float) Geom.pointToAngle(getOwner().getBounds(), p);
    }    
    public Point2D.Double getAnchor() {
        return Geom.angleToPoint(getOwner().getBounds(), angle);
    }
    protected Point2D.Double chop(Figure target, Point2D.Double from) {
            return Geom.angleToPoint(target.getBounds(), angle);
    }

    public String getParameters() {
        return Float.toString((float) (angle / Math.PI * 180));
    }
    public void read(DOMInput in) throws IOException {
        super.read(in);
        angle = (float) in.getAttribute("angle", 0.0);
    }
    public void write(DOMOutput out) throws IOException {
        super.write(out);
        out.addAttribute("angle", angle);
    }
}
