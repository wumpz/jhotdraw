/*
 * @(#)ImageTool.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.tool;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.figure.ImageHolderFigure;

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
 * <p>
 * <em>Prototype</em><br>
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
    protected FileDialog fileDialog;
    protected JFileChooser fileChooser;
    protected boolean useFileDialog;

    /**
     * Creates a new instance.
     */
    public ImageTool(ImageHolderFigure prototype) {
        super(prototype);
    }

    /**
     * Creates a new instance.
     */
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
        final DrawingView v = getView();
        if (v == null) {
            return;
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
            new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    loaderFigure.loadImage(file);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();  //will throw an ExecutionException if in doInBackground something went wrong.
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
                    } catch (InterruptedException | ExecutionException ex) {
                        JOptionPane.showMessageDialog(v.getComponent(),
                            ex.getMessage(),
                            null,
                            JOptionPane.ERROR_MESSAGE);
                        getDrawing().remove(createdFigure);
                        fireToolDone();
                    }
                }
            }.execute();
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
