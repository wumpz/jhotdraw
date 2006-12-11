/*
 * @(#)SVGAttributedFigure.java  1.0  December 10, 2006
 *
 * Copyright (c) 2006 Werner Randelshofer
 * Staldenmattweg 2, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Werner Randelshofer. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Werner Randelshofer.
 */

package org.jhotdraw.samples.svg;

import org.jhotdraw.draw.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.io.*;
import static org.jhotdraw.samples.svg.SVGAttributeKeys.*;
import org.jhotdraw.geom.*;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;
/**
 * SVGAttributedFigure.
 *
 * @author Werner Randelshofer
 * @version 1.0 December 10, 2006 Created.
 */
public abstract class SVGAttributedFigure extends AttributedFigure {
    
    /** Creates a new instance. */
    public SVGAttributedFigure() {
    }
    
    public void drawFigure(Graphics2D g) {
        if (FILL_COLOR.get(this) != null ||
                FILL_GRADIENT.get(this) != null) {
            g.setColor(AttributeKeys.FILL_COLOR.get(this));
            if (FILL_GRADIENT.get(this) != null) {
                g.setPaint(FILL_GRADIENT.get(this).getPaint(this));
            }
            drawFill(g);
        }
        if ((STROKE_COLOR.get(this) != null ||
                STROKE_GRADIENT.get(this) != null)
                && STROKE_WIDTH.get(this) > 0d) {
            g.setStroke(AttributeKeys.getStroke(this));
            g.setColor(STROKE_COLOR.get(this));
            if (FILL_GRADIENT.get(this) != null) {
                g.setPaint(FILL_GRADIENT.get(this).getPaint(this));
            }
            
            drawStroke(g);
        }
        if (TEXT_COLOR.get(this) != null) {
            if (TEXT_SHADOW_COLOR.get(this) != null &&
                    TEXT_SHADOW_OFFSET.get(this) != null) {
                Dimension2DDouble d = TEXT_SHADOW_OFFSET.get(this);
                g.translate(d.width, d.height);
                g.setColor(TEXT_SHADOW_COLOR.get(this));
                drawText(g);
                g.translate(-d.width,-d.height);
            }
            g.setColor(TEXT_COLOR.get(this));
            drawText(g);
        }
        if (isConnectorsVisible()) {
            drawConnectors(g);
        }
    }
}
