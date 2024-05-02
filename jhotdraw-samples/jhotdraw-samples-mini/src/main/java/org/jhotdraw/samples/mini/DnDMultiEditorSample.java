/*
 * @(#)MultiEditorSample.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.mini;

import java.awt.*;
import javax.swing.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.figure.RectangleFigure;
import org.jhotdraw.draw.tool.CreationTool;
import org.jhotdraw.draw.tool.DnDTracker;
import org.jhotdraw.draw.tool.SelectionTool;
import org.jhotdraw.editor.DefaultDrawingEditor;
import org.jhotdraw.gui.action.ButtonFactory;
import org.jhotdraw.io.ImageOutputFormat;
import org.jhotdraw.io.SerializationInputFormat;
import org.jhotdraw.io.SerializationOutputFormat;
import org.jhotdraw.util.*;

/** Example showing how to create a drawing editor which acts on four drawing views. */
public class DnDMultiEditorSample {

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
        // Create four drawing views, each one with its own drawing
        DrawingView view1 = new DefaultDrawingView();
        DrawingView view2 = new DefaultDrawingView();
        DrawingView view3 = new DefaultDrawingView();
        DrawingView view4 = new DefaultDrawingView();
        view1.setDrawing(createDrawing());
        view2.setDrawing(createDrawing());
        view3.setDrawing(createDrawing());
        view4.setDrawing(createDrawing());
        // Create a common drawing editor for the views
        DrawingEditor editor = new DefaultDrawingEditor();
        editor.add(view1);
        editor.add(view2);
        editor.add(view3);
        editor.add(view4);
        // Create a tool bar with selection tool and a
        // creation tool for rectangle figures.
        JToolBar tb = new JToolBar();
        SelectionTool selectionTool = new SelectionTool();
        selectionTool.setDragTracker(new DnDTracker());
        ButtonFactory.addSelectionToolTo(tb, editor, selectionTool);
        ButtonFactory.addToolTo(
            tb, editor, new CreationTool(new RectangleFigure()), "edit.createRectangle", labels);
        tb.setOrientation(JToolBar.VERTICAL);
        // Put all together into a JFrame
        JFrame f = new JFrame("Multi-Editor");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(400, 300);
        // Set up the content pane
        // Place the toolbar on the left
        // Place each drawing view into a scroll pane of its own
        // and put them into a larger scroll pane.
        JPanel innerPane = new JPanel();
        innerPane.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        JScrollPane sp;
        innerPane.add(sp = new JScrollPane(view1.getComponent()));
        sp.setPreferredSize(new Dimension(200, 200));
        innerPane.add(sp = new JScrollPane(view2.getComponent()));
        sp.setPreferredSize(new Dimension(200, 200));
        innerPane.add(sp = new JScrollPane(view3.getComponent()));
        sp.setPreferredSize(new Dimension(200, 200));
        innerPane.add(sp = new JScrollPane(view4.getComponent()));
        sp.setPreferredSize(new Dimension(200, 200));
        f.getContentPane().add(new JScrollPane(innerPane));
        f.getContentPane().add(tb, BorderLayout.WEST);
        f.setVisible(true);
      }
    });
  }

  /**
   * Creates a drawing with input and output formats, so that drawing figures can be copied and
   * pasted between drawing views.
   *
   * @return a drawing
   */
  private static Drawing createDrawing() {
    // Create a default drawing with
    // input/output formats for basic clipboard support.
    DefaultDrawing drawing = new DefaultDrawing();
    drawing.addInputFormat(new SerializationInputFormat());
    drawing.addOutputFormat(new SerializationOutputFormat());
    drawing.addOutputFormat(new ImageOutputFormat());
    return drawing;
  }
}
