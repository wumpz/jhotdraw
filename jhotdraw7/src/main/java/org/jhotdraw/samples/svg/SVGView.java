/*
 * @(#)SVGView.java
 *
 * Copyright (c) 1996-2010 by the original authors of JHotDraw
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

import org.jhotdraw.app.action.edit.RedoAction;
import org.jhotdraw.app.action.edit.UndoAction;
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
import org.jhotdraw.draw.*;
import org.jhotdraw.gui.JFileURIChooser;
import org.jhotdraw.gui.URIChooser;
import org.jhotdraw.net.URIUtil;

/**
 * A view for SVG drawings.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SVGView extends AbstractView {

    public final static String DRAWING_PROPERTY = "drawing";
    public final static String GRID_VISIBLE_PROPERTY = "gridVisible";
    protected JFileURIChooser exportChooser;
    /**
     * Each SVGView uses its own undo redo manager.
     * This allows for undoing and redoing actions per view.
     */
    private UndoRedoManager undo;
    private PropertyChangeListener propertyHandler;

    /**
     * Creates a new View.
     */
    public SVGView() {
        initComponents();

        JPanel zoomButtonPanel = new JPanel(new BorderLayout());

        undo = svgPanel.getUndoRedoManager();
        Drawing oldDrawing = svgPanel.getDrawing();
        svgPanel.setDrawing(createDrawing());
        firePropertyChange(DRAWING_PROPERTY, oldDrawing, svgPanel.getDrawing());
        svgPanel.getDrawing().addUndoableEditListener(undo);
        initActions();
        undo.addPropertyChangeListener(propertyHandler = new PropertyChangeListener() {

            @Override
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

    public UndoRedoManager getUndoManager() {
        return undo;
    }

    /**
     * Initializes view specific actions.
     */
    private void initActions() {
        getActionMap().put(UndoAction.ID, undo.getUndoAction());
        getActionMap().put(RedoAction.ID, undo.getRedoAction());
    }

    @Override
    protected void setHasUnsavedChanges(boolean newValue) {
        super.setHasUnsavedChanges(newValue);
        undo.setHasSignificantEdits(newValue);
    }

    /**
     * Writes the view to the specified uri.
     */
    @Override
    public void write(URI uri, URIChooser chooser) throws IOException {
        new SVGOutputFormat().write(new File(uri), svgPanel.getDrawing());
    }

    /**
     * Reads the view from the specified uri.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void read(final URI uri, URIChooser chooser) throws IOException {
        try {
            JFileURIChooser fc = (JFileURIChooser) chooser;

            final Drawing drawing = createDrawing();

            // We start with the selected uri format in the uri chooser,
            // and then try out all formats we can import.
            // We need to try out all formats, because the user may have
            // chosen to load a uri without having used the uri chooser.

            HashMap<javax.swing.filechooser.FileFilter, InputFormat> fileFilterInputFormatMap = null;
            if (fc != null) {
                fileFilterInputFormatMap = (HashMap<javax.swing.filechooser.FileFilter, InputFormat>) fc.getClientProperty(SVGApplicationModel.INPUT_FORMAT_MAP_CLIENT_PROPERTY);
            }
            //private HashMap<javax.swing.filechooser.FileFilter, OutputFormat> fileFilterOutputFormatMap;


            InputFormat selectedFormat = (fc == null) ? null : fileFilterInputFormatMap.get(fc.getFileFilter());
            boolean success = false;
            if (selectedFormat != null) {
                try {
                    selectedFormat.read(uri, drawing, true);
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
                            sfi.read(uri, drawing, true);
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

                @Override
                public void run() {
                    Drawing oldDrawing = svgPanel.getDrawing();
                    svgPanel.setDrawing(drawing);
                    firePropertyChange(DRAWING_PROPERTY, oldDrawing, svgPanel.getDrawing());
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

    @Override
    public void setEnabled(boolean newValue) {
        svgPanel.setEnabled(newValue);
        super.setEnabled(newValue);
    }

    /**
     * Clears the view.
     */
    @Override
    public void clear() {
        final Drawing newDrawing = createDrawing();
        try {
            Runnable r = new Runnable() {

                @Override
                public void run() {
                    Drawing oldDrawing = svgPanel.getDrawing();
                    svgPanel.setDrawing(newDrawing);
                    firePropertyChange(DRAWING_PROPERTY, oldDrawing, newDrawing);
                    if (oldDrawing != null) {
                        oldDrawing.removeAllChildren();
                        oldDrawing.removeUndoableEditListener(undo);
                    }
                    undo.discardAllEdits();
                    newDrawing.addUndoableEditListener(undo);
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
    public boolean canSaveTo(URI file) {
        return file.getPath().endsWith(".svg")
                || file.getPath().endsWith(".svgz");
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
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jhotdraw.samples.svg.SVGDrawingPanel svgPanel;
    // End of variables declaration//GEN-END:variables
}
