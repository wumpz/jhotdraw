/**
 * @(#)HandleAttributeKeys.java
 *
 * <p>Copyright (c) 2008-2010 The authors and contributors of JHotDraw. You may not use, copy or
 * modify this file, except in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.handle;

import java.awt.*;
import org.jhotdraw.draw.*;

/**
 * Defines a put of well known {@link Handle} attributes.
 *
 * <p>If you want different attribute values for your own editor, put the desired values using
 * {@link DrawingEditor#setHandleAttribute(org.jhotdraw.draw.AttributeKey, java.lang.Object)}.
 */
public class HandleAttributeKeys {

  /** Fill color of disabled handles. */
  private static final Color FILL_COLOR_DISABLED = new Color(0x80000000, true);

  /** Stroke color of disabled handles. */
  private static final Color STROKE_COLOR_DISABLED = new Color(0x80ffffff, true);

  /** General handle size. */
  public static final AttributeKey<Integer> HANDLE_SIZE =
      new AttributeKey<>("handleSize", Integer.class, 7);

  /** General handle stroke color. */
  public static final AttributeKey<Color> HANDLE_STROKE_COLOR =
      new AttributeKey<>("handleStrokeColor", Color.class, Color.WHITE);

  /** General handle fill color. */
  public static final AttributeKey<Color> HANDLE_FILL_COLOR =
      new AttributeKey<>("handleFillColor", Color.class, Color.BLACK);

  /** General handle stroke. */
  public static final AttributeKey<Stroke> HANDLE_STROKE =
      new AttributeKey<>("handleStroke", Stroke.class, new BasicStroke(1f));

  /** General handle stroke color. */
  public static final AttributeKey<Color> HANDLE_STROKE_COLOR_DISABLED =
      new AttributeKey<>("handleStrokeColor", Color.class, STROKE_COLOR_DISABLED);

  /** General handle fill color. */
  public static final AttributeKey<Color> HANDLE_FILL_COLOR_DISABLED =
      new AttributeKey<>("handleFillColor", Color.class, FILL_COLOR_DISABLED);

  // --
  /** Rotate handle stroke color. */
  public static final AttributeKey<Color> ROTATE_HANDLE_STROKE_COLOR =
      new AttributeKey<>("rotateHandleStrokeColor", Color.class, Color.WHITE);

  /** Rotate handle fill color. */
  public static final AttributeKey<Color> ROTATE_HANDLE_FILL_COLOR =
      new AttributeKey<>("rotateHandleFillColor", Color.class, Color.MAGENTA);

  // --
  /** Rotate handle stroke color. */
  public static final AttributeKey<Color> ROTATE_HANDLE_STROKE_COLOR_DISABLED =
      new AttributeKey<>("rotateHandleStrokeColorDisabled", Color.class, STROKE_COLOR_DISABLED);

  /** Rotate handle fill color. */
  public static final AttributeKey<Color> ROTATE_HANDLE_FILL_COLOR_DISABLED =
      new AttributeKey<>("rotateHandleFillColorDisabled", Color.class, FILL_COLOR_DISABLED);

  // --
  /** Bezier control point handle stroke color. */
  public static final AttributeKey<Color> BEZIER_CONTROL_POINT_HANDLE_STROKE_COLOR =
      new AttributeKey<>("bezierControlPointHandleStrokeColor", Color.class, Color.WHITE);

  /** Bezier control point handle fill color. */
  public static final AttributeKey<Color> BEZIER_CONTROL_POINT_HANDLE_FILL_COLOR =
      new AttributeKey<>("bezierControlPointHandleFillColor", Color.class, Color.BLUE);

  /** Bezier tangent line stroke color 1. */
  public static final AttributeKey<Color> BEZIER_TANGENT_COLOR_1 =
      new AttributeKey<>("bezierTangentColor1", Color.class, Color.WHITE);

  /** Bezier tangent line stroke color 2. */
  public static final AttributeKey<Color> BEZIER_TANGENT_COLOR_2 =
      new AttributeKey<>("bezierTangentColor1", Color.class, Color.BLUE);

  /** Bezier tangent line stroke 1. */
  public static final AttributeKey<Stroke> BEZIER_TANGENT_STROKE_1 = new AttributeKey<>(
      "bezierTangentStroke1",
      Stroke.class,
      new BasicStroke(
          1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0f, new float[] {5f, 5f}, 0f));

  /** Bezier tangent line stroke 2. */
  public static final AttributeKey<Stroke> BEZIER_TANGENT_STROKE_2 = new AttributeKey<>(
      "bezierTangentStroke2",
      Stroke.class,
      new BasicStroke(
          1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0f, new float[] {5f, 5f}, 5f));

  /** Bezier node handle stroke color. */
  public static final AttributeKey<Color> BEZIER_NODE_HANDLE_STROKE_COLOR =
      new AttributeKey<>("bezierControlPointStrokeColor", Color.class, Color.WHITE);

  /** Bezier node handle fill color. */
  public static final AttributeKey<Color> BEZIER_NODE_HANDLE_FILL_COLOR =
      new AttributeKey<>("bezierControlPointFillColor", Color.class, new Color(0x00a8ff));

  /** Bezier path stroke color 1. */
  public static final AttributeKey<Color> BEZIER_PATH_COLOR_1 =
      new AttributeKey<>("bezierPathColor1", Color.class, Color.WHITE);

  /** Bezier path stroke color 2. */
  public static final AttributeKey<Color> BEZIER_PATH_COLOR_2 =
      new AttributeKey<>("bezierPathColor2", Color.class, new Color(0x00a8ff));

  /** Bezier path stroke 1. */
  public static final AttributeKey<Stroke> BEZIER_PATH_STROKE_1 = new AttributeKey<>(
      "bezierPathStroke1",
      Stroke.class,
      new BasicStroke(3f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));

  /** Bezier path stroke 2. */
  public static final AttributeKey<Stroke> BEZIER_PATH_STROKE_2 =
      new AttributeKey<>("bezierPathStroke2", Stroke.class, new BasicStroke(1f));

  // ---
  /** Bezier path hover stroke color 1. */
  public static final AttributeKey<Color> BEZIER_PATH_COLOR_1_HOVER =
      new AttributeKey<>("bezierPathColor1Hover", Color.class, null);

  /** Bezier path hover stroke color 2. */
  public static final AttributeKey<Color> BEZIER_PATH_COLOR_2_HOVER =
      new AttributeKey<>("bezierPathColor2Hover", Color.class, new Color(0x00a8ff));

  /** Bezier path hover stroke 1. */
  public static final AttributeKey<Stroke> BEZIER_PATH_STROKE_1_HOVER =
      new AttributeKey<>("bezierPathStroke1Hover", Stroke.class, null);

  /** Bezier path hover stroke 2. */
  public static final AttributeKey<Stroke> BEZIER_PATH_STROKE_2_HOVER =
      new AttributeKey<>("bezierPathStroke2Hover", Stroke.class, new BasicStroke(1f));

  // ---
  /** Bezier control point handle stroke color. */
  public static final AttributeKey<Color> BEZIER_CONTROL_POINT_HANDLE_STROKE_COLOR_DISABLED =
      new AttributeKey<>(
          "bezierControlPointHandleStrokeColorDisabled", Color.class, STROKE_COLOR_DISABLED);

  /** Bezier control point handle fill color. */
  public static final AttributeKey<Color> BEZIER_CONTROL_POINT_HANDLE_FILL_COLOR_DISABLED =
      new AttributeKey<>(
          "bezierControlPointHandleFillColorDisabled", Color.class, FILL_COLOR_DISABLED);

  /** Bezier tangent line stroke color 1. */
  public static final AttributeKey<Color> BEZIER_TANGENT_COLOR_1_DISABLED =
      new AttributeKey<>("bezierTangentColor1Disabled", Color.class, STROKE_COLOR_DISABLED);

  /** Bezier tangent line stroke color 2. */
  public static final AttributeKey<Color> BEZIER_TANGENT_COLOR_2_DISABLED =
      new AttributeKey<>("bezierTangentColor1Disabled", Color.class, FILL_COLOR_DISABLED);

  /** Bezier tangent line stroke 1. */
  public static final AttributeKey<Stroke> BEZIER_TANGENT_STROKE_1_DISABLED = new AttributeKey<>(
      "bezierTangentStroke1Disabled",
      Stroke.class,
      new BasicStroke(
          1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0f, new float[] {5f, 5f}, 0f));

  /** Bezier tangent line stroke 2. */
  public static final AttributeKey<Stroke> BEZIER_TANGENT_STROKE_2_DISABLED = new AttributeKey<>(
      "bezierTangentStroke2Disabled",
      Stroke.class,
      new BasicStroke(
          1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0f, new float[] {5f, 5f}, 5f));

  /** Bezier node handle stroke color. */
  public static final AttributeKey<Color> BEZIER_NODE_HANDLE_STROKE_COLOR_DISABLED =
      new AttributeKey<>(
          "bezierControlPointStrokeColorDisabled", Color.class, STROKE_COLOR_DISABLED);

  /** Bezier node handle fill color. */
  public static final AttributeKey<Color> BEZIER_NODE_HANDLE_FILL_COLOR_DISABLED =
      new AttributeKey<>("bezierControlPointFillColorDisabled", Color.class, FILL_COLOR_DISABLED);

  /** Bezier path stroke color 1. */
  public static final AttributeKey<Color> BEZIER_PATH_COLOR_1_DISABLED =
      new AttributeKey<>("bezierPathColor1Disabled", Color.class, Color.WHITE);

  /** Bezier path stroke color 2. */
  public static final AttributeKey<Color> BEZIER_PATH_COLOR_2_DISABLED =
      new AttributeKey<>("bezierPathColor2Disabled", Color.class, new Color(0x0));

  /** Bezier path stroke 1. */
  public static final AttributeKey<Stroke> BEZIER_PATH_STROKE_1_DISABLED = new AttributeKey<>(
      "bezierPathStroke1Disabled",
      Stroke.class,
      new BasicStroke(3f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));

  /** Bezier path stroke 2. */
  public static final AttributeKey<Stroke> BEZIER_PATH_STROKE_2_DISABLED =
      new AttributeKey<>("bezierPathStroke2Disabled", Stroke.class, new BasicStroke(1f));

  // ---
  /** Scale handle stroke color. */
  public static final AttributeKey<Color> SCALE_HANDLE_STROKE_COLOR =
      new AttributeKey<>("scaleHandleStrokeColor", Color.class, Color.WHITE);

  /** Scale handle fill color. */
  public static final AttributeKey<Color> SCALE_HANDLE_FILL_COLOR =
      new AttributeKey<>("scaleHandleFillColor", Color.class, Color.ORANGE.darker());

  /** Resize handle stroke color. */
  public static final AttributeKey<Color> RESIZE_HANDLE_STROKE_COLOR =
      new AttributeKey<>("resizeHandleStrokeColor", Color.class, Color.WHITE);

  /** Resize handle fill color. */
  public static final AttributeKey<Color> RESIZE_HANDLE_FILL_COLOR =
      new AttributeKey<>("resizeHandleFillColor", Color.class, Color.BLUE);

  /** Resize bounds stroke color 1. */
  public static final AttributeKey<Color> RESIZE_BOUNDS_COLOR_1 =
      new AttributeKey<>("resizeBoundsColor1", Color.class, Color.WHITE);

  /** Resize bounds stroke color 2. */
  public static final AttributeKey<Color> RESIZE_BOUNDS_COLOR_2 =
      new AttributeKey<>("resizeBoundsColor2", Color.class, Color.BLUE);

  /** Resize bounds stroke 1. */
  public static final AttributeKey<Stroke> RESIZE_BOUNDS_STROKE_1 = new AttributeKey<>(
      "resizeBoundsStroke1",
      Stroke.class,
      new BasicStroke(3f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));

  /** Resize bounds stroke 2. */
  public static final AttributeKey<Stroke> RESIZE_BOUNDS_STROKE_2 =
      new AttributeKey<>("resizeBoundsStroke2", Stroke.class, new BasicStroke(1f));

  //
  /** Disabled resize bounds stroke color 1. */
  public static final AttributeKey<Color> RESIZE_BOUNDS_COLOR_1_DISABLED =
      new AttributeKey<>("resizeBoundsColor1Hover", Color.class, STROKE_COLOR_DISABLED);

  /** Disabled resize bounds hover color 2. */
  public static final AttributeKey<Color> RESIZE_BOUNDS_COLOR_2_DISABLED =
      new AttributeKey<>("resizeBoundsColor2Hover", Color.class, FILL_COLOR_DISABLED);

  /** Disabled resize bounds stroke 1. */
  public static final AttributeKey<Stroke> RESIZE_BOUNDS_STROKE_1_DISABLED =
      new AttributeKey<>("resizeBoundsStroke1Hover", Stroke.class, new BasicStroke(3f));

  /** Disabled bounds stroke 2. */
  public static final AttributeKey<Stroke> RESIZE_BOUNDS_STROKE_2_DISABLED =
      new AttributeKey<>("resizeBoundsStroke2Hover", Stroke.class, new BasicStroke(1f));

  // ---
  //
  /** Handle bounds hover stroke color 1. */
  public static final AttributeKey<Color> RESIZE_BOUNDS_COLOR_1_HOVER =
      new AttributeKey<>("resizeBoundsColor1Hover", Color.class, null);

  /** Resize bounds hover stroke color 2. */
  public static final AttributeKey<Color> RESIZE_BOUNDS_COLOR_2_HOVER =
      new AttributeKey<>("resizeBoundsColor2Hover", Color.class, Color.BLUE);

  /** Resize bounds hover stroke 1. */
  public static final AttributeKey<Stroke> RESIZE_BOUNDS_STROKE_1_HOVER =
      new AttributeKey<>("resizeBoundsStroke1Hover", Stroke.class, null);

  /** Resize bounds hover stroke 2. */
  public static final AttributeKey<Stroke> RESIZE_BOUNDS_STROKE_2_HOVER =
      new AttributeKey<>("resizeBoundsStroke2Hover", Stroke.class, new BasicStroke(1f));

  // ---
  /** Transform handle stroke color. */
  public static final AttributeKey<Color> TRANSFORM_HANDLE_STROKE_COLOR =
      new AttributeKey<>("transformHandleStrokeColor", Color.class, Color.WHITE);

  /** Transform handle fill color. */
  public static final AttributeKey<Color> TRANSFORM_HANDLE_FILL_COLOR =
      new AttributeKey<>("transformHandleFillColor", Color.class, Color.MAGENTA);

  // ---
  /** Transform handle stroke color. */
  public static final AttributeKey<Color> TRANSFORM_HANDLE_STROKE_COLOR_DISABLED =
      new AttributeKey<>("transformHandleStrokeColorDisabled", Color.class, STROKE_COLOR_DISABLED);

  /** Transform handle fill color. */
  public static final AttributeKey<Color> TRANSFORM_HANDLE_FILL_COLOR_DISABLED =
      new AttributeKey<>("transformHandleFillColorDisabled", Color.class, FILL_COLOR_DISABLED);

  /** Transform bounds color 1. */
  public static final AttributeKey<Color> TRANSFORM_BOUNDS_COLOR_1 =
      new AttributeKey<>("transformBoundsColor1", Color.class, Color.WHITE);

  /** Transform bounds color 2. */
  public static final AttributeKey<Color> TRANSFORM_BOUNDS_COLOR_2 =
      new AttributeKey<>("transformBoundsColor2", Color.class, Color.MAGENTA);

  /** Transform bounds stroke 1. */
  public static final AttributeKey<Stroke> TRANSFORM_BOUNDS_STROKE_1 = new AttributeKey<>(
      "transformBoundsStroke1",
      Stroke.class,
      new BasicStroke(3f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));

  /** Transform bounds stroke 2. */
  public static final AttributeKey<Stroke> TRANSFORM_BOUNDS_STROKE_2 =
      new AttributeKey<>("transformBoundsStroke2", Stroke.class, new BasicStroke(1f));

  // ---
  /** Transform bounds hover color 1. */
  public static final AttributeKey<Color> TRANSFORM_BOUNDS_COLOR_1_HOVER =
      new AttributeKey<>("transformBoundsColor1Hover", Color.class, null);

  /** Transform bounds hover color 2. */
  public static final AttributeKey<Color> TRANSFORM_BOUNDS_COLOR_2_HOVER =
      new AttributeKey<>("transformBoundsColor2Hover", Color.class, Color.MAGENTA);

  /** Transform bounds hover stroke 1. */
  public static final AttributeKey<Stroke> TRANSFORM_BOUNDS_STROKE_1_HOVER =
      new AttributeKey<>("transformBoundsStroke1Hover", Stroke.class, null);

  /** Transform bounds hover stroke 2. */
  public static final AttributeKey<Stroke> TRANSFORM_BOUNDS_STROKE_2_HOVER =
      new AttributeKey<>("transformBoundsStroke2Hover", Stroke.class, new BasicStroke(1f));

  // ---
  // ---
  /** Transform bounds disabled color 1. */
  public static final AttributeKey<Color> TRANSFORM_BOUNDS_COLOR_1_DISABLED =
      new AttributeKey<>("transformBoundsColor1Disabled", Color.class, STROKE_COLOR_DISABLED);

  /** Transform bounds disabled color 2. */
  public static final AttributeKey<Color> TRANSFORM_BOUNDS_COLOR_2_DISABLED =
      new AttributeKey<>("transformBoundsColor2Disabled", Color.class, FILL_COLOR_DISABLED);

  /** Transform bounds disabled stroke 1. */
  public static final AttributeKey<Stroke> TRANSFORM_BOUNDS_STROKE_1_DISABLED =
      new AttributeKey<>("transformBoundsStroke1Disabled", Stroke.class, new BasicStroke(3f));

  /** Transform bounds disabled stroke 2. */
  public static final AttributeKey<Stroke> TRANSFORM_BOUNDS_STROKE_2_DISABLED =
      new AttributeKey<>("transformBoundsStroke2Disabled", Stroke.class, new BasicStroke(1f));

  // ---
  // ---
  /** Group handle stroke color. */
  public static final AttributeKey<Color> GROUP_HANDLE_STROKE_COLOR =
      new AttributeKey<>("transformHandleStrokeColor", Color.class, Color.WHITE);

  /** Group handle fill color. */
  public static final AttributeKey<Color> GROUP_HANDLE_FILL_COLOR =
      new AttributeKey<>("transformHandleFillColor", Color.class, Color.MAGENTA);

  // ---
  /** Group handle stroke color. */
  public static final AttributeKey<Color> GROUP_HANDLE_STROKE_COLOR_DISABLED =
      new AttributeKey<>("transformHandleStrokeColorDisabled", Color.class, STROKE_COLOR_DISABLED);

  /** Group handle fill color. */
  public static final AttributeKey<Color> GROUP_HANDLE_FILL_COLOR_DISABLED =
      new AttributeKey<>("transformHandleFillColorDisabled", Color.class, FILL_COLOR_DISABLED);

  /** Group bounds color 1. */
  public static final AttributeKey<Color> GROUP_BOUNDS_COLOR_1 =
      new AttributeKey<>("transformBoundsColor1", Color.class, Color.WHITE);

  /** Group bounds color 2. */
  public static final AttributeKey<Color> GROUP_BOUNDS_COLOR_2 =
      new AttributeKey<>("transformBoundsColor2", Color.class, Color.MAGENTA);

  /** Group bounds stroke 1. */
  public static final AttributeKey<Stroke> GROUP_BOUNDS_STROKE_1 = new AttributeKey<>(
      "transformBoundsStroke1",
      Stroke.class,
      new BasicStroke(3f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));

  /** Group bounds stroke 2. */
  public static final AttributeKey<Stroke> GROUP_BOUNDS_STROKE_2 = new AttributeKey<>(
      "transformBoundsStroke2",
      Stroke.class,
      new BasicStroke(
          1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1f, new float[] {2f, 2f}, 0f));

  // ---
  /** Group bounds hover color 1. */
  public static final AttributeKey<Color> GROUP_BOUNDS_COLOR_1_HOVER =
      new AttributeKey<>("transformBoundsColor1Hover", Color.class, null);

  /** Group bounds hover color 2. */
  public static final AttributeKey<Color> GROUP_BOUNDS_COLOR_2_HOVER =
      new AttributeKey<>("transformBoundsColor2Hover", Color.class, Color.MAGENTA);

  /** Group bounds hover stroke 1. */
  public static final AttributeKey<Stroke> GROUP_BOUNDS_STROKE_1_HOVER =
      new AttributeKey<>("transformBoundsStroke1Hover", Stroke.class, null);

  /** Group bounds hover stroke 2. */
  public static final AttributeKey<Stroke> GROUP_BOUNDS_STROKE_2_HOVER = new AttributeKey<>(
      "transformBoundsStroke2Hover",
      Stroke.class,
      new BasicStroke(
          1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1f, new float[] {2f, 2f}, 0f));

  // ---
  // ---
  /** Group bounds disabled color 1. */
  public static final AttributeKey<Color> GROUP_BOUNDS_COLOR_1_DISABLED =
      new AttributeKey<>("transformBoundsColor1Disabled", Color.class, STROKE_COLOR_DISABLED);

  /** Group bounds disabled color 2. */
  public static final AttributeKey<Color> GROUP_BOUNDS_COLOR_2_DISABLED =
      new AttributeKey<>("transformBoundsColor2Disabled", Color.class, FILL_COLOR_DISABLED);

  /** Group bounds disabled stroke 1. */
  public static final AttributeKey<Stroke> GROUP_BOUNDS_STROKE_1_DISABLED =
      new AttributeKey<>("transformBoundsStroke1Disabled", Stroke.class, new BasicStroke(3f));

  /** Group bounds disabled stroke 2. */
  public static final AttributeKey<Stroke> GROUP_BOUNDS_STROKE_2_DISABLED =
      new AttributeKey<>("transformBoundsStroke2Disabled", Stroke.class, new BasicStroke(1f));

  // ---
  /** Handle stroke color. */
  public static final AttributeKey<Color> CONNECTED_CONNECTION_HANDLE_STROKE_COLOR =
      new AttributeKey<>("connectedConnectionHandleStrokeColor", Color.class, Color.BLACK);

  /** Handle fill color. */
  public static final AttributeKey<Color> CONNECTED_CONNECTION_HANDLE_FILL_COLOR =
      new AttributeKey<>("connectedConnectionHandleFillColor", Color.class, Color.GREEN);

  /** Handle stroke color. */
  public static final AttributeKey<Color> DISCONNECTED_CONNECTION_HANDLE_STROKE_COLOR =
      new AttributeKey<>("disconnectedConnectionHandleStrokeColor", Color.class, Color.BLACK);

  /** Handle fill color. */
  public static final AttributeKey<Color> DISCONNECTED_CONNECTION_HANDLE_FILL_COLOR =
      new AttributeKey<>("disconnectedConnectionHandleFillColor", Color.class, Color.RED);

  /** Handle stroke color. */
  public static final AttributeKey<Color> CONNECTED_CONNECTOR_HANDLE_STROKE_COLOR =
      new AttributeKey<>("connectedConnectorHandleStrokeColor", Color.class, Color.BLACK);

  /** Handle fill color. */
  public static final AttributeKey<Color> CONNECTED_CONNECTOR_HANDLE_FILL_COLOR =
      new AttributeKey<>("connectedConnectorHandleFillColor", Color.class, Color.GREEN);

  /** Handle stroke color. */
  public static final AttributeKey<Color> DISCONNECTED_CONNECTOR_HANDLE_STROKE_COLOR =
      new AttributeKey<>("disconnectedConnectorHandleStrokeColor", Color.class, Color.BLACK);

  /** Handle fill color. */
  public static final AttributeKey<Color> DISCONNECTED_CONNECTOR_HANDLE_FILL_COLOR =
      new AttributeKey<>("disconnectedConnectorHandleFillColor", Color.class, Color.RED);

  /** Handle stroke color. */
  public static final AttributeKey<Color> MOVE_HANDLE_STROKE_COLOR =
      new AttributeKey<>("moveHandleStrokeColor", Color.class, Color.BLACK);

  /** Handle fill color. */
  public static final AttributeKey<Color> MOVE_HANDLE_FILL_COLOR =
      new AttributeKey<>("moveHandleFillColor", Color.class, Color.WHITE);

  /** Handle stroke color. */
  public static final AttributeKey<Color> NULL_HANDLE_STROKE_COLOR =
      new AttributeKey<>("nullHandleStrokeColor", Color.class, Color.DARK_GRAY);

  /** Handle fill color. */
  public static final AttributeKey<Color> NULL_HANDLE_FILL_COLOR =
      new AttributeKey<>("nullHandleFillColor", Color.class, null);

  /** Handle stroke color. */
  public static final AttributeKey<Color> OVERFLOW_HANDLE_STROKE_COLOR =
      new AttributeKey<>("overlfowHandleStrokeColor", Color.class, Color.RED);

  /** Handle fill color. */
  public static final AttributeKey<Color> OVERFLOW_HANDLE_FILL_COLOR =
      new AttributeKey<>("overflowHandleFillColor", Color.class, null);

  // --
  /** Attribute handle stroke color. */
  public static final AttributeKey<Color> ATTRIBUTE_HANDLE_STROKE_COLOR =
      new AttributeKey<>("attributeHandleStrokeColor", Color.class, Color.BLACK);

  /** Attribute handle fill color. */
  public static final AttributeKey<Color> ATTRIBUTE_HANDLE_FILL_COLOR =
      new AttributeKey<>("attributeSizeHandleFillColor", Color.class, Color.YELLOW);

  // --
  /** Attribute handle stroke color. */
  public static final AttributeKey<Color> ATTRIBUTE_HANDLE_STROKE_COLOR_DISABLED =
      new AttributeKey<>("attributeHandleStrokeColorDisabled", Color.class, STROKE_COLOR_DISABLED);

  /** Attribute handle fill color. */
  public static final AttributeKey<Color> ATTRIBUTE_HANDLE_FILL_COLOR_DISABLED =
      new AttributeKey<>("attributeSizeHandleFillColorDisabled", Color.class, FILL_COLOR_DISABLED);
}
