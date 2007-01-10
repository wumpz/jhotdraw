/*
 * @(#)SVGProject.java  1.2  2006-12-10
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
 *
 */
package org.jhotdraw.samples.svg;

import java.awt.image.BufferedImage;
import java.awt.print.Pageable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.prefs.Preferences;
import org.jhotdraw.draw.ImageInputFormat;
import org.jhotdraw.draw.ImageOutputFormat;
import org.jhotdraw.draw.OutputFormat;
import org.jhotdraw.gui.*;
import org.jhotdraw.io.*;
import org.jhotdraw.draw.InputFormat;
import org.jhotdraw.samples.svg.figures.*;
import org.jhotdraw.samples.svg.io.*;
import org.jhotdraw.undo.*;
import org.jhotdraw.util.*;
import java.awt.*;
import java.beans.*;
import java.io.*;
import java.lang.reflect.*;
import javax.swing.*;
import javax.swing.border.*;
import org.jhotdraw.app.*;
import org.jhotdraw.app.action.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.action.*;
import org.jhotdraw.xml.*;

/**
 * A drawing project.
 *
 * @author Werner Randelshofer
 * @version 1.2 2006-12-10 Used SVGStorage for reading SVG drawing (experimental). 
 * <br>1.1 2006-06-10 Extended to support DefaultDrawApplicationModel.
 * <br>1.0 2006-02-07 Created.
 */
public class SVGProject extends AbstractProject implements ExportableProject {
    protected JFileChooser exportChooser;
   
    /**
     * Each SVGProject uses its own undo redo manager.
     * This allows for undoing and redoing actions per project.
     */
    private UndoRedoManager undo;
    
    /**
     * Depending on the type of an application, there may be one editor per
     * project, or a single shared editor for all projects.
     */
    private DrawingEditor editor;
   
    private HashMap<javax.swing.filechooser.FileFilter, OutputFormat> fileFilterOutputFormatMap;
    
    private Preferences prefs;
    /**
     * Creates a new Project.
     */
    public SVGProject() {
    }
    
    /**
     * Initializes the project.
     */
    public void init() {
        super.init();
        prefs = Preferences.userNodeForPackage(getClass());
        
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
        pButton = ToolBarButtonFactory.createZoomButton(view);
        pButton.putClientProperty("Quaqua.Button.style","placard");
        pButton.putClientProperty("Quaqua.Component.visualMargin",new Insets(0,0,0,0));
        pButton.setFont(UIManager.getFont("SmallSystemFont"));
        placardPanel.add(pButton, BorderLayout.WEST);
        pButton = ToolBarButtonFactory.createToggleGridButton(view);
        pButton.putClientProperty("Quaqua.Button.style","placard");
        pButton.putClientProperty("Quaqua.Component.visualMargin",new Insets(0,0,0,0));
        pButton.setFont(UIManager.getFont("SmallSystemFont"));
        labels.configureToolBarButton(pButton, "alignGridSmall");
        placardPanel.add(pButton, BorderLayout.EAST);
        scrollPane.add(placardPanel, JScrollPane.LOWER_LEFT_CORNER);
    }
    
    /**
     * Creates a new Drawing for this Project.
     */
    protected Drawing createDrawing() {
        Drawing drawing = new SVGDrawing();
        LinkedList<InputFormat> inputFormats = new LinkedList<InputFormat>();
        inputFormats.add(new SVGInputFormat());
        inputFormats.add(new ImageInputFormat(new SVGImageFigure()));
        drawing.setInputFormats(inputFormats);
        LinkedList<OutputFormat> outputFormats = new LinkedList<OutputFormat>();
        outputFormats.add(new SVGOutputFormat());
        outputFormats.add(new ImageOutputFormat());
        outputFormats.add(new ImageOutputFormat("JPG","Joint Photographics Experts Group (JPEG)", "jpg", BufferedImage.TYPE_INT_RGB));
        outputFormats.add(new ImageOutputFormat("BMP","Windows Bitmap (BMP)", "bmp", BufferedImage.TYPE_BYTE_INDEXED));
        drawing.setOutputFormats(outputFormats);
        return drawing;
    }
    /**
     * Creates a Pageable object for printing the project.
     */
    public Pageable createPageable() {
        return new DrawingPageable(view.getDrawing());
        
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
     * Initializes project specific actions.
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
     * Writes the project to the specified file.
     */
    public void write(File f) throws IOException {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(f));
            new SVGOutputFormat().write(f, view.getDrawing());
        } finally {
            if (out != null) out.close();
        }
    }
    
    /**
     * Reads the project from the specified file.
     */
    public void read(File f) throws IOException {
        try {
            InputFormat sf = new SVGInputFormat();
            final Drawing drawing = createDrawing();
            sf.read(f, drawing);
            SwingUtilities.invokeAndWait(new Runnable() { public void run() {
                view.getDrawing().removeUndoableEditListener(undo);
                view.setDrawing(drawing);
                view.getDrawing().addUndoableEditListener(undo);
                undo.discardAllEdits();
            }});
        } catch (InterruptedException e) {
            InternalError error = new InternalError();
            e.initCause(e);
            throw error;
        } catch (InvocationTargetException e) {
            InternalError error = new InternalError();
            e.initCause(e);
            throw error;
        }
    }
    
    /**
     * Sets a drawing editor for the project.
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
     * Gets the drawing editor of the project.
     */
    public DrawingEditor getDrawingEditor() {
        return editor;
    }
    
    public void setEnabled(boolean newValue) {
        view.setEnabled(newValue);
        super.setEnabled(newValue);
    }
    
    /**
     * Clears the project.
     */
    public void clear() {
        view.setDrawing(new SVGDrawing());
        undo.discardAllEdits();
    }
    
    @Override protected JFileChooser createOpenChooser() {
        JFileChooser c = super.createOpenChooser();
        c.addChoosableFileFilter(new ExtensionFileFilter("SVG Drawing","svg"));
        return c;
    }
    @Override protected JFileChooser createSaveChooser() {
        JFileChooser c = super.createSaveChooser();
        
        fileFilterOutputFormatMap = new HashMap<javax.swing.filechooser.FileFilter,OutputFormat>();
      //  c.addChoosableFileFilter(new ExtensionFileFilter("SVG Drawing","svg"));
        for (OutputFormat format : view.getDrawing().getOutputFormats()) {
            javax.swing.filechooser.FileFilter ff = format.getFileFilter();
            fileFilterOutputFormatMap.put(ff, format);
            c.addChoosableFileFilter(ff);
            break; // only add the first file filter
        }
        
        return c;
    }
    protected JFileChooser createExportChooser() {
        JFileChooser c = new JFileChooser();
        
        fileFilterOutputFormatMap = new HashMap<javax.swing.filechooser.FileFilter,OutputFormat>();
      //  c.addChoosableFileFilter(new ExtensionFileFilter("SVG Drawing","svg"));
        javax.swing.filechooser.FileFilter currentFilter = null;
        for (OutputFormat format : view.getDrawing().getOutputFormats()) {
            javax.swing.filechooser.FileFilter ff = format.getFileFilter();
            fileFilterOutputFormatMap.put(ff, format);
            c.addChoosableFileFilter(ff);
            if (ff.getDescription().equals(prefs.get("projectExportFormat",""))) {
                currentFilter = ff;
            }
        }
        if (currentFilter != null) {
            c.setFileFilter(currentFilter);
        }
            c.setSelectedFile(new File(prefs.get("projectExportFile", System.getProperty("user.home"))));
        
        return c;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        scrollPane = new javax.swing.JScrollPane();
        view = new org.jhotdraw.draw.DefaultDrawingView();

        setLayout(new java.awt.BorderLayout());

        scrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setViewportView(view);

        add(scrollPane, java.awt.BorderLayout.CENTER);

    }// </editor-fold>//GEN-END:initComponents

    public JFileChooser getExportChooser() {
        if (exportChooser == null) {
            exportChooser = createSaveChooser();
        }
        return exportChooser;
    }

    public void export(File f, javax.swing.filechooser.FileFilter filter, Component accessory) throws IOException {

                OutputFormat format = fileFilterOutputFormatMap.get(filter);
                
                if (! f.getName().endsWith("."+format.getFileExtension())) {
                    f = new File(f.getPath()+"."+format.getFileExtension());
                }
                
        format.write(f, view.getDrawing());
        
           prefs.put("projectExportFile", f.getPath());
         prefs.put("projectExportFormat", filter.getDescription());
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane scrollPane;
    private org.jhotdraw.draw.DefaultDrawingView view;
    // End of variables declaration//GEN-END:variables
    
}
