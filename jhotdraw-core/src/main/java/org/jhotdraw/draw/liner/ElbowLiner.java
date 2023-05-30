/*
 * @(#)ElbowLiner.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.liner;

import java.awt.geom.*;
import java.util.*;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.figure.ConnectionFigure;
import org.jhotdraw.draw.figure.LineConnectionFigure;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.geom.Geom;
import org.jhotdraw.geom.path.BezierPath;

/** A {@link Liner} that constrains a connection to orthogonal lines. */
public class ElbowLiner implements Liner {

  private double shoulderSize;

  public ElbowLiner() {
    this(20);
  }

  public ElbowLiner(double slantSize) {
    this.shoulderSize = slantSize;
  }

  @Override
  public Collection<Handle> createHandles(BezierPath path) {
    return Collections.emptyList();
  }

  @Override
  public void lineout(ConnectionFigure figure) {
    BezierPath path = ((LineConnectionFigure) figure).getBezierPath();
    Connector start = figure.getStartConnector();
    Connector end = figure.getEndConnector();
    if (start == null || end == null || path == null) {
      return;
    }
    // Special treatment if the connection connects the same figure
    if (figure.getStartFigure() == figure.getEndFigure()) {
      // Ensure path has exactly four nodes
      while (path.size() < 5) {
        path.add(1, new BezierPath.Node(0, 0));
      }
      while (path.size() > 5) {
        path.remove(1);
      }
      Point2D.Double sp = start.findStart(figure);
      Point2D.Double ep = end.findEnd(figure);
      Rectangle2D.Double sb = start.getBounds();
      Rectangle2D.Double eb = end.getBounds();
      int soutcode = sb.outcode(sp);
      if (soutcode == 0) {
        soutcode = Geom.outcode(sb, eb);
      }
      int eoutcode = eb.outcode(ep);
      if (eoutcode == 0) {
        eoutcode = Geom.outcode(sb, eb);
      }
      path.nodes().get(0).moveTo(sp);
      path.nodes().get(path.size() - 1).moveTo(ep);
      switch (soutcode) {
        case Geom.OUT_TOP:
          eoutcode = Geom.OUT_LEFT;
          break;
        case Geom.OUT_RIGHT:
          eoutcode = Geom.OUT_TOP;
          break;
        case Geom.OUT_BOTTOM:
          eoutcode = Geom.OUT_RIGHT;
          break;
        case Geom.OUT_LEFT:
          eoutcode = Geom.OUT_BOTTOM;
          break;
        default:
          eoutcode = Geom.OUT_TOP;
          soutcode = Geom.OUT_RIGHT;
          break;
      }
      path.nodes().get(1).moveTo(sp.x + shoulderSize, sp.y);
      if ((soutcode & Geom.OUT_RIGHT) != 0) {
        path.nodes().get(1).moveTo(sp.x + shoulderSize, sp.y);
      } else if ((soutcode & Geom.OUT_LEFT) != 0) {
        path.nodes().get(1).moveTo(sp.x - shoulderSize, sp.y);
      } else if ((soutcode & Geom.OUT_BOTTOM) != 0) {
        path.nodes().get(1).moveTo(sp.x, sp.y + shoulderSize);
      } else {
        path.nodes().get(1).moveTo(sp.x, sp.y - shoulderSize);
      }
      if ((eoutcode & Geom.OUT_RIGHT) != 0) {
        path.nodes().get(3).moveTo(ep.x + shoulderSize, ep.y);
      } else if ((eoutcode & Geom.OUT_LEFT) != 0) {
        path.nodes().get(3).moveTo(ep.x - shoulderSize, ep.y);
      } else if ((eoutcode & Geom.OUT_BOTTOM) != 0) {
        path.nodes().get(3).moveTo(ep.x, ep.y + shoulderSize);
      } else {
        path.nodes().get(3).moveTo(ep.x, ep.y - shoulderSize);
      }
      switch (soutcode) {
        case Geom.OUT_RIGHT:
          path.nodes().get(2).moveTo(path.nodes().get(1).x[0], path.nodes().get(3).y[0]);
          break;
        case Geom.OUT_TOP:
          path.nodes().get(2).moveTo(path.nodes().get(1).y[0], path.nodes().get(3).x[0]);
          break;
        case Geom.OUT_LEFT:
          path.nodes().get(2).moveTo(path.nodes().get(1).x[0], path.nodes().get(3).y[0]);
          break;
        case Geom.OUT_BOTTOM:
        default:
          path.nodes().get(2).moveTo(path.nodes().get(1).y[0], path.nodes().get(3).x[0]);
          break;
      }
    } else {
      Point2D.Double sp = start.findStart(figure);
      Point2D.Double ep = end.findEnd(figure);
      path.clear();
      path.add(new BezierPath.Node(sp.x, sp.y));
      if (sp.x == ep.x || sp.y == ep.y) {
        path.add(new BezierPath.Node(ep.x, ep.y));
      } else {
        Rectangle2D.Double sb = start.getBounds();
        sb.x += 5d;
        sb.y += 5d;
        sb.width -= 10d;
        sb.height -= 10d;
        Rectangle2D.Double eb = end.getBounds();
        eb.x += 5d;
        eb.y += 5d;
        eb.width -= 10d;
        eb.height -= 10d;
        int soutcode = sb.outcode(sp);
        if (soutcode == 0) {
          soutcode = Geom.outcode(sb, eb);
        }
        int eoutcode = eb.outcode(ep);
        if (eoutcode == 0) {
          eoutcode = Geom.outcode(eb, sb);
        }
        if ((soutcode & (Geom.OUT_TOP | Geom.OUT_BOTTOM)) != 0
            && (eoutcode & (Geom.OUT_TOP | Geom.OUT_BOTTOM)) != 0) {
          path.add(new BezierPath.Node(sp.x, (sp.y + ep.y) / 2));
          path.add(new BezierPath.Node(ep.x, (sp.y + ep.y) / 2));
        } else if ((soutcode & (Geom.OUT_LEFT | Geom.OUT_RIGHT)) != 0
            && (eoutcode & (Geom.OUT_LEFT | Geom.OUT_RIGHT)) != 0) {
          path.add(new BezierPath.Node((sp.x + ep.x) / 2, sp.y));
          path.add(new BezierPath.Node((sp.x + ep.x) / 2, ep.y));
        } else if (soutcode == Geom.OUT_BOTTOM || soutcode == Geom.OUT_TOP) {
          path.add(new BezierPath.Node(sp.x, ep.y));
        } else {
          path.add(new BezierPath.Node(ep.x, sp.y));
        }
        path.add(new BezierPath.Node(ep.x, ep.y));
      }
    }
    // Ensure all path nodes are straight
    for (BezierPath.Node node : path.nodes()) {
      node.setMask(BezierPath.C0_MASK);
    }
    path.invalidatePath();
  }

  @Override
  public Liner clone() {
    try {
      return (Liner) super.clone();
    } catch (CloneNotSupportedException ex) {
      InternalError error = new InternalError(ex.getMessage());
      error.initCause(ex);
      throw error;
    }
  }
}
