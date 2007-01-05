/*
 * @(#)ConnectingFiguresSample_1.java   1.0  November 9, 2006
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

import java.awt.geom.*;
import javax.swing.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.geom.*;

/**
 * Example showing how to create a drawing and displaying it in an editor 
 * with a "delegation selection tool".
 * We don't make use of a toolbar, so users are not able to add new figure
 * to the drawing, they can just play around with the figures that are there.
 *
 * @author Werner Randelshofer
 * @version 1.0 November 9, 2006 Created.
 */
public class EditorSample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                
                // Create a simple drawing consisting of three
                // text areas and an elbow connection.
                TextAreaFigure ta = new TextAreaFigure();
                ta.basicSetBounds(new Point2D.Double(10,10),new Point2D.Double(100,100));
                TextAreaFigure tb = new TextAreaFigure();
                tb.basicSetBounds(new Point2D.Double(220,120),new Point2D.Double(310,210));
                TextAreaFigure tc = new TextAreaFigure();
                tc.basicSetBounds(new Point2D.Double(220,10),new Point2D.Double(310,100));
                ConnectionFigure cf = new LineConnectionFigure();
                cf.setLiner(new ElbowLiner());
                cf.setStartConnector(ta.findConnector(Geom.center(ta.getBounds()), cf));
                cf.setEndConnector(tb.findConnector(Geom.center(tb.getBounds()), cf));
                Drawing drawing = new DefaultDrawing();
                drawing.add(ta);
                drawing.add(tb);
                drawing.add(tc);
                drawing.add(cf);
                
                // Show the drawing
                JFrame f = new JFrame("My Drawing");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setSize(400,300);
                
                // Set up the drawing view
                DrawingView view = new DefaultDrawingView();
                view.setDrawing(drawing);
                f.getContentPane().add(view.getComponent());
                
                // Set up the drawing editor
                DrawingEditor editor = new DefaultDrawingEditor();
                editor.add(view);
                editor.setTool(new DelegationSelectionTool());
                
                f.show();
            }
        });
    }
}
