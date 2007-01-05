/*
 * @(#)LabeledConnectionSample.java  1.0  November 9, 2006
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
 * Example showing how to connect two rectangles with a labeled connection,
 * that has a labels at both ends.
 *
 * @author Werner Randelshofer
 * @version 1.0 November 9, 2006 Created.
 */
public class LabeledLineConnectionFigureSample {
    
    /** Creates a new instance. */
    public LabeledLineConnectionFigureSample() {
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Create the two rectangle figures
                RectangleFigure ta = new RectangleFigure();
                ta.basicSetBounds(new Point2D.Double(10,10),new Point2D.Double(100,100));
                
                RectangleFigure tb = new RectangleFigure();
                tb.basicSetBounds(new Point2D.Double(210,110),new Point2D.Double(300,200));
                
                // Create a labeled line connection
                LabeledLineConnectionFigure cf = new LabeledLineConnectionFigure();
                cf.setLiner(new ElbowLiner());
                cf.setLayouter(new LocatorLayouter());
                
                // Create labels and add them to both ends of the labeled line connection
                TextFigure startLabel = new TextFigure();
                startLabel.setAttribute(LocatorLayouter.LAYOUT_LOCATOR, new BezierLabelLocator(0, -Math.PI / 4, 8));
                startLabel.setEditable(false);
                startLabel.setText("start");
                cf.add(startLabel);
                
                TextFigure endLabel = new TextFigure();
                endLabel.setAttribute(LocatorLayouter.LAYOUT_LOCATOR, new BezierLabelLocator(1, Math.PI + Math.PI / 4, 8));
                endLabel.setEditable(false);
                endLabel.setText("end");
                cf.add(endLabel);
                
                
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
