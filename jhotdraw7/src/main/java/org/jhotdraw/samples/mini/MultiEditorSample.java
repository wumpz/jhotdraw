/*
 * @(#)MultiEditorSample.java   1.0  November 9, 2006
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */
package org.jhotdraw.samples.mini;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.action.*;
import org.jhotdraw.geom.*;
import org.jhotdraw.util.*;

/**
 * Example showing how to create a drawing and displaying it in an editor 
 * with a "delegation selection tool".
 * We don't make use of a toolbar, so users are not able to add new figure
 * to the drawing, they can just play around with the figures that are there.
 *
 * @author Werner Randelshofer
 * @version 1.0 November 9, 2006 Created.
 */
public class MultiEditorSample {
     public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ResourceBundleUtil labels =  ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
                
                // Create four drawing views, each one with its own drawing
                DrawingView view1 = new DefaultDrawingView();
                view1.setDrawing(new DefaultDrawing());
                DrawingView view2 = new DefaultDrawingView();
                view2.setDrawing(new DefaultDrawing());
                DrawingView view3 = new DefaultDrawingView();
                view3.setDrawing(new DefaultDrawing());
                DrawingView view4 = new DefaultDrawingView();
                view4.setDrawing(new DefaultDrawing());
                
                // Create a common drawing editor for the views
                DrawingEditor editor = new DefaultDrawingEditor();
                editor.add(view1);
                editor.add(view2);
                editor.add(view3);
                editor.add(view4);

                // Create a tool bar with selection tool and a
                // creation tool for rectangle figures.
                JToolBar tb = new JToolBar();
                ButtonFactory.addSelectionToolTo(tb, editor);
                ButtonFactory.addToolTo(
                        tb, editor, 
                        new CreationTool(new RectangleFigure()),
                        "createRectangle",
                        labels
                        );
                tb.setOrientation(JToolBar.VERTICAL);
                
                // Put all together into a JFrame
                JFrame f = new JFrame("Multi-Editor");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setSize(400,300);
                
                // Set up the content pane
                // Place the toolbar on the left
                // Place each drawing view into a scroll pane of its own
                // and put them into a larger scroll pane.
                JPanel innerPane = new JPanel();
                innerPane.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
                JScrollPane sp;
                innerPane.add(sp = new JScrollPane(view1.getComponent()));
                sp.setPreferredSize(new Dimension(200,200));
                innerPane.add(sp = new JScrollPane(view2.getComponent()));
                sp.setPreferredSize(new Dimension(200,200));
                innerPane.add(sp = new JScrollPane(view3.getComponent()));
                sp.setPreferredSize(new Dimension(200,200));
                innerPane.add(sp = new JScrollPane(view4.getComponent()));
                sp.setPreferredSize(new Dimension(200,200));
                f.getContentPane().add(new JScrollPane(innerPane));
                
                f.getContentPane().add(tb, BorderLayout.WEST);

                f.show();
            }
        });
    }
}
