/*
 * @(#)DrawProject.java  1.1  2006-06-10
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
package org.jhotdraw.samples.net;

import java.util.prefs.*;
import org.jhotdraw.gui.*;
import org.jhotdraw.io.*;
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
import org.jhotdraw.samples.pert.figures.*;

/**
 * A Pert drawing project.
 *
 * @author Werner Randelshofer
 * @version 1.1 2006-06-10 Extended to support DefaultDrawApplicationModel.
 * <br>1.0 2006-02-07 Created.
 */
public class NetProject extends AbstractProject {
    
    /**
     * Each DrawProject uses its own undo redo manager.
     * This allows for undoing and redoing actions per project.
     */
    private UndoRedoManager undo;
    
    /**
     * Depending on the type of an application, there may be one editor per
     * project, or a single shared editor for all projects.
     */
    private DrawingEditor editor;
    
    private GridConstrainer visibleConstrainer = new GridConstrainer(10, 10);
    private GridConstrainer invisibleConstrainer = new GridConstrainer(1, 1);
    private Preferences prefs;
    private AbstractButton toggleGridButton;
    
    /**
     * Creates a new Project.
     */
    public NetProject() {
    }
    
    /**
     * Initializes the project.
     */
    public void init() {
        super.init();
        
        initComponents();
        
        JPanel zoomButtonPanel = new JPanel(new BorderLayout());
        scrollPane.setLayout(new PlacardScrollPaneLayout());
        scrollPane.setBorder(new EmptyBorder(0,0,0,0));
        
        setEditor(new DefaultDrawingEditor());
        view.setDOMFactory(new NetFactory());
        undo = new UndoRedoManager();
        view.setDrawing(new DefaultDrawing());
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
        toggleGridButton = pButton = ToolBarButtonFactory.createToggleGridButton(view);
        pButton.putClientProperty("Quaqua.Button.style","placard");
        pButton.putClientProperty("Quaqua.Component.visualMargin",new Insets(0,0,0,0));
        pButton.setFont(UIManager.getFont("SmallSystemFont"));
        labels.configureToolBarButton(pButton, "alignGridSmall");
        placardPanel.add(pButton, BorderLayout.EAST);
        scrollPane.add(placardPanel, JScrollPane.LOWER_LEFT_CORNER);
        
        prefs = Preferences.userNodeForPackage(getClass());
        toggleGridButton.setSelected(prefs.getBoolean("project.gridVisible", false));
        setScaleFactor(prefs.getDouble("project.scaleFactor", 1d));
        
        view.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                String name = evt.getPropertyName();
                if (name.equals("constrainer")) {
                    prefs.putBoolean("project.gridVisible", ((Constrainer) evt.getNewValue()).isVisible());
                    firePropertyChange("gridVisible", ((Constrainer) evt.getOldValue()).isVisible(), ((Constrainer) evt.getNewValue()).isVisible());
                } else if (name.equals("scaleFactor")) {
                    prefs.putDouble("project.scaleFactor", (Double) evt.getNewValue());
                    firePropertyChange("scaleFactor", evt.getOldValue(), evt.getNewValue());
                }
            }
        });
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
    
    public void setGridVisible(boolean newValue) {
        // XXX This code is messy. We should invoke something on view.
        toggleGridButton.setSelected(newValue);
    }
    public boolean isGridVisible() {
        return view.getConstrainer().isVisible();
    }
    public double getScaleFactor() {
        return view.getScaleFactor();
    }
    public void setScaleFactor(double newValue) {
        view.setScaleFactor(newValue);
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
            NanoXMLLiteDOMOutput domo = new NanoXMLLiteDOMOutput(view.getDOMFactory());
            domo.writeObject(view.getDrawing());
            domo.save(out);
        } finally {
            if (out != null) try { out.close(); } catch (IOException e) {};
            //if (out != null) out.close();
        }
    }
    
    /**
     * Reads the project from the specified file.
     */
    public void read(File f) throws IOException {
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            NanoXMLLiteDOMInput domi = new NanoXMLLiteDOMInput(view.getDOMFactory(), in);
            final Drawing drawing = (Drawing) domi.readObject();
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
        } finally {
            if (in != null) try { in.close(); } catch (IOException e) {};
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
    
    /**
     * Clears the project.
     */
    public void clear() {
        view.setDrawing(new DefaultDrawing());
        undo.discardAllEdits();
    }
    
    
    @Override protected JFileChooser createOpenChooser() {
        JFileChooser c = super.createOpenChooser();
        c.addChoosableFileFilter(new ExtensionFileFilter("Net Diagram","xml"));
        return c;
    }
    @Override protected JFileChooser createSaveChooser() {
        JFileChooser c = super.createSaveChooser();
        c.addChoosableFileFilter(new ExtensionFileFilter("Net Diagram","xml"));
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
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane scrollPane;
    private org.jhotdraw.draw.DefaultDrawingView view;
    // End of variables declaration//GEN-END:variables
    
}
