/*
 * @(#)SVGAttributeKeys.java  1.0  December 9, 2006
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

import java.awt.geom.*;
import org.jhotdraw.draw.*;

/**
 * SVGAttributeKeys.
 *
 * @author Werner Randelshofer
 * @version 1.0 December 9, 2006 Created.
 */
public class SVGAttributeKeys extends AttributeKeys {
    
    /**
     * Specifies the transform of a Figure.
     */
    public final static AttributeKey<AffineTransform>TRANSFORM = new AttributeKey<AffineTransform>("transform", null, true);
    
    public enum TextAnchor {
        START, MIDDLE, END
    }
    /**
     * Specifies the text anchor of a SVGText figure.
     */
    public final static AttributeKey<TextAnchor> TEXT_ANCHOR = new AttributeKey<TextAnchor>("textAnchor",TextAnchor.START, false);
   
    /**
     * Specifies the fill gradient of a SVG figure.
     */
    public final static AttributeKey<Gradient> FILL_GRADIENT = new AttributeKey<Gradient>("fillGradient", null);
    
    /**
     * Specifies the stroke gradient of a SVG figure.
     */
    public final static AttributeKey<Gradient> STROKE_GRADIENT = new AttributeKey<Gradient>("strokeGradient", null);
}
