/*
 * @(#)SVGView.java
 *
 * Copyright (c) 1996-2009 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 *
 */
package org.jhotdraw.samples.svg;

import org.jhotdraw.draw.io.OutputFormat;
import org.jhotdraw.draw.io.InputFormat;
import org.jhotdraw.draw.print.DrawingPageable;
import java.awt.print.Pageable;
import java.util.HashMap;
import org.jhotdraw.samples.svg.io.*;
import org.jhotdraw.undo.*;
import org.jhotdraw.util.*;
import java.awt.*;
import java.beans.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.URI;
import javax.swing.*;
import org.jhotdraw.app.*;
import org.jhotdraw.app.action.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.gui.chooser.JFileURIChooser;
import org.jhotdraw.gui.chooser.URIChooser;
import org.jhotdraw.net.URIUtil;

/**
 * A view for SVG drawings.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SVGView extends AbstractView implements ExportableView {

    public final static String GRID_VISIBLE_PROPERTY = "gridVisible";
    protected JFileURIChooser exportChooser;
    /**
     * Each SVGView uses its own undo redo manager.
     * This allows for undoing and redoing actions per view.
     */
    private UndoRedoManager undo;
    private HashMap<javax.swing.filechooser.FileFilter, InputFormat> fileFilterInputFormatMap;
    private HashMap<javax.swing.filechooser.FileFilter, OutputFormat> fileFilterOutputFormatMap;
    private PropertyChangeListener propertyHandler;

    /**
     * Creates a new View.
     */
    public SVGView() {
    }

    /**
     * Initializes the View.
     */
    @Override
    public void init() {
        super.init();

        initComponents();

        JPanel zoomButtonPanel = new JPanel(new BorderLayout());

        undo = svgPanel.getUndoRedoManager();
        svgPanel.setDrawing(createDrawing());
        svgPanel.getDrawing().addUndoableEditListener(undo);
        initActions();
        undo.addPropertyChangeListener(propertyHandler = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                setHasUnsavedChanges(undo.hasSignificantEdits());
            }
        });
    }

    @Override
    public void dispose() {
        DrawingEditor e = getEditor();
        clear();

        undo.removePropertyChangeListener(propertyHandler);
        propertyHandler = null;
        svgPanel.dispose();
        super.dispose();
    }

    /**
     * Creates a new Drawing for this View.
     */
    protected Drawing createDrawing() {
        return svgPanel.createDrawing();
    }

    /**
     * Creates a Pageable object for printing the View.
     */
    public Pageable createPageable() {
        return new DrawingPageable(svgPanel.getDrawing());

    }

    public DrawingEditor getEditor() {
        return svgPanel.getEditor();
    }

    public void setEditor(DrawingEditor newValue) {
        svgPanel.setEditor(newValue);
    }

    /**
     * Initializes view specific actions.
     */
    private void initActions() {
        putAction(UndoAction.ID, undo.getUndoAction());
        putAction(RedoAction.ID, undo.getRedoAction());
    }

    protected void setHasUnsavedChanges(boolean newValue) {
        super.setHasUnsavedChanges(newValue);
        undo.setHasSignificantEdits(newValue);
    }

    /**
     * Writes the view to the specified uri.
     */
    public void write(URI uri) throws IOException {
            new SVGOutputFormat().write(new File(uri), svgPanel.getDrawing());
    }

    /**
     * Reads the view from the specified uri.
     */
    public void read(final URI uri) throws IOException {
        try {
            JFileURIChooser fc = (JFileURIChooser)getOpenChooser();

            final Drawing drawing = createDrawing();

            // We start with the selected uri format in the uri chooser,
            // and then try out all formats we can import.
            // We need to try out all formats, because the user may have
            // chosen to load a uri without having used the uri chooser.
            InputFormat selectedFormat = fileFilterInputFormatMap.get(fc.getFileFilter());
            boolean success = false;
            if (selectedFormat != null) {
                try {
                    selectedFormat.read(new File(uri), drawing, true);
                    success = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    // try with the next input format
                }
            }
            if (!success) {
                for (InputFormat sfi : drawing.getInputFormats()) {
                    if (sfi != selectedFormat) {
                        try {
                            sfi.read(new File(uri), drawing, true);
                            success = true;
                            break;
                        } catch (Exception e) {
                            // try with the next input format
                        }
                    }
                }
            }
            if (!success) {
                ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
                throw new IOException(labels.getFormatted("file.open.unsupportedFileFormat.message", URIUtil.getName(uri)));
            }
            SwingUtilities.invokeAndWait(new Runnable() {

                public void run() {
                    svgPanel.setDrawing(drawing);
                    undo.discardAllEdits();
                }
            });
        } catch (InterruptedException e) {
            InternalError error = new InternalError();
            e.initCause(e);
            throw error;
        } catch (InvocationTargetException e) {
            InternalError error = new InternalError();
            error.initCause(e);
            throw error;
        }
    }

    public Drawing getDrawing() {
        return svgPanel.getDrawing();
    }

    public void setEnabled(boolean newValue) {
        svgPanel.setEnabled(newValue);
        super.setEnabled(newValue);
    }

    /**
     * Clears the view.
     */
    public void clear() {
        final Drawing newDrawing = createDrawing();
        try {
            Runnable r = new Runnable() {

                public void run() {
                    Drawing oldDrawing = svgPanel.getDrawing();
                    svgPanel.setDrawing(newDrawing);
                    if (oldDrawing != null) {
                        oldDrawing.removeAllChildren();
                    }
                    undo.discardAllEdits();
                }
            };
            if (SwingUtilities.isEventDispatchThread()) {
                r.run();
            } else {
                SwingUtilities.invokeAndWait(r);
            }
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected URIChooser createOpenChooser() {
        final JFileURIChooser c = new JFileURIChooser();
        fileFilterInputFormatMap =
                new HashMap<javax.swing.filechooser.FileFilter, InputFormat>();
        javax.swing.filechooser.FileFilter firstFF = null;
        Drawing d = svgPanel.getDrawing();
        if (d == null) {
            d = createDrawing();
        }

        for (InputFormat format : d.getInputFormats()) {
            javax.swing.filechooser.FileFilter ff = format.getFileFilter();
            if (firstFF == null) {
                firstFF = ff;
            }

            fileFilterInputFormatMap.put(ff, format);
            c.addChoosableFileFilter(ff);
        }

        c.setFileFilter(firstFF);
        c.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("fileFilterChanged")) {
                    InputFormat inputFormat = fileFilterInputFormatMap.get(evt.getNewValue());
                    c.setAccessory((inputFormat == null) ? null : inputFormat.getInputFormatAccessory());
                }

            }
        });
        if (preferences != null) {
            c.setSelectedFile(new File(preferences.get("projectFile", System.getProperty("user.home"))));
        }

        return c;
    }

    @Override
    protected URIChooser createSaveChooser() {
        JFileURIChooser c = new JFileURIChooser();

        fileFilterOutputFormatMap =
                new HashMap<javax.swing.filechooser.FileFilter, OutputFormat>();
        //  c.addChoosableFileFilter(new ExtensionFileFilter("SVG Drawing","svg"));
        for (OutputFormat format : svgPanel.getDrawing().getOutputFormats()) {
            javax.swing.filechooser.FileFilter ff = format.getFileFilter();
            fileFilterOutputFormatMap.put(ff, format);
            c.addChoosableFileFilter(ff);
            break; // only add the first uri filter

        }


        if (preferences != null) {
            c.setSelectedFile(new File(preferences.get("projectFile", System.getProperty("user.home"))));
        }

        return c;
    }

    protected JFileURIChooser createExportChooser() {
        JFileURIChooser c = new JFileURIChooser();

        fileFilterOutputFormatMap =
                new HashMap<javax.swing.filechooser.FileFilter, OutputFormat>();
        //  c.addChoosableFileFilter(new ExtensionFileFilter("SVG Drawing","svg"));
        javax.swing.filechooser.FileFilter currentFilter = null;
        for (OutputFormat format : svgPanel.getDrawing().getOutputFormats()) {
            javax.swing.filechooser.FileFilter ff = format.getFileFilter();
            fileFilterOutputFormatMap.put(ff, format);
            c.addChoosableFileFilter(ff);
            if (ff.getDescription().equals(preferences.get("viewExportFormat", ""))) {
                currentFilter = ff;
            }

        }
        if (currentFilter != null) {
            c.setFileFilter(currentFilter);
        }

        c.setSelectedFile(new File(preferences.get("viewExportFile", System.getProperty("user.home"))));

        return c;
    }

    @Override
    public boolean canSaveTo(URI file) {
        return file.getPath().endsWith(".svg") ||
                file.getPath().endsWith(".svgz");
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        svgPanel = new org.jhotdraw.samples.svg.SVGDrawingPanel();

        setLayout(new java.awt.BorderLayout());
        add(svgPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    public URIChooser getExportChooser() {
        if (exportChooser == null) {
            exportChooser = createExportChooser();
        }

        return exportChooser;
    }

    public void export(URI uri, javax.swing.filechooser.FileFilter filter, Component accessory) throws IOException {
File f =new File(uri);
        OutputFormat format = fileFilterOutputFormatMap.get(filter);

        if (!f.getName().endsWith("." + format.getFileExtension())) {
            f = new File(f.getPath() + "." + format.getFileExtension());
        }

        format.write(f, svgPanel.getDrawing());

        preferences.put("viewExportFile", f.getPath());
        preferences.put("viewExportFormat", filter.getDescription());
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jhotdraw.samples.svg.SVGDrawingPanel svgPanel;
    // End of variables declaration//GEN-END:variables
}
