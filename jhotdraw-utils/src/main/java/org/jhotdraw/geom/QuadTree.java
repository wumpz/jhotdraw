/*
 * @(#)QuadTree.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.geom;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * A QuadTree allows to quickly find an object on a two-dimensional space.
 *
 * <p>QuadTree recursively subdivides a space into four rectangles. Each node of a QuadTree
 * subdivides the space covered by the rectangle of its parent node into four smaller rectangles
 * covering the upper left, upper right, lower left and lower right quadrant of the parent
 * rectangle.
 */
public class QuadTree<T> implements Serializable {

  private static final long serialVersionUID = 1L;
  private final HashMap<T, Rectangle2D.Double> outside = new HashMap<>();
  private final QuadNode root;
  private final int maxCapacity = 32;
  private final int minSize = 32;
  private final int maxOutside = 32;

  public QuadTree() {
    root = new QuadNode(new Rectangle2D.Double(0, 0, 800, 600));
  }

  public QuadTree(Rectangle2D.Double bounds) {
    root = new QuadNode(bounds);
  }

  public void add(T o, Rectangle2D.Double bounds) {
    if (root.bounds.contains(bounds)) {
      root.add(o, (Rectangle2D.Double) bounds.clone());
    } else {
      outside.put(o, (Rectangle2D.Double) bounds.clone());
      if (outside.size() > maxOutside) {
        reorganize();
      }
    }
  }

  public void reorganize() {
    root.join();
    outside.putAll(root.objects);
    root.objects.clear();
    Iterator<Map.Entry<T, Rectangle2D.Double>> i = outside.entrySet().iterator();
    Map.Entry<T, Rectangle2D.Double> entry = i.next();
    Rectangle2D.Double treeBounds = (Rectangle2D.Double) (entry.getValue()).clone();
    while (i.hasNext()) {
      entry = i.next();
      Rectangle2D.Double bounds = entry.getValue();
      treeBounds.add(bounds);
    }
    root.bounds = treeBounds;
    i = outside.entrySet().iterator();
    while (i.hasNext()) {
      entry = i.next();
      root.add(entry.getKey(), entry.getValue());
    }
    outside.clear();
  }

  public void remove(T o) {
    outside.remove(o);
    root.remove(o);
  }

  public Collection<T> findContains(Point2D.Double p) {
    HashSet<T> result = new HashSet<>();
    root.findContains(p, result);
    for (Map.Entry<T, Rectangle2D.Double> entry : outside.entrySet()) {
      if (entry.getValue().contains(p)) {
        result.add(entry.getKey());
      }
    }
    return result;
  }

  public Collection<T> findIntersects(Rectangle2D r) {
    return findIntersects(new Rectangle2D.Double(r.getX(), r.getY(), r.getWidth(), r.getHeight()));
  }

  public Collection<T> findIntersects(Rectangle2D.Double r) {
    HashSet<T> result = new HashSet<>();
    root.findIntersects(r, result);
    for (Map.Entry<T, Rectangle2D.Double> entry : outside.entrySet()) {
      if (entry.getValue().intersects(r)) {
        result.add(entry.getKey());
      }
    }
    return result;
  }

  public Collection<T> findInside(Rectangle2D.Double r) {
    HashSet<T> result = new HashSet<>();
    root.findInside(r, result);
    for (Map.Entry<T, Rectangle2D.Double> entry : outside.entrySet()) {
      if (r.contains(entry.getValue())) {
        result.add(entry.getKey());
      }
    }
    return result;
  }

  private class QuadNode implements Serializable {

    private static final long serialVersionUID = 1L;
    private Rectangle2D.Double bounds;

    /**
     * We store an object into this map, if 1) the bounds of the object contain our bounds or 2) we
     * are a leaf.
     *
     * <p>key = Object value = Rectangle2D.Double
     */
    private HashMap<T, Rectangle2D.Double> objects;

    private QuadNode northEast;
    private QuadNode northWest;
    private QuadNode southEast;
    private QuadNode southWest;

    public QuadNode(Rectangle2D.Double bounds) {
      this.bounds = bounds;
      this.objects = new HashMap<>();
    }

    public boolean isLeaf() {
      return northEast == null;
    }

    public void remove(T o) {
      if (objects.remove(o) == null && !isLeaf()) {
        northEast.remove(o);
        northWest.remove(o);
        southEast.remove(o);
        southWest.remove(o);
      }
    }

    public void add(T o, Rectangle2D.Double oBounds) {
      // Do we have to split?
      if (isLeaf()
          && objects.size() >= maxCapacity
          && bounds.width > minSize
          && bounds.height > minSize) {
        split();
      }
      if (isLeaf() || oBounds.contains(bounds)) {
        // We put an object into our hashtable if we are
        // a leaf, or if the bounds of the object contain our bounds.
        objects.put(o, oBounds);
      } else {
        if (northEast.bounds.intersects(oBounds)) {
          northEast.add(o, oBounds);
        }
        if (northWest.bounds.intersects(oBounds)) {
          northWest.add(o, oBounds);
        }
        if (southEast.bounds.intersects(oBounds)) {
          southEast.add(o, oBounds);
        }
        if (southWest.bounds.intersects(oBounds)) {
          southWest.add(o, oBounds);
        }
      }
    }

    public void split() {
      if (isLeaf()) {
        double hw = bounds.width / 2;
        double hh = bounds.height / 2;
        northWest = new QuadNode(new Rectangle2D.Double(bounds.x, bounds.y, hw, hh));
        northEast =
            new QuadNode(new Rectangle2D.Double(bounds.x + hw, bounds.y, bounds.width - hw, hh));
        southWest =
            new QuadNode(new Rectangle2D.Double(bounds.x, bounds.y + hh, hw, bounds.height - hh));
        southEast = new QuadNode(new Rectangle2D.Double(
            bounds.x + hw, bounds.y + hh, bounds.width - hw, bounds.height - hh));
        HashMap<T, Rectangle2D.Double> temp = objects;
        objects = new HashMap<>();
        for (Map.Entry<T, Rectangle2D.Double> entry : temp.entrySet()) {
          add(entry.getKey(), entry.getValue());
        }
      }
    }

    public void join() {
      if (!isLeaf()) {
        northWest.join();
        northEast.join();
        southWest.join();
        southEast.join();
        objects.putAll(northWest.objects);
        objects.putAll(northEast.objects);
        objects.putAll(southWest.objects);
        objects.putAll(southEast.objects);
        northWest = null;
        northEast = null;
        southWest = null;
        southEast = null;
      }
    }

    public void findContains(Point2D.Double p, HashSet<T> result) {
      if (bounds.contains(p)) {
        for (Map.Entry<T, Rectangle2D.Double> entry : objects.entrySet()) {
          if (entry.getValue().contains(p)) {
            result.add(entry.getKey());
          }
        }
        if (!isLeaf()) {
          northWest.findContains(p, result);
          northEast.findContains(p, result);
          southWest.findContains(p, result);
          southEast.findContains(p, result);
        }
      }
    }

    public void findIntersects(Rectangle2D.Double r, HashSet<T> result) {
      if (bounds.intersects(r)) {
        for (Map.Entry<T, Rectangle2D.Double> entry : objects.entrySet()) {
          if (entry.getValue().intersects(r)) {
            result.add(entry.getKey());
          }
        }
        if (!isLeaf()) {
          northWest.findIntersects(r, result);
          northEast.findIntersects(r, result);
          southWest.findIntersects(r, result);
          southEast.findIntersects(r, result);
        }
      }
    }

    public void findInside(Rectangle2D.Double r, HashSet<T> result) {
      if (bounds.intersects(r)) {
        for (Map.Entry<T, Rectangle2D.Double> entry : objects.entrySet()) {
          if (r.contains(entry.getValue())) {
            result.add(entry.getKey());
          }
        }
        if (!isLeaf()) {
          northWest.findInside(r, result);
          northEast.findInside(r, result);
          southWest.findInside(r, result);
          southEast.findInside(r, result);
        }
      }
    }
  }
}
