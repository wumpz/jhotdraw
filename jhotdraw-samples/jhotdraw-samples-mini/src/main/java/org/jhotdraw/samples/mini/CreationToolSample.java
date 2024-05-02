/*
 * @(#)CreationToolSample.java
 *
 * Copyright (c) 2009-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.mini;

import static org.jhotdraw.draw.AttributeKeys.*;

import java.awt.*;
import java.util.HashMap;
import javax.swing.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.figure.RectangleFigure;
import org.jhotdraw.draw.tool.CreationTool;
import org.jhotdraw.editor.DefaultDrawingEditor;
import org.jhotdraw.gui.action.ButtonFactory;
import org.jhotdraw.io.SerializationInputFormat;
import org.jhotdraw.io.SerializationOutputFormat;
import org.jhotdraw.util.*;

/**
 * Example showing how to create a drawing editor with a creation tool for figures with pre-defined
 * attribute values: the example editor creates green rectangles.
 */
public class CreationToolSample {

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
        // Create a drawing view with a default drawing, and
        // input/output formats for basic clipboard support.
        DrawingView view = new DefaultDrawingView();
        DefaultDrawing drawing = new DefaultDrawing();
        drawing.addInputFormat(new SerializationInputFormat());
        drawing.addOutputFormat(new SerializationOutputFormat());
        view.setDrawing(drawing);
        // Create a common drawing editor for the views
        DrawingEditor editor = new DefaultDrawingEditor();
        editor.add(view);
        // Create a tool bar
        JToolBar tb = new JToolBar();
        // Add a selection tool to the toolbar.
        ButtonFactory.addSelectionToolTo(tb, editor);
        // Add a creation tool for green rectangles to the toolbar.
        HashMap<AttributeKey<?>, Object> a = new HashMap<AttributeKey<?>, Object>();
        FILL_COLOR.put(a, Color.GREEN);
        ButtonFactory.addToolTo(
            tb, editor, new CreationTool(new RectangleFigure(), a), "edit.createRectangle", labels);
        tb.setOrientation(JToolBar.VERTICAL);
        // Put all together into a JFrame
        JFrame f = new JFrame("Editor with Creation Tool");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(400, 300);
        // Set up the content pane
        // Place the toolbar on the left
        f.getContentPane().add(tb, BorderLayout.WEST);
        // Place the drawing view inside a scroll pane in the center
        JScrollPane sp = new JScrollPane(view.getComponent());
        sp.setPreferredSize(new Dimension(200, 200));
        f.getContentPane().add(sp, BorderLayout.CENTER);
        f.setVisible(true);
      }
    });
  }
}
