/*
 * Gingko.Systeme
 * (c) 2013
 */
package org.jhotdraw.draw.event;

import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.tool.Tool;

/**
 * Event wird gefeuert, wenn ein Element erstellt über JHotDraw wurde.
 *
 * @author fg
 */
public class FigureCreatedEvent extends ToolEvent {

  final Figure figure;

  public FigureCreatedEvent(Tool src, DrawingView view, Figure figure) {
    super(src, view, null);
    this.figure = figure;
  }

  public Figure getFigure() {
    return figure;
  }
}
