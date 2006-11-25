/*
 * @(#)ConnectingFiguresSample.java   1.0  November 16, 2006
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
import javax.swing.*;
import org.jhotdraw.application.DrawApplication;
import org.jhotdraw.contrib.*;
import org.jhotdraw.figures.*;
import org.jhotdraw.framework.*;
import org.jhotdraw.standard.*;
import org.jhotdraw.util.*;

/**
 * Example showing how to connect two text areas with an elbow connection.
 * This example uses JHotDraw 6.
 *
 * @author Werner Randelshofer
 * @version 1.0 November 16, 2006 Created.
 */
public class ConnectingFiguresSample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                
                // Create the two text areas
                TextAreaFigure ta = new TextAreaFigure();
                ta.displayBox(new Point(10,10),new Point(100,100));
                
                TextAreaFigure tb = new TextAreaFigure();
                tb.displayBox(new Point(210,110),new Point(300,200));
                
                // Create an elbow connection
                ConnectionFigure cf = new ElbowConnection();
                
                // Connect the figures
                Point startPoint = Geom.center(ta.displayBox());
                cf.startPoint(startPoint.x, startPoint.y);
                cf.connectStart(ta.connectorAt(startPoint.x, startPoint.y));
                Point endPoint = Geom.center(tb.displayBox());
                cf.endPoint(endPoint.x, endPoint.y);
                cf.connectEnd(tb.connectorAt(endPoint.x, endPoint.y));
                cf.updateConnection();
                
                // Add all figures to a drawing
                Drawing drawing = new StandardDrawing();
                drawing.add(ta);
                drawing.add(tb);
                drawing.add(cf);
                
                // Show the drawing
                JFrame f = new JFrame("My Drawing");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setSize(400,300);
                
                DrawApplication editor = new DrawApplication("Connecting Figures Sample");
                editor.open();
                DrawingView view = editor.view();
                view.setDrawing(drawing);
            }
        });
    }
}
