/**
 * @(#)HandleAttributeKeys.java  1.0  11.05.2008
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
 * @version 1.0 11.05.2008 Created.
 */
public class HandleAttributeKeys {
    /**
     * Handle size.
     */
    public final static AttributeKey<Integer> HANDLE_SIZE = new AttributeKey<Integer>("handleSize", 7);
    /**
     * Handle stroke color.
     */
    public final static AttributeKey<Color> HANDLE_STROKE_COLOR = new AttributeKey<Color>("handleStrokeColor", Color.WHITE);
    /**
     * Handle fill color.
     */
    public final static AttributeKey<Color> HANDLE_FILL_COLOR = new AttributeKey<Color>("handleFillColor", Color.BLACK);
    /**
     * Handle stroke.
     */
    public final static AttributeKey<Stroke> HANDLE_STROKE = new AttributeKey<Stroke>("handleStroke", new BasicStroke(1f));

    /**
     * Handle stroke color.
     */
    public final static AttributeKey<Color> ROTATE_HANDLE_STROKE_COLOR = new AttributeKey<Color>("rotateHandleStrokeColor", Color.WHITE);
    /**
     * Handle fill color.
     */
    public final static AttributeKey<Color> ROTATE_HANDLE_FILL_COLOR = new AttributeKey<Color>("rotateHandleFillColor", Color.GREEN.darker());

    /**
     * Handle stroke color.
     */
    public final static AttributeKey<Color> BEZIER_CONTROL_POINT_HANDLE_STROKE_COLOR = new AttributeKey<Color>("bezierControlPointStrokeColor", Color.WHITE);
    /**
     * Handle fill color.
     */
    public final static AttributeKey<Color> BEZIER_CONTROL_POINT_HANDLE_FILL_COLOR = new AttributeKey<Color>("bezierControlPointFillColor", Color.BLUE);
    /**
     * Handle stroke color.
     */
    public final static AttributeKey<Color> BEZIER_CONTROL_POINT_HANDLE_STROKE_COLOR_1 = new AttributeKey<Color>("bezierControlPointStrokeColor1", Color.BLUE);
    /**
     * Handle stroke color.
     */
    public final static AttributeKey<Color> BEZIER_CONTROL_POINT_HANDLE_STROKE_COLOR_2 = new AttributeKey<Color>("bezierControlPointStrokeColor2", Color.WHITE);
    /**
     * Handle stroke.
     */
    public final static AttributeKey<Stroke> BEZIER_CONTROL_POINT_HANDLE_STROKE_1 = new AttributeKey<Stroke>("bezierControlPointHandleStroke1", new BasicStroke(
            1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0f, new float[] { 5f, 5f }, 0f
            ));
    /**
     * Handle stroke.
     */
    public final static AttributeKey<Stroke> BEZIER_CONTROL_POINT_HANDLE_STROKE_2 = new AttributeKey<Stroke>("bezierControlPointHandleStroke2", new BasicStroke(
            1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0f, new float[] { 5f, 5f }, 5f
            ));
    /**
     * Handle stroke color.
     */
    public final static AttributeKey<Color> BEZIER_NODE_HANDLE_STROKE_COLOR = new AttributeKey<Color>("bezierControlPointStrokeColor", Color.WHITE);
    /**
     * Handle fill color.
     */
    public final static AttributeKey<Color> BEZIER_NODE_HANDLE_FILL_COLOR = new AttributeKey<Color>("bezierControlPointFillColor", Color.MAGENTA);
    /**
     * Handle stroke color.
     */
    public final static AttributeKey<Color> BEZIER_OUTLINE_HANDLE_STROKE_COLOR_1 = new AttributeKey<Color>("bezierControlPointStrokeColor1", Color.MAGENTA);
    /**
     * Handle stroke color.
     */
    public final static AttributeKey<Color> BEZIER_OUTLINE_HANDLE_STROKE_COLOR_2 = new AttributeKey<Color>("bezierControlPointStrokeColor2", Color.WHITE);
    /**
     * Handle stroke.
     */
    public final static AttributeKey<Stroke> BEZIER_OUTLINE_HANDLE_STROKE_1 = new AttributeKey<Stroke>("bezierControlPointHandleStroke1", new BasicStroke(
            2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0f, new float[] { 3f, 7f }, 0f
            ));
    /**
     * Handle stroke.
     */
    public final static AttributeKey<Stroke> BEZIER_OUTLINE_HANDLE_STROKE_2 = new AttributeKey<Stroke>("bezierControlPointHandleStroke2", new BasicStroke(
            2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0f, new float[] { 3f, 7f }, 5f
            ));
    /**
     * Handle stroke color.
     */
    public final static AttributeKey<Color> SCALE_HANDLE_STROKE_COLOR = new AttributeKey<Color>("scaleHandleStrokeColor", Color.WHITE);
    /**
     * Handle fill color.
     */
    public final static AttributeKey<Color> SCALE_HANDLE_FILL_COLOR = new AttributeKey<Color>("scaleHandleFillColor", Color.ORANGE.darker());
    /**
     * Handle stroke color.
     */
    public final static AttributeKey<Color> RESIZE_HANDLE_STROKE_COLOR = new AttributeKey<Color>("resizeHandleStrokeColor", Color.WHITE);
    /**
     * Handle stroke color.
     */
    public final static AttributeKey<Color> RESIZE_HANDLE_FILL_COLOR = new AttributeKey<Color>("resizeHandleFillColor", Color.BLUE);
    /**
     * Handle stroke color.
     */
    public final static AttributeKey<Color> RESIZE_OUTLINE_HANDLE_STROKE_COLOR_1 = new AttributeKey<Color>("resizeOutlineHandleStrokeColor1", Color.BLUE);
    /**
     * Handle stroke color.
     */
    public final static AttributeKey<Color> RESIZE_OUTLINE_HANDLE_STROKE_COLOR_2 = new AttributeKey<Color>("resizeOutlineHandleStrokeColor2", Color.WHITE);
    /**
     * Handle stroke.
     */
    public final static AttributeKey<Stroke> RESIZE_OUTLINE_HANDLE_STROKE_1 = new AttributeKey<Stroke>("bezierControlPointHandleStroke1", new BasicStroke(
            2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0f, new float[] { 3f, 7f }, 0f
            ));
    /**
     * Handle stroke.
     */
    public final static AttributeKey<Stroke> RESIZE_OUTLINE_HANDLE_STROKE_2 = new AttributeKey<Stroke>("bezierControlPointHandleStroke2", new BasicStroke(
            2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0f, new float[] { 3f, 7f }, 5f
            ));
    /**
     * Handle stroke color.
     */
    public final static AttributeKey<Color> TRANSFORM_OUTLINE_HANDLE_STROKE_COLOR_1 = new AttributeKey<Color>("bezierControlPointStrokeColor1", Color.GREEN.darker());
    /**
     * Handle stroke color.
     */
    public final static AttributeKey<Color> TRANSFORM_OUTLINE_HANDLE_STROKE_COLOR_2 = new AttributeKey<Color>("bezierControlPointStrokeColor2", Color.WHITE);
    /**
     * Handle stroke.
     */
    public final static AttributeKey<Stroke> TRANSFORM_OUTLINE_HANDLE_STROKE_1 = new AttributeKey<Stroke>("bezierControlPointHandleStroke1", new BasicStroke(
            2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0f, new float[] { 3f, 7f }, 0f
            ));
    /**
     * Handle stroke.
     */
    public final static AttributeKey<Stroke> TRANSFORM_OUTLINE_HANDLE_STROKE_2 = new AttributeKey<Stroke>("bezierControlPointHandleStroke2", new BasicStroke(
            2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0f, new float[] { 3f, 7f }, 5f
            ));
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
    public final static AttributeKey<Color> CONNECTED_CONNECTOR_HANDLE_FILL_COLOR = new AttributeKey<Color>("connectedConnectorHandleFillColor",  Color.GREEN);
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
