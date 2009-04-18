/*
 * @(#)CreationToolSample.java   1.0  2009-04-18
 *
 * Copyright (c) 2009 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */
package org.jhotdraw.samples.mini;

import java.awt.*;
import javax.swing.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.action.*;
import org.jhotdraw.util.*;

/**
 * Example showing how to create a drawing editor with a creation tool for
 * rectangle figures.
 *
 * @author Werner Randelshofer
 * @version 1.0 November 9, 2006 Created.
 */
public class CreationToolSample {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");

                // Create a drawing view with a default drawing.
                DrawingView view = new DefaultDrawingView();
                view.setDrawing(new DefaultDrawing());

                // Create a common drawing editor for the views
                DrawingEditor editor = new DefaultDrawingEditor();
                editor.add(view);

                // Create a tool bar with selection tool and a
                // creation tool for rectangle figures.
                JToolBar tb = new JToolBar();
                ButtonFactory.addSelectionToolTo(tb, editor);
                ButtonFactory.addToolTo(
                        tb, editor,
                        new CreationTool(new RectangleFigure()),
                        "edit.createRectangle",
                        labels);
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
