/*
 * @(#)LayouterSample.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.mini;

import javax.swing.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.figure.GraphicalCompositeFigure;
import org.jhotdraw.draw.figure.LineFigure;
import org.jhotdraw.draw.figure.TextFigure;
import org.jhotdraw.draw.layouter.VerticalLayouter;
import org.jhotdraw.draw.tool.DelegationSelectionTool;
import org.jhotdraw.editor.DefaultDrawingEditor;

/**
 * Example showing how to layout two editable text figures and a line figure within a graphical
 * composite figure.
 */
public class LayouterSample {

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        // Create a graphical composite figure.
        GraphicalCompositeFigure composite = new GraphicalCompositeFigure();
        // Add child figures to the composite figure
        composite.add(new TextFigure("Above the line"));
        composite.add(new LineFigure());
        composite.add(new TextFigure("Below the line"));
        // Set a layouter and perform the layout
        composite.setLayouter(new VerticalLayouter());
        composite.layout(1.0);
        // Add the composite figure to a drawing
        Drawing drawing = new DefaultDrawing();
        drawing.add(composite);
        // Create a frame with a drawing view and a drawing editor
        JFrame f = new JFrame("My Drawing");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(400, 300);
        DrawingView view = new DefaultDrawingView();
        view.setDrawing(drawing);
        f.getContentPane().add(view.getComponent());
        DrawingEditor editor = new DefaultDrawingEditor();
        editor.add(view);
        editor.setTool(new DelegationSelectionTool());
        f.setVisible(true);
      }
    });
  }
}
