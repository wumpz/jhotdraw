/*
 * @(#)AttributeKeys.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import org.jhotdraw.draw.figure.Figure;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import org.jhotdraw.draw.decoration.LineDecoration;
import org.jhotdraw.draw.liner.Liner;
import org.jhotdraw.geom.Dimension2DDouble;
import org.jhotdraw.geom.DoubleStroke;
import org.jhotdraw.geom.Insets2D;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * Defines a put of well known {@link Figure} attributes.
 * <p>
 * If you are developing an applications that uses a different put or an extended put of attributes,
 * it is recommended to create a new AttributeKeys class, and to define all needed AttributeKeys as
 * static variables in there.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class AttributeKeys {

    private static final ResourceBundleUtil LABELS = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
    /**
     * Canvas fill color. The value of this attribute is a Color object. This attribute is used by a
     * Drawing object to specify the fill color of the drawing. The default value is white.
     */
    public static final AttributeKey<Color> CANVAS_FILL_COLOR = new AttributeKey<Color>("canvasFillColor", Color.class, Color.white, true, LABELS);
    /**
     * Canvas fill opacity. The value of this attribute is a Double object. This is a value between
     * 0 and 1 whereas 0 is translucent and 1 is fully opaque.
     */
    public static final AttributeKey<Double> CANVAS_FILL_OPACITY = new AttributeKey<Double>("canvasFillOpacity", Double.class, 1d, false, LABELS);
    /**
     * The width of the canvas. The value of this attribute is a Double object. This is a value
     * between 1 and Double.MAX_VALUE. If the value is null, the width is dynamically adapted to the
     * content of the drawing.
     */
    public static final AttributeKey<Double> CANVAS_WIDTH = new AttributeKey<Double>("canvasWidth", Double.class, null, true, LABELS);
    /**
     * The height of the canvas. The value of this attribute is a Double object. This is a value
     * between 1 and Double.MAX_VALUE. If the value is null, the height is dynamically adapted to
     * the content of the drawing.
     */
    public static final AttributeKey<Double> CANVAS_HEIGHT = new AttributeKey<Double>("canvasHeight", Double.class, null, true, LABELS);
    /**
     * Figure fill color. The value of this attribute is a Color object.
     */
    public static final AttributeKey<Color> FILL_COLOR = new AttributeKey<Color>("fillColor", Color.class, Color.white, true, LABELS);
    /**
     * Whether to path a BezierFigure is closed. The value of this attribute is a Boolean object.
     */
    public static final AttributeKey<Boolean> PATH_CLOSED = new AttributeKey<Boolean>("pathClosed", Boolean.class, false, false, LABELS);
    /**
     * Whether an unclosed path of a BezierFigure is filled. The value of this attribute is a
     * Boolean object.
     */
    public static final AttributeKey<Boolean> UNCLOSED_PATH_FILLED = new AttributeKey<Boolean>("unclosedPathFilled", Boolean.class, false, false, LABELS);

    public static enum WindingRule {
        /**
         * If WINDING_RULE is put to this value, an even-odd winding rule is used for determining
         * the interior of a path.
         */
        EVEN_ODD,
        /**
         * If WINDING_RULE is put to this value, a non-zero winding rule is used for determining the
         * interior of a path.
         */
        NON_ZERO
    }
    /**
     * Fill under stroke. The value of this attribute is a Boolean object.
     */
    public static final AttributeKey<WindingRule> WINDING_RULE = new AttributeKey<WindingRule>("windingRule", WindingRule.class, WindingRule.EVEN_ODD, false, LABELS);

    public static enum Underfill {
        /**
         * If FILL_UNDER_STROKE is put to this value, the area under the stroke will not be filled.
         */
        NONE,
        /**
         * If FILL_UNDER_STROKE is put to this value, the area under the stroke is filled to the
         * center of the stroke. This is the default behavior of Graphics2D.fill(Shape),
         * Graphics2D.draw(Shape) when using the same shape object.
         */
        CENTER,
        /**
         * If FILL_UNDER_STROKE is put to this value, the area under the stroke will be filled.
         */
        FULL
    }
    /**
     * Fill under stroke. The value of this attribute is a Boolean object.
     */
    public static final AttributeKey<Underfill> FILL_UNDER_STROKE = new AttributeKey<Underfill>("fillUnderStroke", Underfill.class, Underfill.CENTER, false, LABELS);
    /**
     * Stroke color. The value of this attribute is a Color object.
     */
    public static final AttributeKey<Color> STROKE_COLOR = new AttributeKey<Color>("strokeColor", Color.class, Color.black, true, LABELS);
    /**
     * Stroke width. A double used to construct a BasicStroke or the outline of a DoubleStroke.
     */
    public static final AttributeKey<Double> STROKE_WIDTH = new AttributeKey<Double>("strokeWidth", Double.class, 1d, false, LABELS);
    /**
     * Factor for the stroke inner width. This is a double. The default value is 2.
     * <p>
     * FIXME - This is not flexible enough. Lets replace this with a
     * STROKE_STRIPES_ARRAY&lt;Double[]&gt; and a IS_STROKE_STRIPES_FACTOR.
     */
    public static final AttributeKey<Double> STROKE_INNER_WIDTH_FACTOR = new AttributeKey<Double>("innerStrokeWidthFactor", Double.class, 2d, false, LABELS);
    /**
     * Stroke join. One of the BasicStroke.JOIN_... values used to construct a BasicStroke.
     */
    public static final AttributeKey<Integer> STROKE_JOIN = new AttributeKey<Integer>("strokeJoin", Integer.class, BasicStroke.JOIN_MITER, false, LABELS);
    /**
     * Stroke join. One of the BasicStroke.CAP_... values used to construct a BasicStroke.
     */
    public static final AttributeKey<Integer> STROKE_CAP = new AttributeKey<Integer>("strokeCap", Integer.class, BasicStroke.CAP_BUTT, false, LABELS);
    /**
     * Stroke miter limit factor. A double multiplied by total stroke width, used to construct the
     * miter limit of a BasicStroke.
     */
    public static final AttributeKey<Double> STROKE_MITER_LIMIT = new AttributeKey<Double>("strokeMiterLimitFactor", Double.class, 3d, false, LABELS);
    /**
     * A boolean used to indicate whether STROKE_MITER_LIMIT is a factor of STROKE_WIDTH, or whether
     * it represents an absolute value.
     */
    public static final AttributeKey<Boolean> IS_STROKE_MITER_LIMIT_FACTOR = new AttributeKey<Boolean>("isStrokeMiterLimitFactor", Boolean.class, true, false, LABELS);
    /**
     * An array of doubles used to specify the dash pattern in a BasicStroke;
     */
    public static final AttributeKey<double[]> STROKE_DASHES = new AttributeKey<double[]>("strokeDashes", double[].class, null, true, LABELS);
    /**
     * A double used to specify the starting phase of the stroke dashes.
     */
    public static final AttributeKey<Double> STROKE_DASH_PHASE = new AttributeKey<Double>("strokeDashPhase", Double.class, 0d, false, LABELS);
    /**
     * A boolean used to indicate whether STROKE_DASHES and STROKE_DASH_PHASE shall be interpreted
     * as factors of STROKE_WIDTH, or whether they are absolute values.
     */
    public static final AttributeKey<Boolean> IS_STROKE_DASH_FACTOR = new AttributeKey<Boolean>("isStrokeDashFactor", Boolean.class, true, false, LABELS);
    /**
     * Are stroke values pixel values or should they be transformed as well.
     */
    public static final AttributeKey<Boolean> IS_STROKE_PIXEL_VALUE = new AttributeKey<Boolean>("isStrokePixelValue", Boolean.class, false, false, LABELS);

    public static enum StrokeType {
        /**
         * If STROKE_TYPE is put to this value, a BasicStroke instance is used for stroking.
         */
        BASIC,
        /**
         * If STROKE_TYPE is put to this value, a DoubleStroke instance is used for stroking.
         */
        DOUBLE
    }
    /**
     * Stroke type. The value of this attribute is either VALUE_STROKE_TYPE_BASIC or
     * VALUE_STROKE_TYPE_DOUBLE. FIXME - Type should be an enumeration.
     */
    public static final AttributeKey<StrokeType> STROKE_TYPE = new AttributeKey<StrokeType>("strokeType", StrokeType.class, StrokeType.BASIC, false, LABELS);

    public static enum StrokePlacement {
        /**
         * If STROKE_PLACEMENT is put to this value, the stroke is centered on the path.
         */
        CENTER,
        /**
         * If STROKE_PLACEMENT is put to this value, the stroke is placed inside of a closed path.
         */
        INSIDE,
        /**
         * If STROKE_PLACEMENT is put to this value, the stroke is placed outside of a closed path.
         */
        OUTSIDE
    }
    /**
     * Stroke placement. The value is either StrokePlacement.CENTER, StrokePlacement.INSIDE or
     * StrokePlacement.OUTSIDE. This only has effect for closed paths. On open paths, the stroke is
     * always centered on the path.
     * <p>
     * The default value is StrokePlacement.CENTER.
     */
    public static final AttributeKey<StrokePlacement> STROKE_PLACEMENT = new AttributeKey<StrokePlacement>("strokePlacement", StrokePlacement.class, StrokePlacement.CENTER, false, LABELS);
    /**
     * The value of this attribute is a String object, which is used to display the text of the
     * figure.
     */
    public static final AttributeKey<String> TEXT = new AttributeKey<String>("text", String.class, null, true, LABELS);
    /**
     * Text color. The value of this attribute is a Color object.
     */
    public static final AttributeKey<Color> TEXT_COLOR = new AttributeKey<Color>("textColor", Color.class, Color.BLACK, false, LABELS);
    /**
     * Text shadow color. The value of this attribute is a Color object.
     */
    public static final AttributeKey<Color> TEXT_SHADOW_COLOR = new AttributeKey<Color>("textShadowColor", Color.class, null, true, LABELS);
    /**
     * Text shadow offset. The value of this attribute is a Dimension2DDouble object.
     */
    public static final AttributeKey<Dimension2DDouble> TEXT_SHADOW_OFFSET = new AttributeKey<Dimension2DDouble>("textShadowOffset", Dimension2DDouble.class, new Dimension2DDouble(1d, 1d), false, LABELS);

    public static enum Alignment {
        /**
         * align on the left or the top
         */
        LEADING,
        /**
         * align on the right or the bottom
         */
        TRAILING,
        /**
         * align in the center
         */
        CENTER,
        /**
         * stretch to fill horizontally, or vertically
         */
        BLOCK,
    }
    /**
     * Text alignment. The value of this attribute is a Alignment enum.
     */
    public static final AttributeKey<Alignment> TEXT_ALIGNMENT = new AttributeKey<Alignment>("textAlignment", Alignment.class, Alignment.LEADING, false, LABELS);
    /**
     * The value of this attribute is a Font object, which is used as a prototype to create the font
     * for the text.
     */
    public static final AttributeKey<Font> FONT_FACE = new AttributeKey<Font>("fontFace", Font.class, new Font("VERDANA", Font.PLAIN, 10), false, LABELS);
    /**
     * The value of this attribute is a double object.
     */
    public static final AttributeKey<Double> FONT_SIZE = new AttributeKey<Double>("fontSize", Double.class, 12d, false, LABELS);
    /**
     * The value of this attribute is a Boolean object.
     */
    public static final AttributeKey<Boolean> FONT_BOLD = new AttributeKey<Boolean>("fontBold", Boolean.class, false, false, LABELS);
    /**
     * The value of this attribute is a Boolean object.
     */
    public static final AttributeKey<Boolean> FONT_ITALIC = new AttributeKey<Boolean>("fontItalic", Boolean.class, false, false, LABELS);
    /**
     * The value of this attribute is a Boolean object.
     */
    public static final AttributeKey<Boolean> FONT_UNDERLINE = new AttributeKey<Boolean>("fontUnderline", Boolean.class, false, false, LABELS);
    /**
     * The value of this attribute is a Liner object.
     */
    public static final AttributeKey<Liner> BEZIER_PATH_LAYOUTER = new AttributeKey<Liner>("bezierPathLayouter", Liner.class, null, true, LABELS);
    public static final AttributeKey<LineDecoration> END_DECORATION = new AttributeKey<LineDecoration>("endDecoration", LineDecoration.class, null, true, LABELS);
    public static final AttributeKey<LineDecoration> START_DECORATION = new AttributeKey<LineDecoration>("startDecoration", LineDecoration.class, null, true, LABELS);
    /**
     * The value of this attribute is a Insets2D.Double object.
     */
    public static final AttributeKey<Insets2D.Double> DECORATOR_INSETS = new AttributeKey<Insets2D.Double>("decoratorInsets", Insets2D.Double.class, new Insets2D.Double(), false, LABELS);
    /**
     * The value of this attribute is a Insets2D.Double object.
     * <p>
     * This attribute can be put on a CompositeFigure, which uses a Layouter to lay out its
     * children.
     * <p>
     * The insets are used to determine the insets between the bounds of the CompositeFigure and its
     * children.
     */
    public static final AttributeKey<Insets2D.Double> LAYOUT_INSETS = new AttributeKey<Insets2D.Double>("borderInsets", Insets2D.Double.class, new Insets2D.Double(), false, LABELS);
    /**
     * The value of this attribute is a Alignment object.
     * <p>
     * This attribute can be put on a CompositeFigure, which uses a Layouter to lay out its
     * children.
     * <p>
     * The insets are used to determine the default alignment of the children of the
     * CompositeFigure.
     */
    public static final AttributeKey<Alignment> COMPOSITE_ALIGNMENT = new AttributeKey<Alignment>("layoutAlignment", Alignment.class, Alignment.BLOCK, false, LABELS);
    /**
     * The value of this attribute is a Alignment object.
     * <p>
     * This attribute can be put on a child of a CompositeFigure, which uses a Layouter to lay out
     * its children.
     * <p>
     * Layouters should use this attribute, to determine the default alignment of the child figures
     * contained in the CompositeFigure which they lay out.
     */
    public static final AttributeKey<Alignment> CHILD_ALIGNMENT = new AttributeKey<Alignment>("layoutAlignment", Alignment.class, null, true, LABELS);
    /**
     * Specifies the transform of a Figure.
     */
    public static final AttributeKey<AffineTransform> TRANSFORM = new AttributeKey<AffineTransform>("transform", AffineTransform.class, null, true, LABELS);

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
     * Specifies the orientation of a Figure.
     */
    public static final AttributeKey<Orientation> ORIENTATION = new AttributeKey<Orientation>("orientation", Orientation.class, Orientation.NORTH, false, LABELS);
    /**
     * A put with all attributes defined by this class.
     */
    public static final Set<AttributeKey<?>> SUPPORTED_ATTRIBUTES;
    public static final Map<String, AttributeKey<?>> SUPPORTED_ATTRIBUTES_MAP;

    static {
        HashSet<AttributeKey<?>> as = new HashSet<>();
        as.addAll(Arrays.asList(new AttributeKey<?>[]{
            FILL_COLOR,
            FILL_UNDER_STROKE,
            STROKE_COLOR,
            STROKE_WIDTH,
            STROKE_INNER_WIDTH_FACTOR,
            STROKE_JOIN,
            STROKE_CAP,
            STROKE_MITER_LIMIT,
            STROKE_DASHES,
            STROKE_DASH_PHASE,
            STROKE_TYPE,
            STROKE_PLACEMENT,
            TEXT,
            TEXT_COLOR,
            TEXT_SHADOW_COLOR,
            TEXT_SHADOW_OFFSET,
            TRANSFORM,
            FONT_FACE,
            FONT_SIZE,
            FONT_BOLD,
            FONT_ITALIC,
            FONT_UNDERLINE,
            BEZIER_PATH_LAYOUTER,
            END_DECORATION,
            START_DECORATION,
            DECORATOR_INSETS,
            ORIENTATION,
            WINDING_RULE}));
        SUPPORTED_ATTRIBUTES = Collections.unmodifiableSet(as);
        HashMap<String, AttributeKey<?>> am = new HashMap<>();
        for (AttributeKey<?> a : as) {
            am.put(a.getKey(), a);
        }
        // XXX Redundant cast needed, becaues Collections.unmodifiableMap loses the <?>
        @SuppressWarnings("cast")
        Map<String, AttributeKey<?>> sam = (Map<String, AttributeKey<?>>) Collections.unmodifiableMap(am);
        SUPPORTED_ATTRIBUTES_MAP = sam;
    }

    /**
     * Computing a global scale factor derived from pixel with or different measures.
     */
    public static double getGlobalValueFactor(Figure f, double factor) {
        if (factor != 1.0 && f.get(IS_STROKE_PIXEL_VALUE)) {
            return factor != 0.0 ? factor : 1.0;
        }
        return 1.0;
    }

    /**
     * Returns a scale factor derived from a Graphics2D context.
     *
     * @param g
     * @return
     */
    public static double getScaleFactorFromGraphics(Graphics2D g) {
        return getScaleFactor(g.getTransform());
    }

    /**
     * Returns a scale factor derived from a AffineTransform.
     *
     * @param transform
     * @return
     */
    public static double getScaleFactor(AffineTransform transform) {
        if (transform == null) {
            return 1.0;
        }
        double scale = transform.getScaleX();
        return scale != 0 ? 1.0 / scale : 1.0;
    }

    /**
     * Convenience method for computing the total stroke width from the STROKE_WIDTH,
     * STROKE_INNER_WIDTH and STROKE_TYPE attributes.
     */
    public static double getStrokeTotalWidth(Figure f, double factor) {
        switch (f.get(STROKE_TYPE)) {
            case BASIC:
            default:
                return f.get(STROKE_WIDTH) / getGlobalValueFactor(f, factor);
            // break; not reached
            case DOUBLE:
                return f.get(STROKE_WIDTH) * (1d + f.get(STROKE_INNER_WIDTH_FACTOR)) / getGlobalValueFactor(f, factor);
            // break; not reached
        }
    }

    /**
     * Convenience method for computing the total stroke miter limit from the STROKE_MITER_LIMIT,
     * and IS_STROKE_MITER_LIMIT factor.
     */
    public static double getStrokeTotalMiterLimit(Figure f, double factor) {
        if (f.get(IS_STROKE_MITER_LIMIT_FACTOR)) {
            return f.get(STROKE_MITER_LIMIT) * f.get(STROKE_WIDTH) * getGlobalValueFactor(f, factor);
        } else {
            return f.get(STROKE_MITER_LIMIT);
        }
    }

    public static Stroke getStroke(Figure f, double factor) {
        double strokeWidth = f.get(STROKE_WIDTH) * getGlobalValueFactor(f, factor);
        float miterLimit = (float) getStrokeTotalMiterLimit(f, factor);
        double dashFactor = f.get(IS_STROKE_DASH_FACTOR) ? strokeWidth : 1d;
        double dashPhase = f.get(STROKE_DASH_PHASE);
        double[] ddashes = f.get(STROKE_DASHES);
        float[] dashes = null;
        boolean isAllZeroes = true;
        if (ddashes != null) {
            dashes = new float[ddashes.length];
            double dashSize = 0f;
            for (int i = 0; i < dashes.length; i++) {
                dashes[i] = Math.max(0f, (float) (ddashes[i] * dashFactor));
                dashSize += dashes[i];
                if (isAllZeroes && dashes[i] != 0) {
                    isAllZeroes = false;
                }
            }
            if (dashes.length % 2 == 1) {
                dashSize *= 2;
            }
            if (dashPhase < 0) {
                dashPhase = dashSize + dashPhase % dashSize;
            }
        }
        if (isAllZeroes) {
            // don't draw dashes, if all values are 0.
            dashes = null;
        }
        switch (f.get(STROKE_TYPE)) {
            case BASIC:
            default:
                return new BasicStroke((float) strokeWidth,
                        f.get(STROKE_CAP),
                        f.get(STROKE_JOIN),
                        Math.max(1, miterLimit),
                        dashes, Math.max(0, (float) (dashPhase * dashFactor)));
            //not reached
            case DOUBLE:
                return new DoubleStroke(
                        (float) (f.get(STROKE_INNER_WIDTH_FACTOR) * strokeWidth),
                        (float) strokeWidth,
                        f.get(STROKE_CAP),
                        f.get(STROKE_JOIN),
                        Math.max(1, miterLimit),
                        dashes, Math.max(0, (float) (dashPhase * dashFactor)));
            //not reached
        }
    }

    /**
     * Returns a stroke which is useful for hit-testing. The stroke reflects the stroke width, but
     * not the stroke dashes attribute.
     *
     * @param f
     * @return A stroke suited for creating a shape for hit testing.
     */
    public static Stroke getHitStroke(Figure f, double factor) {
        double strokeWidth = Math.max(1, f.get(STROKE_WIDTH) * getGlobalValueFactor(f, factor));
        float miterLimit = (float) getStrokeTotalMiterLimit(f, factor);
        double dashFactor = f.get(IS_STROKE_DASH_FACTOR) ? strokeWidth : 1d;
        switch (f.get(STROKE_TYPE)) {
            case BASIC:
            default:
                return new BasicStroke((float) strokeWidth,
                        f.get(STROKE_CAP),
                        f.get(STROKE_JOIN),
                        miterLimit,
                        null, Math.max(0, (float) (f.get(STROKE_DASH_PHASE) * dashFactor)));
            //not reached
            case DOUBLE:
                return new DoubleStroke(
                        (float) (f.get(STROKE_INNER_WIDTH_FACTOR) * strokeWidth),
                        (float) strokeWidth,
                        f.get(STROKE_CAP),
                        f.get(STROKE_JOIN),
                        miterLimit,
                        null, Math.max(0, (float) (f.get(STROKE_DASH_PHASE).floatValue() * dashFactor)));
            //not reached
        }
    }

    public static Font getFont(Figure f) {
        Font prototype = f.get(FONT_FACE);
        if (prototype == null) {
            return null;
        }
        if (getFontStyle(f) != Font.PLAIN) {
            return prototype.deriveFont(getFontStyle(f), f.get(FONT_SIZE).floatValue());
        } else {
            return prototype.deriveFont(f.get(FONT_SIZE).floatValue());
        }
    }

    public static int getFontStyle(Figure f) {
        int style = Font.PLAIN;
        if (f.get(FONT_BOLD)) {
            style |= Font.BOLD;
        }
        if (f.get(FONT_ITALIC)) {
            style |= Font.ITALIC;
        }
        return style;
    }

    /**
     * Returns the distance, that a Rectangle needs to grow (or shrink) to fill its shape as
     * specified by the FILL_UNDER_STROKE and STROKE_POSITION attributes of a figure. The value
     * returned is the number of units that need to be grown (or shrunk) perpendicular to a stroke
     * on an outline of the shape.
     */
    public static double getPerpendicularFillGrowth(Figure f, double factor) {
        double grow;
        double strokeWidth = AttributeKeys.getStrokeTotalWidth(f, factor);
        StrokePlacement placement = f.get(STROKE_PLACEMENT);
        switch (f.get(FILL_UNDER_STROKE)) {
            case FULL:
                switch (placement) {
                    case INSIDE:
                        grow = 0f;
                        break;
                    case OUTSIDE:
                        grow = strokeWidth;
                        break;
                    case CENTER:
                    default:
                        grow = strokeWidth / 2d;
                        break;
                }
                break;
            case NONE:
                switch (placement) {
                    case INSIDE:
                        grow = -strokeWidth;
                        break;
                    case OUTSIDE:
                        grow = 0f;
                        break;
                    case CENTER:
                    default:
                        grow = strokeWidth / -2d;
                        break;
                }
                break;
            case CENTER:
            default:
                switch (placement) {
                    case INSIDE:
                        grow = strokeWidth / -2d;
                        break;
                    case OUTSIDE:
                        grow = strokeWidth / 2d;
                        break;
                    case CENTER:
                    default:
                        grow = 0d;
                        break;
                }
                break;
        }
        return grow;
    }

    /**
     * Returns the distance, that a Rectangle needs to grow (or shrink) to draw (aka stroke) its
     * shape as specified by the FILL_UNDER_STROKE and STROKE_POSITION attributes of a figure. The
     * value returned is the number of units that need to be grown (or shrunk) perpendicular to a
     * stroke on an outline of the shape.
     */
    public static double getPerpendicularDrawGrowth(Figure f, double factor) {
        double grow;
        double strokeWidth = AttributeKeys.getStrokeTotalWidth(f, factor);
        switch (f.get(STROKE_PLACEMENT)) {
            case INSIDE:
                grow = strokeWidth / -2d;
                break;
            case OUTSIDE:
                grow = strokeWidth / 2d;
                break;
            case CENTER:
            default:
                grow = 0f;
                break;
        }
        return grow;
    }

    /**
     * Returns the distance, that a Rectangle needs to grow (or shrink) to make hit detections on a
     * shape as specified by the FILL_UNDER_STROKE and STROKE_POSITION attributes of a figure. The
     * value returned is the number of units that need to be grown (or shrunk) perpendicular to a
     * stroke on an outline of the shape.
     */
    public static double getPerpendicularHitGrowth(Figure f, double factor) {
        double grow;
        if (f.get(STROKE_COLOR) == null) {
            grow = getPerpendicularFillGrowth(f, factor);
        } else {
            double strokeWidth = AttributeKeys.getStrokeTotalWidth(f, factor);
            grow = getPerpendicularDrawGrowth(f, factor) + strokeWidth / 2d;
        }
        return grow;
    }
}
