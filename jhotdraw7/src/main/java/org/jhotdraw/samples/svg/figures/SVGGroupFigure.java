/*
 * @(#)SVGGroup.java  1.0  July 8, 2006
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

package org.jhotdraw.samples.svg.figures;

import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.samples.svg.*;
import org.jhotdraw.samples.svg.SVGConstants;
import org.jhotdraw.xml.*;
/**
 * SVGGroup.
 *
 * @author Werner Randelshofer
 * @version 1.0 July 8, 2006 Created.
 */
public class SVGGroupFigure extends GroupFigure implements SVGFigure {
    
    /** Creates a new instance. */
    public SVGGroupFigure() {
        SVGAttributeKeys.setDefaults(this);
    }
/*
    public void drawFigure(Graphics2D g) {
        super.drawFigure(g);
        g.setStroke(new BasicStroke());
        g.setColor(Color.red);
        g.draw(getBounds());
        g.setColor(Color.blue);
        g.draw(getFigureDrawBounds());
    }*/
    @Override public LinkedList<Handle> createHandles(int detailLevel) {
        LinkedList<Handle> handles;
        if (detailLevel == 0) {
            handles = (LinkedList<Handle>) super.createHandles(detailLevel);
            handles.add(new RotateHandle(this));
        } else {
      handles = new LinkedList<Handle>();
        }
        return handles;
    }
    
    @Override final public void write(DOMOutput out) throws IOException {
        throw new UnsupportedOperationException("Use SVGStorableOutput to write this Figure.");
    }
    @Override final public void read(DOMInput in) throws IOException {
        throw new UnsupportedOperationException("Use SVGStorableInput to read this Figure.");
    }
    public boolean isEmpty() {
        return getChildCount() == 0;
    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(getClass().getName().substring(getClass().getName().lastIndexOf('.')+1));
        buf.append('@');
        buf.append(hashCode());
        if (getChildCount() > 0) {
            buf.append('(');
            for (Iterator<Figure> i = getChildren().iterator(); i.hasNext(); ) {
                Figure child = i.next();
                buf.append(child);
                if (i.hasNext()) {
                buf.append(',');
                }
            }
            buf.append(')');
        }
        return buf.toString();
    }
}
