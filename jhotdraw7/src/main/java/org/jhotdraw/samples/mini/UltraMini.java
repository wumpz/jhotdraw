/*
 * @(#)UltraMini.java  1.0 January 11, 2007
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
 * UltraMini.
 *
 * @author Pondus
 * @version 1.0 January 11, 2007 Created.
 */
public class UltraMini {

        /** Creates a new instance of UltraMini */
        public UltraMini() {
            LineFigure lf = new LineFigure();
            lf.basicSetBounds(new Point2D.Double(40,40), new Point2D.Double(200,
40));

            // Add all figures to a drawing 
            Drawing drawing = new DefaultDrawing(); 

            drawing.add(lf);

            // Show the drawing 
            JFrame f = new JFrame("UltraMini"); 
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
            f.setSize(600,300); 

            DrawingView view = new DefaultDrawingView(); 
            view.setDrawing(drawing);

            f.getContentPane().add(view.getComponent()); 
            // set up the drawing editor
            DrawingEditor editor = new DefaultDrawingEditor();
            editor.add(view);
           // editor.setTool(new DelegationSelectionTool());
           // editor.setTool(new SelectionTool());
            //editor.setTool(new SelectAreaTracker());
         //   editor.setTool(new DragTracker(lf));
            
            view.selectAll();
            editor.setTool(new HandleTracker(view.findHandle(view.drawingToView(lf.getStartPoint()))));
            
            f.show(); 
        }
      public static void main(String[] args) { 
            SwingUtilities.invokeLater(new Runnable() { 
                public void run() { 
                    new UltraMini(); 
            } 
        }); 
      }    
}
