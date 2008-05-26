/**
 * @(#)HandleAttributeKeys.java  2.0  2008-05-22
 *
 * Copyright (c) 2008 by the original authors of JHotDraw
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

import java.awt.*;

/**
 * HandleAttributeKeys defines attribute keys for {@link Handle}'s.
 * <p>
 * If you want different attribute values for your own editor, set
 * the desired values using {@link DrawingEditor#setHandleAttribute(org.jhotdraw.draw.AttributeKey, java.lang.Object)}.
 *
 * @author Werner Randelshofer
 * @version 2.0 2008-05-22 Added support for hover handles. Assigned better names
 * to attribute keys. Changed default
 * values, to better much the look of well known drawing software, such as
 * Adobe Fireworks and Adobe Illustrator. 
 * <br>1.0 11.05.2008 Created.
 */
public class HandleAttributeKeys {

    /**
     * General handle size.
     */
    public final static AttributeKey<Integer> HANDLE_SIZE = new AttributeKey<Integer>("handleSize", 7);
    /**
     * General handle stroke color.
     */
    public final static AttributeKey<Color> HANDLE_STROKE_COLOR = new AttributeKey<Color>("handleStrokeColor", Color.WHITE);
    /**
     * General handle fill color.
     */
    public final static AttributeKey<Color> HANDLE_FILL_COLOR = new AttributeKey<Color>("handleFillColor", Color.BLACK);
    /**
     * General handle stroke.
     */
    public final static AttributeKey<Stroke> HANDLE_STROKE = new AttributeKey<Stroke>("handleStroke", new BasicStroke(1f));
    /**
     * General handle stroke color.
     */
    public final static AttributeKey<Color> HANDLE_STROKE_COLOR_DISABLED = new AttributeKey<Color>("handleStrokeColor", Color.WHITE);
    /**
     * General handle fill color.
     */
    public final static AttributeKey<Color> HANDLE_FILL_COLOR_DISABLED = new AttributeKey<Color>("handleFillColor", new Color(0x0));
    //--
    /**
     * Rotate handle stroke color.
     */
    public final static AttributeKey<Color> ROTATE_HANDLE_STROKE_COLOR = new AttributeKey<Color>("rotateHandleStrokeColor", Color.WHITE);
    /**
     * Rotate handle fill color.
     */
    public final static AttributeKey<Color> ROTATE_HANDLE_FILL_COLOR = new AttributeKey<Color>("rotateHandleFillColor", Color.MAGENTA);
    //--
    /**
     * Rotate handle stroke color.
     */
    public final static AttributeKey<Color> ROTATE_HANDLE_STROKE_COLOR_DISABLED = new AttributeKey<Color>("rotateHandleStrokeColorDisabled", Color.WHITE);
    /**
     * Rotate handle fill color.
     */
    public final static AttributeKey<Color> ROTATE_HANDLE_FILL_COLOR_DISABLED = new AttributeKey<Color>("rotateHandleFillColorDisabled", new Color(0x0));
    //--
    /**
     * Bezier control point handle stroke color.
     */
    public final static AttributeKey<Color> BEZIER_CONTROL_POINT_HANDLE_STROKE_COLOR = new AttributeKey<Color>("bezierControlPointHandleStrokeColor", Color.WHITE);
    /**
     * Bezier control point handle fill color.
     */
    public final static AttributeKey<Color> BEZIER_CONTROL_POINT_HANDLE_FILL_COLOR = new AttributeKey<Color>("bezierControlPointHandleFillColor", Color.BLUE);
    /**
     * Bezier tangent line stroke color 1.
     */
    public final static AttributeKey<Color> BEZIER_TANGENT_COLOR_1 = new AttributeKey<Color>("bezierTangentColor1", Color.WHITE);
    /**
     * Bezier tangent line stroke color 2.
     */
    public final static AttributeKey<Color> BEZIER_TANGENT_COLOR_2 = new AttributeKey<Color>("bezierTangentColor1", Color.BLUE);
    /**
     * Bezier tangent line stroke 1.
     */
    public final static AttributeKey<Stroke> BEZIER_TANGENT_STROKE_1 = new AttributeKey<Stroke>("bezierTangentStroke1",
            new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0f, new float[]{5f, 5f}, 0f));
    /**
     * Bezier tangent line stroke 2.
     */
    public final static AttributeKey<Stroke> BEZIER_TANGENT_STROKE_2 = new AttributeKey<Stroke>("bezierTangentStroke2",
            new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0f, new float[]{5f, 5f}, 5f));
    /**
     * Bezier node handle stroke color.
     */
    public final static AttributeKey<Color> BEZIER_NODE_HANDLE_STROKE_COLOR = new AttributeKey<Color>("bezierControlPointStrokeColor", Color.WHITE);
    /**
     * Bezier node handle fill color.
     */
    public final static AttributeKey<Color> BEZIER_NODE_HANDLE_FILL_COLOR = new AttributeKey<Color>("bezierControlPointFillColor", new Color(0x00a8ff));
    /**
     * Bezier path stroke color 1.
     */
    public final static AttributeKey<Color> BEZIER_PATH_COLOR_1 = new AttributeKey<Color>("bezierPathColor1", Color.WHITE);
    /**
     * Bezier path stroke color 2.
     */
    public final static AttributeKey<Color> BEZIER_PATH_COLOR_2 = new AttributeKey<Color>("bezierPathColor2", new Color(0x00a8ff));
    /**
     * Bezier path stroke 1.
     */
    public final static AttributeKey<Stroke> BEZIER_PATH_STROKE_1 = new AttributeKey<Stroke>("bezierPathStroke1",
            new BasicStroke(3f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
    /**
     * Bezier path stroke 2.
     */
    public final static AttributeKey<Stroke> BEZIER_PATH_STROKE_2 = new AttributeKey<Stroke>("bezierPathStroke2",
            new BasicStroke(1f));
    //---
    /**
     * Bezier path hover stroke color 1.
     */
    public final static AttributeKey<Color> BEZIER_PATH_COLOR_1_HOVER = new AttributeKey<Color>("bezierPathColor1Hover", null);
    /**
     * Bezier path hover stroke color 2.
     */
    public final static AttributeKey<Color> BEZIER_PATH_COLOR_2_HOVER = new AttributeKey<Color>("bezierPathColor2Hover", new Color(0x00a8ff));
    /**
     * Bezier path hover stroke 1.
     */
    public final static AttributeKey<Stroke> BEZIER_PATH_STROKE_1_HOVER = new AttributeKey<Stroke>("bezierPathStroke1Hover",
            null);
    /**
     * Bezier path hover stroke 2.
     */
    public final static AttributeKey<Stroke> BEZIER_PATH_STROKE_2_HOVER = new AttributeKey<Stroke>("bezierPathStroke2Hover",
            new BasicStroke(1f));
    //---
    /**
     * Bezier control point handle stroke color.
     */
    public final static AttributeKey<Color> BEZIER_CONTROL_POINT_HANDLE_STROKE_COLOR_DISABLED = new AttributeKey<Color>("bezierControlPointHandleStrokeColorDisabled", Color.WHITE);
    /**
     * Bezier control point handle fill color.
     */
    public final static AttributeKey<Color> BEZIER_CONTROL_POINT_HANDLE_FILL_COLOR_DISABLED = new AttributeKey<Color>("bezierControlPointHandleFillColorDisabled", new Color(0x0));
    /**
     * Bezier tangent line stroke color 1.
     */
    public final static AttributeKey<Color> BEZIER_TANGENT_COLOR_1_DISABLED = new AttributeKey<Color>("bezierTangentColor1Disabled", Color.WHITE);
    /**
     * Bezier tangent line stroke color 2.
     */
    public final static AttributeKey<Color> BEZIER_TANGENT_COLOR_2_DISABLED = new AttributeKey<Color>("bezierTangentColor1Disabled", new Color(0x0));
    /**
     * Bezier tangent line stroke 1.
     */
    public final static AttributeKey<Stroke> BEZIER_TANGENT_STROKE_1_DISABLED = new AttributeKey<Stroke>("bezierTangentStroke1Disabled",
            new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0f, new float[]{5f, 5f}, 0f));
    /**
     * Bezier tangent line stroke 2.
     */
    public final static AttributeKey<Stroke> BEZIER_TANGENT_STROKE_2_DISABLED = new AttributeKey<Stroke>("bezierTangentStroke2Disabled",
            new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0f, new float[]{5f, 5f}, 5f));
    /**
     * Bezier node handle stroke color.
     */
    public final static AttributeKey<Color> BEZIER_NODE_HANDLE_STROKE_COLOR_DISABLED = new AttributeKey<Color>("bezierControlPointStrokeColorDisabled", Color.WHITE);
    /**
     * Bezier node handle fill color.
     */
    public final static AttributeKey<Color> BEZIER_NODE_HANDLE_FILL_COLOR_DISABLED = new AttributeKey<Color>("bezierControlPointFillColorDisabled", new Color(0x0));
    /**
     * Bezier path stroke color 1.
     */
    public final static AttributeKey<Color> BEZIER_PATH_COLOR_1_DISABLED = new AttributeKey<Color>("bezierPathColor1Disabled", Color.WHITE);
    /**
     * Bezier path stroke color 2.
     */
    public final static AttributeKey<Color> BEZIER_PATH_COLOR_2_DISABLED = new AttributeKey<Color>("bezierPathColor2Disabled", new Color(0x0));
    /**
     * Bezier path stroke 1.
     */
    public final static AttributeKey<Stroke> BEZIER_PATH_STROKE_1_DISABLED = new AttributeKey<Stroke>("bezierPathStroke1Disabled",
            new BasicStroke(3f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
    /**
     * Bezier path stroke 2.
     */
    public final static AttributeKey<Stroke> BEZIER_PATH_STROKE_2_DISABLED = new AttributeKey<Stroke>("bezierPathStroke2Disabled",
            new BasicStroke(1f));
    //---
    /**
     * Scale handle stroke color.
     */
    public final static AttributeKey<Color> SCALE_HANDLE_STROKE_COLOR = new AttributeKey<Color>("scaleHandleStrokeColor", Color.WHITE);
    /**
     * Scale handle fill color.
     */
    public final static AttributeKey<Color> SCALE_HANDLE_FILL_COLOR = new AttributeKey<Color>("scaleHandleFillColor", Color.ORANGE.darker());
    /**
     * Resize handle stroke color.
     */
    public final static AttributeKey<Color> RESIZE_HANDLE_STROKE_COLOR = new AttributeKey<Color>("resizeHandleStrokeColor", Color.WHITE);
    /**
     * Resize handle fill color.
     */
    public final static AttributeKey<Color> RESIZE_HANDLE_FILL_COLOR = new AttributeKey<Color>("resizeHandleFillColor", Color.BLUE);
    /**
     * Resize bounds stroke color 1.
     */
    public final static AttributeKey<Color> RESIZE_BOUNDS_COLOR_1 = new AttributeKey<Color>("resizeBoundsColor1", Color.WHITE);
    /**
     * Resize bounds stroke color 2.
     */
    public final static AttributeKey<Color> RESIZE_BOUNDS_COLOR_2 = new AttributeKey<Color>("resizeBoundsColor2", Color.BLUE);
    /**
     * Resize bounds stroke 1.
     */
    public final static AttributeKey<Stroke> RESIZE_BOUNDS_STROKE_1 = new AttributeKey<Stroke>("resizeBoundsStroke1",
            new BasicStroke(3f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
    /**
     * Resize bounds stroke 2.
     */
    public final static AttributeKey<Stroke> RESIZE_BOUNDS_STROKE_2 = new AttributeKey<Stroke>("resizeBoundsStroke2",
            new BasicStroke(1f));
    // 
    /**
     * Disabled resize bounds stroke color 1.
     */
    public final static AttributeKey<Color> RESIZE_BOUNDS_COLOR_1_DISABLED = new AttributeKey<Color>("resizeBoundsColor1Hover", Color.WHITE);
    /**
     * Disabled resize bounds hover color 2.
     */
    public final static AttributeKey<Color> RESIZE_BOUNDS_COLOR_2_DISABLED = new AttributeKey<Color>("resizeBoundsColor2Hover", new Color(0x0));
    /**
     * Disabled resize bounds stroke 1.
     */
    public final static AttributeKey<Stroke> RESIZE_BOUNDS_STROKE_1_DISABLED = new AttributeKey<Stroke>("resizeBoundsStroke1Hover",
            new BasicStroke(3f));
    /**
     * Disabled bounds stroke 2.
     */
    public final static AttributeKey<Stroke> RESIZE_BOUNDS_STROKE_2_DISABLED = new AttributeKey<Stroke>("resizeBoundsStroke2Hover",
            new BasicStroke(1f));
    //---
    // 
    /**
     * Handle bounds hover stroke color 1.
     */
    public final static AttributeKey<Color> RESIZE_BOUNDS_COLOR_1_HOVER = new AttributeKey<Color>("resizeBoundsColor1Hover", null);
    /**
     * Resize bounds hover stroke color 2.
     */
    public final static AttributeKey<Color> RESIZE_BOUNDS_COLOR_2_HOVER = new AttributeKey<Color>("resizeBoundsColor2Hover", Color.BLUE);
    /**
     * Resize bounds hover stroke 1.
     */
    public final static AttributeKey<Stroke> RESIZE_BOUNDS_STROKE_1_HOVER = new AttributeKey<Stroke>("resizeBoundsStroke1Hover",
            null);
    /**
     * Resize bounds hover stroke 2.
     */
    public final static AttributeKey<Stroke> RESIZE_BOUNDS_STROKE_2_HOVER = new AttributeKey<Stroke>("resizeBoundsStroke2Hover",
            new BasicStroke(1f));
    //---
    /**
     * Transform handle stroke color.
     */
    public final static AttributeKey<Color> TRANSFORM_HANDLE_STROKE_COLOR = new AttributeKey<Color>("transformHandleStrokeColor", Color.WHITE);
    /**
     * Transform handle fill color.
     */
    public final static AttributeKey<Color> TRANSFORM_HANDLE_FILL_COLOR = new AttributeKey<Color>("transformHandleFillColor", Color.MAGENTA);
    //---
    /**
     * Transform handle stroke color.
     */
    public final static AttributeKey<Color> TRANSFORM_HANDLE_STROKE_COLOR_DISABLED = new AttributeKey<Color>("transformHandleStrokeColorDisabled", Color.WHITE);
    /**
     * Transform handle fill color.
     */
    public final static AttributeKey<Color> TRANSFORM_HANDLE_FILL_COLOR_DISABLED = new AttributeKey<Color>("transformHandleFillColorDisabled", new Color(0x0));
    /**
     * Transform bounds color 1.
     */
    public final static AttributeKey<Color> TRANSFORM_BOUNDS_COLOR_1 = new AttributeKey<Color>("transformBoundsColor1", Color.WHITE);
    /**
     * Transform bounds color 2.
     */
    public final static AttributeKey<Color> TRANSFORM_BOUNDS_COLOR_2 = new AttributeKey<Color>("transformBoundsColor2", Color.MAGENTA);
    /**
     * Transform bounds stroke 1.
     */
    public final static AttributeKey<Stroke> TRANSFORM_BOUNDS_STROKE_1 = new AttributeKey<Stroke>("transformBoundsStroke1",
            new BasicStroke(3f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
    /**
     * Transform bounds stroke 2.
     */
    public final static AttributeKey<Stroke> TRANSFORM_BOUNDS_STROKE_2 = new AttributeKey<Stroke>("transformBoundsStroke2",
            new BasicStroke(1f));
    //---
    /**
     * Transform bounds hover color 1.
     */
    public final static AttributeKey<Color> TRANSFORM_BOUNDS_COLOR_1_HOVER = new AttributeKey<Color>("transformBoundsColor1Hover", null);
    /**
     * Transform bounds hover color 2.
     */
    public final static AttributeKey<Color> TRANSFORM_BOUNDS_COLOR_2_HOVER = new AttributeKey<Color>("transformBoundsColor2Hover", Color.MAGENTA);
    /**
     * Transform bounds hover stroke 1.
     */
    public final static AttributeKey<Stroke> TRANSFORM_BOUNDS_STROKE_1_HOVER = new AttributeKey<Stroke>("transformBoundsStroke1Hover",
            null);
    /**
     * Transform bounds hover stroke 2.
     */
    public final static AttributeKey<Stroke> TRANSFORM_BOUNDS_STROKE_2_HOVER = new AttributeKey<Stroke>("transformBoundsStroke2Hover",
            new BasicStroke(1f));
    //---
    //---
    /**
     * Transform bounds disabled color 1.
     */
    public final static AttributeKey<Color> TRANSFORM_BOUNDS_COLOR_1_DISABLED = new AttributeKey<Color>("transformBoundsColor1Disabled", Color.WHITE);
    /**
     * Transform bounds disabled color 2.
     */
    public final static AttributeKey<Color> TRANSFORM_BOUNDS_COLOR_2_DISABLED = new AttributeKey<Color>("transformBoundsColor2Disabled", new Color(0x0));
    /**
     * Transform bounds disabled stroke 1.
     */
    public final static AttributeKey<Stroke> TRANSFORM_BOUNDS_STROKE_1_DISABLED = new AttributeKey<Stroke>("transformBoundsStroke1Disabled",
            new BasicStroke(3f));
    /**
     * Transform bounds disabled stroke 2.
     */
    public final static AttributeKey<Stroke> TRANSFORM_BOUNDS_STROKE_2_DISABLED = new AttributeKey<Stroke>("transformBoundsStroke2Disabled",
            new BasicStroke(1f));
    //---
    /**
     * Handle stroke color.
     */
    public final static AttributeKey<Color> CONNECTED_CONNECTION_HANDLE_STROKE_COLOR = new AttributeKey<Color>("connectedConnectionHandleStrokeColor", Color.BLACK);
    /**
     * Handle fill color.
     */
    public final static AttributeKey<Color> CONNECTED_CONNECTION_HANDLE_FILL_COLOR = new AttributeKey<Color>("connectedConnectionHandleFillColor", Color.GREEN);
    /**
     * Handle stroke color.
     */
    public final static AttributeKey<Color> DISCONNECTED_CONNECTION_HANDLE_STROKE_COLOR = new AttributeKey<Color>("disconnectedConnectionHandleStrokeColor", Color.BLACK);
    /**
     * Handle fill color.
     */
    public final static AttributeKey<Color> DISCONNECTED_CONNECTION_HANDLE_FILL_COLOR = new AttributeKey<Color>("disconnectedConnectionHandleFillColor", Color.RED);
    /**
     * Handle stroke color.
     */
    public final static AttributeKey<Color> CONNECTED_CONNECTOR_HANDLE_STROKE_COLOR = new AttributeKey<Color>("connectedConnectorHandleStrokeColor", Color.BLACK);
    /**
     * Handle fill color.
     */
    public final static AttributeKey<Color> CONNECTED_CONNECTOR_HANDLE_FILL_COLOR = new AttributeKey<Color>("connectedConnectorHandleFillColor", Color.GREEN);
    /**
     * Handle stroke color.
     */
    public final static AttributeKey<Color> DISCONNECTED_CONNECTOR_HANDLE_STROKE_COLOR = new AttributeKey<Color>("disconnectedConnectorHandleStrokeColor", Color.BLACK);
    /**
     * Handle fill color.
     */
    public final static AttributeKey<Color> DISCONNECTED_CONNECTOR_HANDLE_FILL_COLOR = new AttributeKey<Color>("disconnectedConnectorHandleFillColor", Color.RED);
    /**
     * Handle stroke color.
     */
    public final static AttributeKey<Color> MOVE_HANDLE_STROKE_COLOR = new AttributeKey<Color>("moveHandleStrokeColor", Color.BLACK);
    /**
     * Handle fill color.
     */
    public final static AttributeKey<Color> MOVE_HANDLE_FILL_COLOR = new AttributeKey<Color>("moveHandleFillColor", Color.WHITE);
    /**
     * Handle stroke color.
     */
    public final static AttributeKey<Color> NULL_HANDLE_STROKE_COLOR = new AttributeKey<Color>("nullHandleStrokeColor", Color.DARK_GRAY);
    /**
     * Handle fill color.
     */
    public final static AttributeKey<Color> NULL_HANDLE_FILL_COLOR = new AttributeKey<Color>("nullHandleFillColor", null);
    /**
     * Handle stroke color.
     */
    public final static AttributeKey<Color> OVERFLOW_HANDLE_STROKE_COLOR = new AttributeKey<Color>("overlfowHandleStrokeColor", Color.RED);
    /**
     * Handle fill color.
     */
    public final static AttributeKey<Color> OVERFLOW_HANDLE_FILL_COLOR = new AttributeKey<Color>("overflowHandleFillColor", null);
    //--
    /**
     * Attribute handle stroke color.
     */
    public final static AttributeKey<Color> ATTRIBUTE_HANDLE_STROKE_COLOR = new AttributeKey<Color>("attributeHandleStrokeColor", Color.BLACK);
    /**
     * Attribute handle fill color.
     */
    public final static AttributeKey<Color> ATTRIBUTE_HANDLE_FILL_COLOR = new AttributeKey<Color>("attributeSizeHandleFillColor", Color.YELLOW);
    //--
    /**
     * Attribute handle stroke color.
     */
    public final static AttributeKey<Color> ATTRIBUTE_HANDLE_STROKE_COLOR_DISABLED = new AttributeKey<Color>("attributeHandleStrokeColorDisabled", new Color(0x0));
    /**
     * Attribute handle fill color.
     */
    public final static AttributeKey<Color> ATTRIBUTE_HANDLE_FILL_COLOR_DISABLED = new AttributeKey<Color>("attributeSizeHandleFillColorDisabled", Color.WHITE);
}
