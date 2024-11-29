/*
 * @(#)HandleMulticaster.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.event;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.utils.util.ReversedList;

public class HandleMulticaster {

  private List<Handle> handles;

  public HandleMulticaster(Handle handle) {
    this.handles = new ArrayList<>();
    this.handles.add(handle);
  }

  public HandleMulticaster(Collection<Handle> handles) {
    this.handles = new ArrayList<>(handles);
  }

  public void draw(java.awt.Graphics2D g) {
    for (Handle h : handles) {
      h.draw(g);
    }
  }

  public void keyPressed(java.awt.event.KeyEvent e) {
    for (Handle h : handles) {
      h.keyPressed(e);
      if (e.isConsumed()) {
        break;
      }
    }
  }

  public void keyReleased(java.awt.event.KeyEvent e) {
    for (Handle h : handles) {
      h.keyReleased(e);
    }
  }

  public void keyTyped(java.awt.event.KeyEvent e) {
    for (Handle h : handles) {
      h.keyTyped(e);
    }
  }

  public void trackEnd(Point current, Point anchor, int modifiersEx, DrawingView view) {
    for (Handle h : new ReversedList<>(handles)) {
      h.trackEnd(current, anchor, modifiersEx);
    }
  }

  public void trackStart(Point anchor, int modifiersEx, DrawingView view) {
    for (Handle h : handles) {
      h.trackStart(anchor, modifiersEx);
    }
  }

  public void trackDoubleClick(Point p, int modifiersEx, DrawingView view) {
    for (Handle h : handles) {
      h.trackDoubleClick(p, modifiersEx);
    }
  }

  public void trackStep(Point anchor, Point lead, int modifiersEx, DrawingView view) {
    for (Handle h : handles) {
      h.trackStep(anchor, lead, modifiersEx);
    }
  }
}
