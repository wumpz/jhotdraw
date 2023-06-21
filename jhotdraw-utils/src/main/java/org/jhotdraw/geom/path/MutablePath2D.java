/*
 * Copyright (C) 2023 JHotDraw.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.jhotdraw.geom.path;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Path storage as a List of Nodes. This is simply a decorator around a real Path2D.Double. */
public class MutablePath2D implements Shape {

  protected final List<Node> nodes = new ArrayList<>();

  private Path2D.Double pathCache = null;

  private int windingRule = PathIterator.WIND_NON_ZERO;

  public int size() {
    return nodes.size();
  }

  @Override
  public boolean contains(double x, double y) {
    return toPath2D().contains(x, y);
  }

  @Override
  public boolean contains(Point2D p) {
    return toPath2D().contains(p);
  }

  @Override
  public boolean contains(double x, double y, double w, double h) {
    return toPath2D().contains(x, y, w, h);
  }

  @Override
  public boolean contains(Rectangle2D r) {
    return toPath2D().contains(r);
  }

  @Override
  public Rectangle getBounds() {
    return toPath2D().getBounds();
  }

  @Override
  public Rectangle2D getBounds2D() {
    return toPath2D().getBounds2D();
  }

  @Override
  public final PathIterator getPathIterator(AffineTransform at) {
    return toPath2D().getPathIterator(at);
  }

  @Override
  public PathIterator getPathIterator(AffineTransform at, double flatness) {
    return toPath2D().getPathIterator(at, flatness);
  }

  @Override
  public boolean intersects(double x, double y, double w, double h) {
    return toPath2D().intersects(x, y, w, h);
  }

  @Override
  public boolean intersects(Rectangle2D r) {
    return toPath2D().intersects(r);
  }

  public void lineTo(double x, double y) {
    nodes.add(Node.lineTo(x, y));
    invalidatePathCache();
  }

  public void moveTo(double x, double y) {
    if (!nodes.isEmpty() && nodes.get(nodes.size() - 1).pointType == PathIterator.SEG_MOVETO) {
      nodes.set(nodes.size() - 1, Node.moveTo(x, y));
    } else {
      nodes.add(Node.moveTo(x, y));
    }
    invalidatePathCache();
  }

  public void changeNode(int idx, Consumer<Node> change) {
    change.accept(nodes.get(idx));
    invalidatePathCache();
  }

  public void removeNode(int idx) {
    nodes.remove(idx);
    invalidatePathCache();
  }

  public Point2D.Double getNodePoint(int idx) {
    return nodes.get(idx).getPoint();
  }

  public Path2D.Double toPath2D() {
    if (pathCache != null) {
      return pathCache;
    }
    Path2D.Double path = new Path2D.Double(windingRule);
    for (var node : nodes) {
      switch (node.pointType) {
        case PathIterator.SEG_MOVETO -> path.moveTo(node.values[0], node.values[1]);
        case PathIterator.SEG_LINETO -> path.lineTo(node.values[0], node.values[1]);
        case PathIterator.SEG_CLOSE -> path.closePath();
      }
    }
    pathCache = path;
    return path;
  }

  protected void invalidatePathCache() {
    pathCache = null;
  }

  public static class Node {

    final int pointType;
    final double values[];

    public static final int[] SIZE_FOR_POINTTYPE = {2, 2, 4, 6, 0};

    Node(int pointType) {
      this.pointType = pointType;
      this.values = new double[SIZE_FOR_POINTTYPE[pointType]];
    }

    public Node withPoint(double x, double y) {
      values[0] = x;
      values[1] = y;
      return this;
    }

    public Point2D.Double getPoint() {
      return new Point2D.Double(values[0], values[1]);
    }

    public static Node moveTo(double x, double y) {
      return new Node(PathIterator.SEG_MOVETO).withPoint(x, y);
    }

    public static Node lineTo(double x, double y) {
      return new Node(PathIterator.SEG_LINETO).withPoint(x, y);
    }
  }
}
