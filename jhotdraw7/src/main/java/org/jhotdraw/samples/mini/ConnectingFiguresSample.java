/*
 * @(#)ConnectingFiguresSample.java   1.0  November 9, 2006
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
 * Example showing how to connect two text areas with an elbow connection.
 *
 * @author Werner Randelshofer
 * @version 1.0 November 9, 2006 Created.
 */
public class ConnectingFiguresSample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                
                // Create the two text areas
                TextAreaFigure ta = new TextAreaFigure();
                ta.basicSetBounds(new Point2D.Double(10,10),new Point2D.Double(100,100));
                
                TextAreaFigure tb = new TextAreaFigure();
                tb.basicSetBounds(new Point2D.Double(210,110),new Point2D.Double(300,200));
                
                // Create an elbow connection
                ConnectionFigure cf = new LineConnectionFigure();
                cf.setLiner(new ElbowLiner());
                
                // Connect the figures
                cf.setStartConnector(ta.findConnector(Geom.center(ta.getBounds()), cf));
                cf.setEndConnector(tb.findConnector(Geom.center(tb.getBounds()), cf));
                
                // Add all figures to a drawing
                Drawing drawing = new DefaultDrawing();
                drawing.add(ta);
                drawing.add(tb);
                drawing.add(cf);
                
                // Show the drawing
                JFrame f = new JFrame("My Drawing");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setSize(400,300);
                
                DrawingView view = new DefaultDrawingView();
                view.setDrawing(drawing);
                f.getContentPane().add(view.getComponent());
                
                f.show();
            }
        });
    }
}
