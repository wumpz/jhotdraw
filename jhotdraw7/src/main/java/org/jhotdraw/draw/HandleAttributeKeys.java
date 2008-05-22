/**
 * @(#)HandleAttributeKeys.java  2.0  2008-05-22
 *
 * Copyright (c) 2008 Werner Randelshofer
 * Staldenmattweg 2, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
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
     * Rotate handle stroke color.
     */
    public final static AttributeKey<Color> ROTATE_HANDLE_STROKE_COLOR = new AttributeKey<Color>("rotateHandleStrokeColor", Color.WHITE);
    /**
     * Rotate handle fill color.
     */
    public final static AttributeKey<Color> ROTATE_HANDLE_FILL_COLOR = new AttributeKey<Color>("rotateHandleFillColor", Color.MAGENTA);
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
            new BasicStroke(
            1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0f, new float[]{5f, 5f}, 0f));
    /**
     * Bezier tangent line stroke 2.
     */
    public final static AttributeKey<Stroke> BEZIER_TANGENT_STROKE_2 = new AttributeKey<Stroke>("bezierTangentStroke2",
            new BasicStroke(
            1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0f, new float[]{5f, 5f}, 5f));
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
            new BasicStroke(
            3f));
    /**
     * Bezier path stroke 2.
     */
    public final static AttributeKey<Stroke> BEZIER_PATH_STROKE_2 = new AttributeKey<Stroke>("bezierPathStroke2",
            new BasicStroke(
            1f));
    //---
    /**
     * Bezier path hover stroke color 1.
     */
    public final static AttributeKey<Color> BEZIER_PATH_HOVER_COLOR_1 = new AttributeKey<Color>("bezierPathHoverColor1", null);
    /**
     * Bezier path hover stroke color 2.
     */
    public final static AttributeKey<Color> BEZIER_PATH_HOVER_COLOR_2 = new AttributeKey<Color>("bezierPathHoverColor2", new Color(0x00a8ff));
    /**
     * Bezier path hover stroke 1.
     */
    public final static AttributeKey<Stroke> BEZIER_PATH_HOVER_STROKE_1 = new AttributeKey<Stroke>("bezierPathHoverStroke1",
            null);
    /**
     * Bezier path hover stroke 2.
     */
    public final static AttributeKey<Stroke> BEZIER_PATH_HOVER_STROKE_2 = new AttributeKey<Stroke>("bezierPathHoverStroke2",
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
     * Handle bounds stroke color 1.
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
            new BasicStroke(
            3f));
    /**
     * Resize bounds stroke 2.
     */
    public final static AttributeKey<Stroke> RESIZE_BOUNDS_STROKE_2 = new AttributeKey<Stroke>("resizeBoundsStroke2",
            new BasicStroke(
            1f));
    //---
    /**
     * Handle bounds hover stroke color 1.
     */
    public final static AttributeKey<Color> RESIZE_BOUNDS_HOVER_COLOR_1 = new AttributeKey<Color>("resizeBoundsHoverColor1", null);
    /**
     * Resize bounds hover stroke color 2.
     */
    public final static AttributeKey<Color> RESIZE_BOUNDS_HOVER_COLOR_2 = new AttributeKey<Color>("resizeBoundsHoverColor2", Color.BLUE);
    /**
     * Resize bounds hover stroke 1.
     */
    public final static AttributeKey<Stroke> RESIZE_BOUNDS_HOVER_STROKE_1 = new AttributeKey<Stroke>("resizeBoundsHoverStroke1",
            null);
    /**
     * Resize bounds hover stroke 2.
     */
    public final static AttributeKey<Stroke> RESIZE_BOUNDS_HOVER_STROKE_2 = new AttributeKey<Stroke>("resizeBoundsHoverStroke2",
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
            new BasicStroke(3f));
    /**
     * Transform bounds stroke 2.
     */
    public final static AttributeKey<Stroke> TRANSFORM_BOUNDS_STROKE_2 = new AttributeKey<Stroke>("transformBoundsStroke2",
            new BasicStroke(1f));
    //---
    /**
     * Transform bounds hover color 1.
     */
    public final static AttributeKey<Color> TRANSFORM_BOUNDS_HOVER_COLOR_1 = new AttributeKey<Color>("transformBoundsHoverColor1", null);
    /**
     * Transform bounds hover color 2.
     */
    public final static AttributeKey<Color> TRANSFORM_BOUNDS_HOVER_COLOR_2 = new AttributeKey<Color>("transformBoundsHoverColor2", Color.MAGENTA);
    /**
     * Transform bounds hover stroke 1.
     */
    public final static AttributeKey<Stroke> TRANSFORM_BOUNDS_HOVER_STROKE_1 = new AttributeKey<Stroke>("transformBoundsStroke1",
            null);
    /**
     * Transform bounds hover stroke 2.
     */
    public final static AttributeKey<Stroke> TRANSFORM_BOUNDS_HOVER_STROKE_2 = new AttributeKey<Stroke>("transformBoundsStroke2",
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
    /**
     * Handle stroke color.
     */
    public final static AttributeKey<Color> ATTRIBUTE_HANDLE_STROKE_COLOR = new AttributeKey<Color>("attributeHandleStrokeColor", Color.BLACK);
    /**
     * Handle fill color.
     */
    public final static AttributeKey<Color> ATTRIBUTE_HANDLE_FILL_COLOR = new AttributeKey<Color>("attributeSizeHandleFillColor", Color.YELLOW);
}
