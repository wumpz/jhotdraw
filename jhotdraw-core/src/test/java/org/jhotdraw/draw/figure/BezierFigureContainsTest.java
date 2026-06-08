/*
 * @(#)BezierFigureContainsTest.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.figure;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.geom.Point2D;
import org.jhotdraw.utils.geom.path.BezierPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Characterization tests for {@link BezierFigure#contains(Point2D.Double, double)}.
 *
 * <p>Written before any refactoring to pin the observable hit-test behaviour. The same tests must
 * pass unchanged after Extract Method + Decompose Conditional is applied.
 *
 * <p>Fixture figures:
 *
 * <pre>
 *   closedSquare — closed BezierFigure with nodes at (10,10), (110,10), (110,110), (10,110)
 *   openLine     — open BezierFigure from (0,0) to (100,0)
 * </pre>
 */
public class BezierFigureContainsTest {

  private static final double SCALE = 1.0;

  /** Closed square-shaped bezier, corners at (10,10)–(110,110). */
  private BezierFigure closedSquare;

  /** Open straight line from (0,0) to (100,0). */
  private BezierFigure openLine;

  @BeforeEach
  void setUp() {
    closedSquare = new BezierFigure(true);
    closedSquare.addNode(new BezierPath.Node(10, 10));
    closedSquare.addNode(new BezierPath.Node(110, 10));
    closedSquare.addNode(new BezierPath.Node(110, 110));
    closedSquare.addNode(new BezierPath.Node(10, 110));

    openLine = new BezierFigure(false);
    openLine.addNode(new BezierPath.Node(0, 0));
    openLine.addNode(new BezierPath.Node(100, 0));
  }

  // ── closed figure ──────────────────────────────────────────────────────────

  @Test
  void closedFigure_pointDeepInside_returnsTrue() {
    assertThat(closedSquare.contains(new Point2D.Double(60, 60), SCALE)).isTrue();
  }

  @Test
  void closedFigure_pointClearlyOutside_returnsFalse() {
    assertThat(closedSquare.contains(new Point2D.Double(300, 300), SCALE)).isFalse();
  }

  @Test
  void closedFigure_pointOnCornerNode_returnsTrue() {
    // A corner node itself is on the boundary and must be inside the hit region.
    assertThat(closedSquare.contains(new Point2D.Double(10, 10), SCALE)).isTrue();
  }

  @Test
  void closedFigure_pointJustOutsideButWithinStrokeTolerance_returnsTrue() {
    // A point 1 pixel outside one edge is within the default stroke-hit tolerance.
    assertThat(closedSquare.contains(new Point2D.Double(60, 9), SCALE)).isTrue();
  }

  @Test
  void closedFigure_pointFarOutsideStrokeTolerance_returnsFalse() {
    // 50 pixels beyond any edge is well outside even a generous stroke tolerance.
    assertThat(closedSquare.contains(new Point2D.Double(60, -50), SCALE)).isFalse();
  }

  // ── open figure ────────────────────────────────────────────────────────────

  @Test
  void openFigure_pointOnPathMidpoint_returnsTrue() {
    // Mid-point of the horizontal line segment sits exactly on the outline.
    assertThat(openLine.contains(new Point2D.Double(50, 0), SCALE)).isTrue();
  }

  @Test
  void openFigure_pointFarFromPath_returnsFalse() {
    assertThat(openLine.contains(new Point2D.Double(50, 200), SCALE)).isFalse();
  }
}
