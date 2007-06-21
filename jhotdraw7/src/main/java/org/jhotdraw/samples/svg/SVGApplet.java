/*
 * @(#)SVGApplet.java  1.0  2006-07-08
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */

package org.jhotdraw.samples.svg;

import org.jhotdraw.draw.*;
import org.jhotdraw.draw.action.*;
import org.jhotdraw.gui.*;
import org.jhotdraw.samples.svg.figures.*;
import org.jhotdraw.samples.svg.io.*;
import org.jhotdraw.util.*;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import org.jhotdraw.xml.*;
/**
 * SVGApplet.
 *
 * @author  Werner Randelshofer
 * @version 1.0 2006-07-08 Created.
 */
public class SVGApplet extends JApplet {
    private static String version;
    private final static String NAME = "JHotDraw SVG";
    private SVGDrawingPanel drawingPanel;
    
    /**
     * We override getParameter() to make it work even if we have no Applet
     * context.
     */
    public String getParameter(String name) {
        try {
            return super.getParameter(name);
        } catch (NullPointerException e) {
            return null;
        }
    }
    protected String getVersion() {
        if (version == null) {
            BufferedReader r = null;
            try {
                r = new BufferedReader(
                        new InputStreamReader(
                        getClass().getResourceAsStream("version.txt"), "UTF-8"
                        )
                        );
                version = r.readLine();
            } catch (Throwable e) {
                version = "unknown";
            } finally {
                if (r != null) try {
                    r.close();
                } catch (IOException e) {}
            }
        }
        return version;
    }
    
    /** Initializes the applet SVGApplet */
    public void init() {
        // Set look and feel
        // -----------------
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Throwable e) {
            // Do nothing.
            // If we can't set the desired look and feel, UIManager does
            // automaticaly the right thing for us.
        }
        
        // Display copyright info while we are loading the data
        // ----------------------------------------------------
        Container c = getContentPane();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        String[] labels = getAppletInfo().split("\n");//Strings.split(getAppletInfo(), '\n');
        for (int i=0; i < labels.length; i++) {
            c.add(new JLabel((labels[i].length() == 0) ? " " : labels[i]));
        }
        
        // We load the data using a worker thread
        // --------------------------------------
        new Worker() {
            public Object construct() {
                Object result = null;
                
                InputStream in = null;
                try {
                    if (getParameter("data") != null) {
                        in = new ByteArrayInputStream(
                                getParameter("data").getBytes("UTF8")
                                );
                    } else if (getParameter("datafile") != null) {
                        URL url = new URL(getDocumentBase(), getParameter("datafile"));
                        in = url.openConnection().getInputStream();
                    }
                    if (in != null) {
                        Drawing drawing = createDrawing();
                        drawing.getInputFormats().get(0).read(in, drawing);
                        result = drawing;
                    }
                } catch (Throwable t) {
                    result = t;
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException ex) {
                            // ignore
                        }
                    }
                }
                
                return result;
            }
            public void finished(Object result) {
                if (result instanceof Throwable) {
                    ((Throwable) result).printStackTrace();
                }
                Container c = getContentPane();
                c.setLayout(new BorderLayout());
                c.removeAll();
                c.add(drawingPanel = new SVGDrawingPanel());
                
                initComponents();
                if (result != null) {
                    if (result instanceof Drawing) {
                        setDrawing((Drawing) result);
                    } else if (result instanceof Throwable) {
                        getDrawing().add(new SVGTextFigure(result.toString()));
                        ((Throwable) result).printStackTrace();
                    }
                }
                
                c.validate();
            }
        }.start();
    }
    
    
    private void setDrawing(Drawing d) {
        drawingPanel.setDrawing(d);
    }
    private Drawing getDrawing() {
        return drawingPanel.getDrawing();
    }
    
    private Drawing createDrawing() {
        DefaultDrawing drawing = new DefaultDrawing();
        LinkedList<InputFormat> inputFormats = new LinkedList<InputFormat>();
        inputFormats.add(new SVGInputFormat());
        inputFormats.add(new ImageInputFormat(new SVGImageFigure()));
        inputFormats.add(new TextInputFormat(new SVGTextFigure()));
        LinkedList<OutputFormat> outputFormats = new LinkedList<OutputFormat>();
        outputFormats.add(new SVGOutputFormat());
        outputFormats.add(new ImageOutputFormat());
        drawing.setInputFormats(inputFormats);
        drawing.setOutputFormats(outputFormats);
        return drawing;
    }
    
    
    public void setData(String text) {
        if (text != null && text.length() > 0) {
            InputStream in = null;
            try {
                in = new ByteArrayInputStream(text.getBytes("UTF8"));
                Drawing drawing = createDrawing();
                drawing.getInputFormats().get(0).read(in, drawing);
                setDrawing(drawing);
            } catch (Throwable e) {
                getDrawing().clear();
                SVGTextFigure tf = new SVGTextFigure();
                tf.setText(e.getMessage());
                tf.setBounds(new Point2D.Double(10,10), new Point2D.Double(100,100));
                getDrawing().add(tf);
                e.printStackTrace();
            } finally {
                if (in != null)  {
                    try {
                        in.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
    public String getData() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            getDrawing().getOutputFormats().get(0).write(out, getDrawing());
            return out.toString("UTF8");
        } catch (IOException e) {
            SVGTextFigure tf = new SVGTextFigure();
            tf.setText(e.getMessage());
            tf.setBounds(new Point2D.Double(10,10), new Point2D.Double(100,100));
            getDrawing().add(tf);
            e.printStackTrace();
            return "";
        }
    }
    
    public String[][] getParameterInfo() {
        return new String[][] {
            { "data", "String", "the data to be displayed by this applet." },
            { "datafile", "URL", "an URL to a file containing the data to be displayed by this applet." },
        };
    }
    public String getAppletInfo() {
        return NAME +
                "\nVersion "+getVersion() +
                "\n\nCopyright 1996-2007 (c) by the authors of JHotDraw" +
                "\nThis software is licensed under LGPL or" +
                "\nCreative Commons 2.5 BY";
    }
    /** This method is called from within the init() method to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

    }// </editor-fold>//GEN-END:initComponents
    
    public static void mainx(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame f = new JFrame("SVGDraw Applet");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                SVGApplet a = new SVGApplet();
                f.getContentPane().add(a);
                a.init();
                f.setSize(500,300);
                f.setVisible(true);
                a.start();
            }
        });
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
}
