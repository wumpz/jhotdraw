/*
 * @(#)DrawView.java  1.3.1  2008-03-19
 *
 * Copyright (c) 1996-2008 by the original authors of JHotDraw
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
package org.jhotdraw.samples.draw;

import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.print.Pageable;
import org.jhotdraw.draw.ImageInputFormat;
import org.jhotdraw.draw.ImageOutputFormat;
import org.jhotdraw.draw.InputFormat;
import org.jhotdraw.draw.OutputFormat;
import org.jhotdraw.gui.*;
import org.jhotdraw.io.*;
import org.jhotdraw.draw.DOMStorableInputOutputFormat;
import org.jhotdraw.undo.*;
import org.jhotdraw.util.*;
import java.awt.*;
import java.beans.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import org.jhotdraw.app.AbstractView;
import org.jhotdraw.app.action.RedoAction;
import org.jhotdraw.app.action.UndoAction;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.action.*;
import org.jhotdraw.xml.*;

/**
 * A view for JHotDraw drawings.
 *
 * @author Werner Randelshofer
 * @version 1.3.1 2008-03-19 Method read() tries out now all supported files format.
 * <br>1.3 2007-11-25 Method clear() is now invoked from a worker thread.
 * <br>1.2 2006-12-26 Reworked I/O support.
 * <br>1.1 2006-06-10 Extended to support DefaultDrawApplicationModel.
 * <br>1.0 2006-02-07 Created.
 */
public class DrawView extends AbstractView {
    
    /**
     * Each DrawView uses its own undo redo manager.
     * This allows for undoing and redoing actions per view.
     */
    private UndoRedoManager undo;
    
    /**
     * Depending on the type of an application, there may be one editor per
     * view, or a single shared editor for all views.
     */
    private DrawingEditor editor;
    
    /**
     * Creates a new view.
     */
    public DrawView() {
    }
    
    /**
     * Initializes the view.
     */
    public void init() {
        super.init();
        
        initComponents();
        
        JPanel zoomButtonPanel = new JPanel(new BorderLayout());
        scrollPane.setLayout(new PlacardScrollPaneLayout());
        scrollPane.setBorder(new EmptyBorder(0,0,0,0));
        
        setEditor(new DefaultDrawingEditor());
        undo = new UndoRedoManager();
        view.setDrawing(createDrawing());
        view.getDrawing().addUndoableEditListener(undo);
        initActions();
        undo.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                setHasUnsavedChanges(undo.hasSignificantEdits());
            }
        });
        
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        
        JPanel placardPanel = new JPanel(new BorderLayout());
        javax.swing.AbstractButton pButton;
        pButton = ButtonFactory.createZoomButton(view);
        pButton.putClientProperty("Quaqua.Button.style","placard");
        pButton.putClientProperty("Quaqua.Component.visualMargin",new Insets(0,0,0,0));
        pButton.setFont(UIManager.getFont("SmallSystemFont"));
        placardPanel.add(pButton, BorderLayout.WEST);
        pButton = ButtonFactory.createToggleGridButton(view);
        pButton.putClientProperty("Quaqua.Button.style","placard");
        pButton.putClientProperty("Quaqua.Component.visualMargin",new Insets(0,0,0,0));
        pButton.setFont(UIManager.getFont("SmallSystemFont"));
        labels.configureToolBarButton(pButton, "alignGridSmall");
        placardPanel.add(pButton, BorderLayout.EAST);
        scrollPane.add(placardPanel, JScrollPane.LOWER_LEFT_CORNER);
    }
    
    /**
     * Creates a new Drawing for this view.
     */
    protected Drawing createDrawing() {
        Drawing drawing = new QuadTreeDrawing();
        DOMStorableInputOutputFormat ioFormat =
                new DOMStorableInputOutputFormat(new DrawFigureFactory());
        
        drawing.addInputFormat(ioFormat);
        ImageFigure prototype = new ImageFigure();
        
        drawing.addInputFormat(new ImageInputFormat(prototype));
        drawing.addInputFormat(new ImageInputFormat(prototype, "JPG","Joint Photographics Experts Group (JPEG)", "jpg", BufferedImage.TYPE_INT_RGB));
        drawing.addInputFormat(new ImageInputFormat(prototype, "GIF","Graphics Interchange Format (GIF)", "gif", BufferedImage.TYPE_INT_ARGB));
        drawing.addInputFormat(new ImageInputFormat(prototype));
        drawing.addInputFormat(new PictImageInputFormat(prototype));
        drawing.addInputFormat(new TextInputFormat(new TextFigure()));
        TextAreaFigure taf = new TextAreaFigure();
        taf.setBounds(new Point2D.Double(10,10), new Point2D.Double(60,40));
        drawing.addInputFormat(new TextInputFormat(taf));
        
        drawing.addOutputFormat(ioFormat);
        drawing.addOutputFormat(new ImageOutputFormat());
        return drawing;
    }
    
    public DrawingEditor getEditor() {
        return editor;
    }
    public void setEditor(DrawingEditor newValue) {
        DrawingEditor oldValue = editor;
        if (oldValue != null) {
            oldValue.remove(view);
        }
        editor = newValue;
        if (newValue != null) {
            newValue.add(view);
        }
    }
    
    /**
     * Creates a Pageable object for printing the view.
     */
    public Pageable createPageable() {
        return new DrawingPageable(view.getDrawing());
        
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
     * Writes the view to the specified file.
     */
    public void write(File f) throws IOException {
        Drawing drawing = view.getDrawing();
        OutputFormat outputFormat = drawing.getOutputFormats().get(0);
        outputFormat.write(f, drawing);
    }
    
    /**
     * Reads the view from the specified file.
     */
    public void read(File f) throws IOException {
        try {
            JFileChooser fc = getOpenChooser();

            final Drawing drawing = createDrawing();

            boolean success = false;
                for (InputFormat sfi : drawing.getInputFormats()) {
                        try {
                            sfi.read(f, drawing);
                            success = true;
                            break;
                        } catch (Exception e) {
                        // try with the next input format
                        }
                    }
            if (!success) {
                ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
                throw new IOException(labels.getFormatted("errorUnsupportedFileFormat", f.getName()));
            }
            SwingUtilities.invokeAndWait(new Runnable() {

                public void run() {
                    view.getDrawing().removeUndoableEditListener(undo);
                    view.setDrawing(drawing);
                    view.getDrawing().addUndoableEditListener(undo);
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
    
    
    /**
     * Sets a drawing editor for the view.
     */
    public void setDrawingEditor(DrawingEditor newValue) {
        if (editor != null) {
            editor.remove(view);
        }
        editor = newValue;
        if (editor != null) {
            editor.add(view);
        }
    }
    
    /**
     * Gets the drawing editor of the view.
     */
    public DrawingEditor getDrawingEditor() {
        return editor;
    }
    
    /**
     * Clears the view.
     */
    public void clear() {
        final Drawing newDrawing = createDrawing();
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    view.getDrawing().removeUndoableEditListener(undo);
                    view.setDrawing(newDrawing);
                    view.getDrawing().addUndoableEditListener(undo);
                    undo.discardAllEdits();
                }
            });
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override protected JFileChooser createOpenChooser() {
        JFileChooser c = super.createOpenChooser();
        c.addChoosableFileFilter(new ExtensionFileFilter("Drawing .xml","xml"));
        return c;
    }
    @Override protected JFileChooser createSaveChooser() {
        JFileChooser c = super.createSaveChooser();
        c.addChoosableFileFilter(new ExtensionFileFilter("Drawing .xml","xml"));
        return c;
    }
    @Override
    public boolean canSaveTo(File file) {
        return file.getName().endsWith(".xml");
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPane = new javax.swing.JScrollPane();
        view = new org.jhotdraw.draw.DefaultDrawingView();

        setLayout(new java.awt.BorderLayout());

        scrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setViewportView(view);

        add(scrollPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane scrollPane;
    private org.jhotdraw.draw.DefaultDrawingView view;
    // End of variables declaration//GEN-END:variables
    
}
