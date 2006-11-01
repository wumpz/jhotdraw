/*
 * @(#)AttributeKeys.java  1.1  2006-07-09
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

import java.awt.*;
import java.util.*;
import org.jhotdraw.geom.*;
/**
 * Defines AttributeKeys used by the Figures in this package as well as some
 * helper methods.
 * <p>
 * Applications can have an AttributeKeys class of their own.
 *
 * @author Werner Randelshofer
 * @version 1.2 2006-07-09 Stroke dash factor added. 
 * <br>1.1 2006-06-07 Changed all values to double.
 * <br>1.0 23. 3. 2006 Created.
 */
public class AttributeKeys {
    /**
     * Fill color. The value of this attribute is a Color object.
     */
    public final static AttributeKey<Color> FILL_COLOR = new AttributeKey<Color>("fillColor", Color.white);
    
    public static enum WindingRule {
        /**
         * If WINDING_RULE is set to this value, an even-odd winding rule
         * is used for determining the interior of a path.  
         */
        EVEN_ODD,
        /**
         * If WINDING_RULE is set to this value, a non-zero winding rule
         * is used for determining the interior of a path.  
         */
        NON_ZERO
    }
    
    /**
     * Fill under stroke. The value of this attribute is a Boolean object.
     */
    public final static AttributeKey<WindingRule> WINDING_RULE = new AttributeKey<WindingRule>("windingRule", WindingRule.EVEN_ODD, false);

    public static enum Underfill {
        /**
         * If FILL_UNDER_STROKE is set to this value, the area under the
         * stroke will not be filled.
         */
        NONE,
        /**
         * If FILL_UNDER_STROKE is set to this value, the area under the stroke
         * is filled to the center of the stroke. This is the default behavior
         * of Graphics2D.fill(Shape), Graphics2D.draw(Shape) when using the
         * same shape object.
         */
        CENTER,
        /**
         * If FILL_UNDER_STROKE is set to this value, the area under the
         * stroke will be filled.
         */
        FULL
    }
    
    /**
     * Fill under stroke. The value of this attribute is a Boolean object.
     */
    public final static AttributeKey<Underfill> FILL_UNDER_STROKE = new AttributeKey<Underfill>("fillUnderStroke", Underfill.CENTER, false);
    
    /**
     * Stroke color. The value of this attribute is a Color object.
     */
    public final static AttributeKey<Color> STROKE_COLOR = new AttributeKey<Color>("strokeColor", Color.black);
    /**
     * Stroke width. A double used to construct a BasicStroke or the
     * outline of a DoubleStroke.
     */
    public final static AttributeKey<Double> STROKE_WIDTH = new AttributeKey<Double>("strokeWidth", 1d, false);
    /**
     * Factor for the stroke inner width. This is a double. The default value
     * is 2.
     */
    public final static AttributeKey<Double> STROKE_INNER_WIDTH_FACTOR = new AttributeKey<Double>("innerStrokeWidthFactor", 2d, false);
    /**
     * Stroke join. One of the BasicStroke.JOIN_... values used to
     * construct a BasicStroke.
     */
    public final static AttributeKey<Integer> STROKE_JOIN = new AttributeKey<Integer>("strokeJoin", BasicStroke.JOIN_MITER, false);
    /**
     * Stroke join. One of the BasicStroke.CAP_... values used to
     * construct a BasicStroke.
     */
    public final static AttributeKey<Integer> STROKE_CAP = new AttributeKey<Integer>("strokeCap", BasicStroke.CAP_BUTT, false);
    /**
     * Stroke miter limit factor. A double multiplied by total stroke width,
     * used to construct the miter limit of a BasicStroke.
     */
    public final static AttributeKey<Double> STROKE_MITER_LIMIT_FACTOR = new AttributeKey<Double>("strokeMiterLimitFactor", 3d, false);
    /**
     * An array of doubles used to specify the dash pattern in
     * a BasicStroke;
     */
    public final static AttributeKey<double[]> STROKE_DASHES = new AttributeKey<double[]>("strokeDashes", null);
    /**
     * A double used to specify the starting phase of the stroke dashes.
     */
    public final static AttributeKey<Double> STROKE_DASH_PHASE = new AttributeKey<Double>("strokeDashPhase", 0d, false);
    /**
     * A double used to specify the multiplication factor for the stroke dashes.
     * If this is null, the STROKE_WIDTH is used as the factor.
     */
    public final static AttributeKey<Double> STROKE_DASH_FACTOR = new AttributeKey<Double>("strokeDashFactor", null);
    
    
    public static enum StrokeType {
        /**
         * If STROKE_TYPE is set to this value, a BasicStroke instance is used
         * for stroking.
         */
        BASIC,
        /**
         * If STROKE_TYPE is set to this value, a DoubleStroke instance is used
         * for stroking.
         */
        DOUBLE
    }
    
    /**
     * Stroke type. The value of this attribute is either VALUE_STROKE_TYPE_BASIC
     * or VALUE_STROKE_TYPE_DOUBLE.
     * FIXME - Type should be an enumeration.
     */
    public final static AttributeKey<StrokeType> STROKE_TYPE = new AttributeKey<StrokeType>("strokeType", StrokeType.BASIC, false);
    
    public static enum StrokePlacement {
        /**
         * If STROKE_PLACEMENT is set to this value, the stroke is centered
         * on the path.
         */
        CENTER,
        /**
         * If STROKE_PLACEMENT is set to this value, the stroke is placed
         * inside of a closed path.
         */
        INSIDE,
        /**
         * If STROKE_PLACEMENT is set to this value, the stroke is placed
         * outside of a closed path.
         */
        OUTSIDE
    }
    /**
     * Stroke placement. The value is either VALUE_STROKE_PLACEMENT_INSIDE,
     * VALUE_STROKE_PLACEMENT_OUTSIDE, VALUE_STROKE_PLACEMENT_CENTER.
     * This only has effect for closed paths. On open paths, the stroke
     * is always centered on the path.
     * FIXME - Type should be an enumeration.
     */
    public final static AttributeKey<StrokePlacement> STROKE_PLACEMENT = new AttributeKey<StrokePlacement>("strokePlacement", StrokePlacement.CENTER, false);
    
    /**
     * The value of this attribute is a String object, which is used to
     * display the text of the figure.
     */
    public final static AttributeKey<String> TEXT = new AttributeKey<String>("text", null);
    
    /**
     * Text color. The value of this attribute is a Color object.
     */
    public final static AttributeKey<Color> TEXT_COLOR = new AttributeKey<Color>("textColor", Color.black);
    /**
     * Text shadow color. The value of this attribute is a Color object.
     */
    public final static AttributeKey<Color> TEXT_SHADOW_COLOR = new AttributeKey<Color>("textShadowColor", null);
    /**
     * Text shadow offset. The value of this attribute is a Dimension2DDouble object.
     */
    public final static AttributeKey<Dimension2DDouble> TEXT_SHADOW_OFFSET = new AttributeKey<Dimension2DDouble>("textShadowOffset", new Dimension2DDouble(1d,1d), false);
    /**
     * The value of this attribute is a Font object, which is used as a prototype
     * to create the font for the text.
     */
    public final static AttributeKey<Font> FONT_FACE = new AttributeKey<Font>("fontFace", new Font("VERDANA", Font.PLAIN, 10), false);
    /**
     * The value of this attribute is a double object.
     */
    public final static AttributeKey<Double> FONT_SIZE = new AttributeKey<Double>("fontSize", 12d, false);
    /**
     * The value of this attribute is a Boolean object.
     */
    public final static AttributeKey<Boolean> FONT_BOLD = new AttributeKey<Boolean>("fontBold", false, false);
    /**
     * The value of this attribute is a Boolean object.
     */
    public final static AttributeKey<Boolean> FONT_ITALIC = new AttributeKey<Boolean>("fontItalic", false, false);
    /**
     * The value of this attribute is a Boolean object.
     */
    public final static AttributeKey<Boolean> FONT_UNDERLINED = new AttributeKey<Boolean>("fontUnderlined", false, false);
    /**
     * The value of this attribute is a Liner object.
     */
    public final static AttributeKey<Liner> BEZIER_PATH_LAYOUTER = new AttributeKey<Liner>("bezierPathLayouter", null);
    
    public static final AttributeKey<LineDecoration> END_DECORATION = new AttributeKey<LineDecoration>("endDecoration",  null);
    
    public static final AttributeKey<LineDecoration> START_DECORATION = new AttributeKey<LineDecoration>("startDecoration", null);
    
    /**
     * The value of this attribute is a Insets2DDouble object.
     */
    public static final AttributeKey<Insets2DDouble> DECORATOR_INSETS = new AttributeKey<Insets2DDouble>("decoratorInsets", new Insets2DDouble(0,0,0,0), false);
    
    /**
     * The value of this attribute is a Insets2DDouble object.
     */
    public final static AttributeKey<Insets2DDouble> LAYOUT_INSETS = new AttributeKey<Insets2DDouble>("borderInsets", new Insets2DDouble(0,0,0,0));

    public static enum Orientation {
        NORTH,
        NORTH_EAST,
        EAST,
        SOUTH_EAST,
        SOUTH,
        SOUTH_WEST,
        WEST,
        NORTH_WEST
    }
    
    /**
     * Specifies the direction of the triangle. Possible values are
     * NORTH, SOUTH, EAST,
     * WEST.
     */
    public final static AttributeKey<Orientation> ORIENTATION = new AttributeKey<Orientation>("orientation", Orientation.NORTH);
    /**
     * A set with all attributes defined by this class.
     */
    public final static Set<AttributeKey> supportedAttributes;
    public final static Map<String, AttributeKey> supportedAttributeMap;
    static {
        HashSet<AttributeKey> as = new HashSet<AttributeKey>();
        as.addAll(Arrays.asList(new AttributeKey[] {
            FILL_COLOR,
            FILL_UNDER_STROKE,
            STROKE_COLOR,
            STROKE_WIDTH,
            STROKE_INNER_WIDTH_FACTOR,
            STROKE_JOIN,
            STROKE_CAP,
            STROKE_MITER_LIMIT_FACTOR,
            STROKE_DASHES,
            STROKE_DASH_PHASE,
            STROKE_TYPE,
            STROKE_PLACEMENT,
            TEXT,
            TEXT_COLOR,
            TEXT_SHADOW_COLOR,
            TEXT_SHADOW_OFFSET,
            FONT_FACE,
            FONT_SIZE,
            FONT_BOLD,
            FONT_ITALIC,
            FONT_UNDERLINED,
            BEZIER_PATH_LAYOUTER,
            END_DECORATION,
            START_DECORATION,
            DECORATOR_INSETS,
            ORIENTATION,
            WINDING_RULE,
        }));
        supportedAttributes = Collections.unmodifiableSet(as);
        HashMap<String,AttributeKey> am = new HashMap<String,AttributeKey>();
        for (AttributeKey a: as) {
            am.put(a.getKey(), a);
        }
        supportedAttributeMap = Collections.unmodifiableMap(am);
    }
    
    
    /**
     * Convenience method for computing the total stroke widht from the
     * STROKE_WIDTH, STROKE_INNER_WIDTH and STROKE_TYPE attributes.
     */
    public static double getStrokeTotalWidth(Figure f) {
        switch (STROKE_TYPE.get(f)) {
            case BASIC :
            default :
                return STROKE_WIDTH.get(f);
                // break; not reached
            case DOUBLE :
                return STROKE_WIDTH.get(f) * (1d + STROKE_INNER_WIDTH_FACTOR.get(f));
                // break; not reached
        }
    }
    
    public static Stroke getStroke(Figure f) {
        double strokeWidth = STROKE_WIDTH.get(f);
        double dashFactor = STROKE_DASH_FACTOR.get(f) != null ? STROKE_DASH_FACTOR.get(f) : strokeWidth;
        double[] ddashes = STROKE_DASHES.get(f);
        float[] dashes = null;
        if (ddashes != null) {
            dashes = new float[ddashes.length];
            for (int i=0; i < dashes.length; i++) {
                dashes[i] = (float) (ddashes[i] * dashFactor);
            }
        }
        
        switch (STROKE_TYPE.get(f)) {
            case BASIC :
            default :
                return new BasicStroke((float) strokeWidth, BasicStroke.CAP_BUTT,
                        STROKE_JOIN.get(f) ,
                        Math.max(1f, (float) (STROKE_MITER_LIMIT_FACTOR.get(f) * strokeWidth)),
                        dashes, (float) (STROKE_DASH_PHASE.get(f) * dashFactor));
                //not reached
                
            case DOUBLE :
                return new DoubleStroke(
                        (float) (STROKE_INNER_WIDTH_FACTOR.get(f) * strokeWidth),
                        (float) strokeWidth, BasicStroke.CAP_BUTT, STROKE_JOIN.get(f),
                        Math.max(1f, (float) (STROKE_MITER_LIMIT_FACTOR.get(f) * strokeWidth)),
                        dashes, (float) (STROKE_DASH_PHASE.get(f).floatValue() * dashFactor));
                //not reached
        }
    }
    
    public static Font getFont(Figure f) {
        Font prototype = FONT_FACE.get(f);
        if (prototype == null) {
            return null;
        }
        if (getFontStyle(f) != Font.PLAIN) {
            return prototype.deriveFont(getFontStyle(f), FONT_SIZE.get(f).floatValue());
        } else {
            return prototype.deriveFont(FONT_SIZE.get(f).floatValue());
        }
    }
    public static int getFontStyle(Figure f) {
        int style = Font.PLAIN;
        if (FONT_BOLD.get(f)) style |= Font.BOLD;
        if (FONT_ITALIC.get(f)) style |= Font.ITALIC;
        return style;
    }
    /**
     * Returns the distance, that a Rectangle needs to grow (or shrink) to
     * fill its shape as specified by the FILL_UNDER_STROKE and STROKE_POSITION
     * attributes of a figure.
     * The value returned is the number of units that need to be grown (or shrunk)
     * perpendicular to a stroke on an outline of the shape.
     */
    public static double getPerpendicularFillGrowth(Figure f) {
        double grow;
        double strokeWidth = AttributeKeys.getStrokeTotalWidth(f);
        StrokePlacement placement = STROKE_PLACEMENT.get(f);
        switch (FILL_UNDER_STROKE.get(f)) {
            case FULL :
                switch (placement) {
                    case INSIDE :
                        grow = 0f;
                        break;
                    case OUTSIDE :
                        grow = strokeWidth;
                        break;
                    case CENTER :
                    default :
                        grow = strokeWidth / 2d;
                        break;
                }
                break;
            case NONE :
                switch (placement) {
                    case INSIDE :
                        grow = -strokeWidth;
                        break;
                    case OUTSIDE :
                        grow = 0f;
                        break;
                    case CENTER :
                    default :
                        grow = strokeWidth / -2d;
                        break;
                }
                break;
            case CENTER :
            default :
                switch (placement) {
                    case INSIDE :
                        grow = strokeWidth / -2d;
                        break;
                    case OUTSIDE :
                        grow = strokeWidth / 2d;
                        break;
                    case CENTER :
                    default :
                        grow = 0d;
                        break;
                }
                break;
        }
        return grow;
    }
    /**
     * Returns the distance, that a Rectangle needs to grow (or shrink) to
     * draw its shape as specified by the FILL_UNDER_STROKE and STROKE_POSITION
     * attributes of a figure.
     * The value returned is the number of units that need to be grown (or shrunk)
     * perpendicular to a stroke on an outline of the shape.
     */
    public static double getPerpendicularDrawGrowth(Figure f) {
        double grow;
        
        double strokeWidth = AttributeKeys.getStrokeTotalWidth(f);
        StrokePlacement placement = STROKE_PLACEMENT.get(f);
        switch (placement) {
            case INSIDE :
                grow = strokeWidth / -2d;
                break;
            case OUTSIDE :
                grow = strokeWidth / 2d;
                break;
            case CENTER :
            default :
                grow = 0f;
                break;
        }
        return grow;
    }
    /**
     * Returns the distance, that a Rectangle needs to grow (or shrink) to
     * make hit detections on a shape as specified by the FILL_UNDER_STROKE and STROKE_POSITION
     * attributes of a figure.
     * The value returned is the number of units that need to be grown (or shrunk)
     * perpendicular to a stroke on an outline of the shape.
     */
    public static double getPerpendicularHitGrowth(Figure f) {
        double grow;
        if (STROKE_COLOR.get(f) == null) {
            grow = getPerpendicularFillGrowth(f);
        } else {
            double strokeWidth = AttributeKeys.getStrokeTotalWidth(f);
            grow = getPerpendicularDrawGrowth(f) + strokeWidth / 2d;
        }
        return grow;
    }
    
}
