/*
 * @(#)ImageTool.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.draw.tool;

import org.jhotdraw.draw.*;
import org.jhotdraw.draw.ImageHolderFigure;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import javax.annotation.Nullable;
import org.jhotdraw.gui.BackgroundTask;
import org.jhotdraw.gui.Worker;

/**
 * A tool to create new figures that implement the ImageHolderFigure
 * interface, such as ImageFigure. The figure to be created is specified by a
 * prototype.
 * <p>
 * Immediately, after the ImageTool has been activated, it opens a JFileChooser,
 * letting the user specify an image file. The the user then performs 
 * the following mouse gesture:
 * <ol>
 * <li>Press the mouse button and drag the mouse over the DrawingView. 
 * This defines the bounds of the created figure.</li>
 * </ol>
 *
 * <hr>
 * <b>Design Patterns</b>
 *
 * <p><em>Prototype</em><br>
 * The {@code ImageTool} creates new figures by cloning a prototype
 * {@code ImageHolderFigure} object.<br>
 * Prototype: {@link ImageHolderFigure}; Client: {@link ImageTool}.
 * <hr>
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ImageTool extends CreationTool {
    private static final long serialVersionUID = 1L;

    @Nullable protected FileDialog fileDialog;
    @Nullable protected JFileChooser fileChooser;
    protected boolean useFileDialog;
    protected Thread workerThread;

    /** Creates a new instance. */
    public ImageTool(ImageHolderFigure prototype) {
        super(prototype);
    }

    /** Creates a new instance. */
    public ImageTool(ImageHolderFigure prototype, Map<AttributeKey<?>, Object> attributes) {
        super(prototype, attributes);
    }

    public void setUseFileDialog(boolean newValue) {
        useFileDialog = newValue;
        if (useFileDialog) {
            fileChooser = null;
        } else {
            fileDialog = null;
        }
    }

    public boolean isUseFileDialog() {
        return useFileDialog;
    }

    @Override
    public void activate(DrawingEditor editor) {
        super.activate(editor);
        final DrawingView v=getView();
        if (v==null)return;

        if (workerThread != null) {
            try {
                workerThread.join();
            } catch (InterruptedException ex) {
                // ignore
            }
        }

        final File file;
        if (useFileDialog) {
            getFileDialog().setVisible(true);
            if (getFileDialog().getFile() != null) {
                file = new File(getFileDialog().getDirectory(), getFileDialog().getFile());
            } else {
                file = null;
            }
        } else {
            if (getFileChooser().showOpenDialog(v.getComponent()) == JFileChooser.APPROVE_OPTION) {
                file = getFileChooser().getSelectedFile();
            } else {
                file = null;
            }
        }

        if (file != null) {
            final ImageHolderFigure loaderFigure = ((ImageHolderFigure) prototype.clone());
            BackgroundTask worker = new BackgroundTask() {

                @Override
                protected void construct() throws IOException {
                    loaderFigure.loadImage(file);
                }

                @Override
                protected void done() {
                    try {
                        if (createdFigure == null) {
                            ((ImageHolderFigure) prototype).setImage(loaderFigure.getImageData(), loaderFigure.getBufferedImage());
                        } else {
                            ((ImageHolderFigure) createdFigure).setImage(loaderFigure.getImageData(), loaderFigure.getBufferedImage());
                        }
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(v.getComponent(),
                                ex.getMessage(),
                                null,
                                JOptionPane.ERROR_MESSAGE);
                    }
                }

                @Override
                protected void failed(Throwable value) {
                    Throwable t = value;
                    JOptionPane.showMessageDialog(v.getComponent(),
                            t.getMessage(),
                            null,
                            JOptionPane.ERROR_MESSAGE);
                    getDrawing().remove(createdFigure);
                    fireToolDone();
                }
            };
            workerThread = new Thread(worker);
            workerThread.start();
        } else {
            //getDrawing().remove(createdFigure);
            if (isToolDoneAfterCreation()) {
                fireToolDone();
            }
        }
    }

    private JFileChooser getFileChooser() {
        if (fileChooser == null) {
            fileChooser = new JFileChooser();
        }
        return fileChooser;
    }

    private FileDialog getFileDialog() {
        if (fileDialog == null) {
            fileDialog = new FileDialog(new Frame());
        }
        return fileDialog;
    }
}
