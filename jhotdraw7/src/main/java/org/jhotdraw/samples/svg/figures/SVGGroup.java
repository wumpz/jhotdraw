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

import java.awt.geom.*;
import java.io.*;
import java.util.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.samples.svg.*;
import org.jhotdraw.xml.*;
/**
 * SVGGroup.
 *
 * @author Werner Randelshofer
 * @version 1.0 July 8, 2006 Created.
 */
public class SVGGroup extends GroupFigure implements SVGFigure {
    
    /** Creates a new instance. */
    public SVGGroup() {
        SVGUtil.setDefaults(this);
    }
    
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
    
    @Override public void read(DOMInput in) throws IOException {
        readAttributes(in);
        for (int i=0, n = in.getElementCount(); i < n; i++) {
            in.openElement(i);
            String name = in.getTagName();
            if (name.equals("pattern")) {
                // We ignore "pattern" elements for now.
                in.closeElement();
            } else if (name.equals("color-profile")) {
                // We ignore "pattern" elements for now.
                in.closeElement();
           } else if (name.equals("defs")) {
                // We ignore "defs" elements for now.
                in.closeElement();
            } else if (name.equals("use")) {
                // We ignore "use" elements for now.
                in.closeElement();
            } else if (name.equals("script")) {
                // We ignore "script" elements for now.
                in.closeElement();
            } else if (name.equals("filter")) {
                // We ignore "filter" elements for now.
                in.closeElement();
            } else if (name.equals("title")) {
                // We ignore "title" elements for now.
                in.closeElement();
            } else if (name.equals("desc")) {
                // We ignore "desc" elements for now.
                in.closeElement();
            } else if (name.equals("switch")) {
                // We ignore "switch" elements for now.
                in.closeElement();
            } else if (name.equals("radialGradient")) {
                // We ignore "radialGradient" elements for now.
                in.closeElement();
            } else if (name.equals("linearGradient")) {
                // We ignore "linearGradient" elements for now.
                in.closeElement();
            } else {
                in.closeElement();
                Object f = (Object) in.readObject(i);
                if (f instanceof SVGDrawing) {
                    SVGGroup g = new SVGGroup();
                    g.willChange();
                    for (Figure child : ((SVGDrawing) f).getFigures()) {
                        g.basicAdd(child);
                    }
                    g.changed();
                    if (! g.isEmpty()) {
                    add(g);
                    }
                } else if (f instanceof SVGFigure) {
                    if (!((SVGFigure) f).isEmpty()) {
                    add((SVGFigure) f);
                    }
                } else {
                    throw new IOException("Unexpected child "+f);
                }
            }
        }
        AffineTransform tx = SVGUtil.getTransform(in, "transform");
        for (Figure child : getChildren()) {
            child.basicTransform(tx);
        }
        invalidateBounds();
    }
    protected void readAttributes(DOMInput in) throws IOException {
        SVGUtil.readAttributes(this, in);
    }
    
    @Override public void write(DOMOutput out) throws IOException {
        for (Figure child : getChildren()) {
            out.writeObject(child);
        }
        writeAttributes(out);
    }
    protected void writeAttributes(DOMOutput out) throws IOException {
        SVGUtil.writeAttributes(this, out);
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
