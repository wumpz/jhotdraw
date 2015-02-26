/*
 * @(#)ImageTool.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.svg;

import javax.annotation.Nullable;
import org.jhotdraw.draw.tool.CreationTool;
import org.jhotdraw.draw.io.InputFormat;
import org.jhotdraw.draw.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import org.jhotdraw.gui.BackgroundTask;
import org.jhotdraw.gui.Worker;
import org.jhotdraw.samples.svg.io.SVGInputFormat;
import org.jhotdraw.samples.svg.io.SVGZInputFormat;

/**
 * A tool to create new figures from an input file. If the file holds a bitmap
 * image, this tool creates a SVGImageFigure. If the file holds a SVG or a SVGZ
 * image, ths tool creates a SVGGroupFigure. <p> Immediately, after the
 * ImageTool has been activated, it opens a JFileChooser, letting the user
 * specify a file. The the user then performs the following mouse gesture: <ol>
 * <li>Press the mouse button and drag the mouse over the DrawingView. This
 * defines the bounds of the created Figure.</li> </ol>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SVGCreateFromFileTool extends CreationTool {

    private static final long serialVersionUID = 1L;
    @Nullable
    protected FileDialog fileDialog;
    @Nullable
    protected JFileChooser fileChooser;
    protected Thread workerThread;
    protected CompositeFigure groupPrototype;
    protected ImageHolderFigure imagePrototype;
    protected boolean useFileDialog;

    /**
     * Creates a new instance.
     */
    public SVGCreateFromFileTool(ImageHolderFigure imagePrototype, CompositeFigure groupPrototype) {
        super(imagePrototype);
        this.groupPrototype = groupPrototype;
        this.imagePrototype = imagePrototype;
    }

    /**
     * Creates a new instance.
     */
    public SVGCreateFromFileTool(ImageHolderFigure imagePrototype, CompositeFigure groupPrototype, Map<AttributeKey<?>, Object> attributes) {
        super(imagePrototype, attributes);
        this.groupPrototype = groupPrototype;
        this.imagePrototype = imagePrototype;
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

            if (file.getName().toLowerCase().endsWith(".svg")
                    || file.getName().toLowerCase().endsWith(".svgz")) {
                prototype = groupPrototype.clone();
                Worker<Drawing> worker;
                worker = new Worker<Drawing>() {

                    @Override
                    public Drawing construct() throws IOException {
                        Drawing drawing = new DefaultDrawing();
                        InputFormat in = (file.getName().toLowerCase().endsWith(".svg")) ? new SVGInputFormat() : new SVGZInputFormat();
                        in.read(file.toURI(), drawing);
                        return drawing;
                    }

                    @Override
                    protected void done(Drawing drawing) {
                        CompositeFigure parent;
                        if (createdFigure == null) {
                            parent = (CompositeFigure) prototype;
                            for (Figure f : drawing.getChildren()) {
                                parent.basicAdd(f);
                            }
                        } else {
                            parent = (CompositeFigure) createdFigure;
                            parent.willChange();
                            for (Figure f : drawing.getChildren()) {
                                parent.add(f);
                            }
                            parent.changed();
                        }
                    }

                    @Override
                    protected void failed(Throwable t) {
                        JOptionPane.showMessageDialog(v.getComponent(),
                                t.getMessage(),
                                null,
                                JOptionPane.ERROR_MESSAGE);
                        getDrawing().remove(createdFigure);
                        fireToolDone();
                    }

                    @Override
                    protected void finished() {
                    }
                };
                workerThread = new Thread(worker);
            } else {
                prototype = imagePrototype;
                final ImageHolderFigure loaderFigure = ((ImageHolderFigure) prototype.clone());
                BackgroundTask worker;
                worker = new BackgroundTask() {

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
                    protected void failed(Throwable t) {
                        JOptionPane.showMessageDialog(v.getComponent(),
                                t.getMessage(),
                                null,
                                JOptionPane.ERROR_MESSAGE);
                        getDrawing().remove(createdFigure);
                        fireToolDone();
                    }
                };
                workerThread = new Thread(worker);
            }
            workerThread.start();
        } else {
            //getDrawing().remove(createdFigure);
            if (isToolDoneAfterCreation()) {
                fireToolDone();
            }
        }
    }

    @Override
    protected Figure createFigure() {
        if (prototype instanceof CompositeFigure) {
            // we must not apply default attributs to the composite figure,
            // because this would change the look of the figures that we
            // read from the SVG file.
            return prototype.clone();
        } else {
            return super.createFigure();
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
