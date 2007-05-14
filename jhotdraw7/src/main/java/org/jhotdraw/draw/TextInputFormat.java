/*
 * @(#)TextInputFormat.java  1.0  2007-04-12
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

package org.jhotdraw.draw;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import org.jhotdraw.geom.Dimension2DDouble;
import org.jhotdraw.gui.datatransfer.*;
import org.jhotdraw.io.*;

/**
 * An input format for importing text into a drawing.
 * <p>
 * This class uses the prototype design pattern. A TextHolderFigure figure is used
 * as a prototype for creating a figure that holds the imported text.
 * <p>
 * For text that spans multiple lines, TextInputFormat can either add all the
 * text to the same Figure, or it can create a new Figure for each line.
 *
 * @author Werner Randelshoer 1.0 2007-04-12 Created.
 * @see org.jhotdraw.draw.TextHolderFigure
 */
public class TextInputFormat implements InputFormat {
    /**
     * The prototype for creating a figure that holds the imported text.
     */
    private TextHolderFigure prototype;
    
    /**
     * Format description used for the file filter.
     */
    private String description;
    /**
     * File name extension used for the file filter.
     */
    private String fileExtension;
    /**
     * Image IO image format name.
     */
    private String formatName;
    /**
     * This should be set to true for ImageHolderFigures that can hold multiple
     * lines of text.
     */
    private boolean isMultiline;
    
    /** Creates a new image output format for text, for a figure that can not.
     * hold multiple lines of text.
     */
    public TextInputFormat(TextHolderFigure prototype) {
        this(prototype, "Text", "Text", "txt", false);
    }
    
    /** Creates a new image output format for the specified image format.
     *
     * @param formatName The format name for the javax.imageio.ImageIO object.
     * @param description The format description to be used for the file filter.
     * @param fileExtension The file extension to be used for file filter.
     * @param isMultiline Set this to true, if the TextHolderFigure can hold
     * multiple lines of text. If this is true, multiple lines of text are
     * added to the same figure. If this is false, a new Figure is created for
     * each line of text.
     */
    public TextInputFormat(TextHolderFigure prototype, String formatName,
            String description, String fileExtension, boolean isMultiline) {
        this.prototype = prototype;
        this.formatName = formatName;
        this.description = description;
        this.fileExtension = fileExtension;
        this.isMultiline = isMultiline;
    }
    
    public javax.swing.filechooser.FileFilter getFileFilter() {
        return new ExtensionFileFilter(description, fileExtension);
    }
    
    public String getFileExtension() {
        return fileExtension;
    }
    
    public JComponent getInputFormatAccessory() {
        return null;
    }
    
    public void read(File file, Drawing drawing) throws IOException {
        read(new FileInputStream(file), drawing);
    }
    
    public void read(InputStream in, Drawing drawing) throws IOException {
        drawing.basicAddAll(0, createTextHolderFigures(in));
    }
    
    public LinkedList<Figure> createTextHolderFigures(InputStream in) throws IOException {
        LinkedList list = new LinkedList<Figure>();
        
        BufferedReader r = new BufferedReader(new InputStreamReader(in, "UTF8"));
        
        if (isMultiline) {
            TextHolderFigure figure = (TextHolderFigure) prototype.clone();
            StringBuilder buf = new StringBuilder();
            for (String line = null; line != null; line = r.readLine()) {
                if (buf.length() != 0) {
                    buf.append('\n');
                }
                buf.append(line);
            }
            figure.setText(buf.toString());
            Dimension2DDouble s = figure.getPreferredSize();
            figure.setBounds(
                    new Point2D.Double(0,0),
                    new Point2D.Double(
                    s.width, s.height
                    ));
        } else {
            double y = 0;
            for (String line = null; line != null; line = r.readLine()) {
                TextHolderFigure figure = (TextHolderFigure) prototype.clone();
                figure.setText(line);
                Dimension2DDouble s = figure.getPreferredSize();
                figure.setBounds(
                        new Point2D.Double(0,y),
                        new Point2D.Double(
                        s.width, s.height
                        ));
                list.add(figure);
                y += s.height;
            }
        }
        
        return list;
    }
    
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(DataFlavor.stringFlavor);
    }
    
    public java.util.List<Figure> readFigures(Transferable t) throws UnsupportedFlavorException, IOException {
        String text = (String) t.getTransferData(DataFlavor.stringFlavor);
        
        LinkedList list = new LinkedList<Figure>();
        if (isMultiline) {
            TextHolderFigure figure = (TextHolderFigure) prototype.clone();
            figure.setText(text);
            Dimension2DDouble s = figure.getPreferredSize();
            figure.willChange();
            figure.setBounds(
                    new Point2D.Double(0,0),
                    new Point2D.Double(
                    s.width, s.height
                    ));
            figure.changed();
            list.add(figure);
        } else {
            double y = 0;
            for (String line : text.split("\n")) {
                TextHolderFigure figure = (TextHolderFigure) prototype.clone();
                figure.setText(line);
                Dimension2DDouble s = figure.getPreferredSize();
                y += s.height;
                figure.willChange();
                figure.setBounds(
                        new Point2D.Double(0,0+y),
                        new Point2D.Double(
                        s.width, s.height+y
                        ));
                figure.changed();
                list.add(figure);
            }
        }
        return list;
    }
}
