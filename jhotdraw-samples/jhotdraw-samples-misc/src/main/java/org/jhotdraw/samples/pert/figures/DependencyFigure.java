/*
 * @(#)DependencyFigure.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.pert.figures;

import static org.jhotdraw.draw.AttributeKeys.*;

import java.awt.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.decoration.ArrowTip;
import org.jhotdraw.draw.figure.LineConnectionFigure;

/**
 * DependencyFigure.
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class DependencyFigure extends LineConnectionFigure {

  private static final long serialVersionUID = 1L;

  public DependencyFigure() {
    attr().set(STROKE_COLOR, new Color(0x000099));
    attr().set(STROKE_WIDTH, 1d);
    attr().set(END_DECORATION, new ArrowTip());
    attr().setAttributeEnabled(END_DECORATION, false);
    attr().setAttributeEnabled(START_DECORATION, false);
    attr().setAttributeEnabled(STROKE_DASHES, false);
    attr().setAttributeEnabled(FONT_ITALIC, false);
    attr().setAttributeEnabled(FONT_UNDERLINE, false);
  }

  /**
   * Checks if two figures can be connected. Implement this method to constrain the allowed
   * connections between figures.
   */
  @Override
  public boolean canConnect(Connector start, Connector end) {
    if ((start.getOwner() instanceof TaskFigure) && (end.getOwner() instanceof TaskFigure)) {
      TaskFigure sf = (TaskFigure) start.getOwner();
      TaskFigure ef = (TaskFigure) end.getOwner();
      // Disallow multiple connections to same dependent
      if (ef.getPredecessors().contains(sf)) {
        return false;
      }
      // Disallow cyclic connections
      return !sf.isDependentOf(ef);
    }
    return false;
  }

  @Override
  public boolean canConnect(Connector start) {
    return (start.getOwner() instanceof TaskFigure);
  }

  /** Handles the disconnection of a connection. Override this method to handle this event. */
  @Override
  protected void handleDisconnect(Connector start, Connector end) {
    TaskFigure sf = (TaskFigure) start.getOwner();
    TaskFigure ef = (TaskFigure) end.getOwner();
    sf.removeDependency(this);
    ef.removeDependency(this);
  }

  /** Handles the connection of a connection. Override this method to handle this event. */
  @Override
  protected void handleConnect(Connector start, Connector end) {
    TaskFigure sf = (TaskFigure) start.getOwner();
    TaskFigure ef = (TaskFigure) end.getOwner();
    sf.addDependency(this);
    ef.addDependency(this);
  }

  @Override
  public DependencyFigure clone() {
    DependencyFigure that = (DependencyFigure) super.clone();
    return that;
  }

  @Override
  public int getLayer() {
    return 1;
  }

  @Override
  public void removeNotify(Drawing d) {
    if (getStartFigure() != null) {
      ((TaskFigure) getStartFigure()).removeDependency(this);
    }
    if (getEndFigure() != null) {
      ((TaskFigure) getEndFigure()).removeDependency(this);
    }
    super.removeNotify(d);
  }
}
