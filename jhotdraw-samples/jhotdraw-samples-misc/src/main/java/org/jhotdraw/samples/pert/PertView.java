/*
 * @(#)PertView.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 *
 */
package org.jhotdraw.samples.pert;

import java.awt.*;
import java.awt.print.Pageable;
import java.beans.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.URI;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import org.jhotdraw.action.edit.RedoAction;
import org.jhotdraw.action.edit.UndoAction;
import org.jhotdraw.api.app.View;
import org.jhotdraw.api.gui.URIChooser;
import org.jhotdraw.app.AbstractView;
import org.jhotdraw.draw.DefaultDrawing;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.io.InputFormat;
import org.jhotdraw.draw.io.OutputFormat;
import org.jhotdraw.draw.print.DrawingPageable;
import org.jhotdraw.editor.DefaultDrawingEditor;
import org.jhotdraw.gui.PlacardScrollPaneLayout;
import org.jhotdraw.gui.action.ButtonFactory;
import org.jhotdraw.io.DOMStorableInputFormat;
import org.jhotdraw.io.DOMStorableOutputFormat;
import org.jhotdraw.io.ImageOutputFormat;
import org.jhotdraw.undo.UndoRedoManager;
import org.jhotdraw.util.*;

/**
 * Provides a view on a Pert drawing.
 *
 * <p>See {@link View} interface on how this view interacts with an application.
 */
public class PertView extends AbstractView {

  private static final long serialVersionUID = 1L;
  public static final String GRID_VISIBLE_PROPERTY = "gridVisible";

  /**
   * Each view uses its own undo redo manager. This allows for undoing and redoing actions per view.
   */
  private UndoRedoManager undo;

  /**
   * Depending on the type of an application, there may be one editor per view, or a single shared
   * editor for all views.
   */
  private DrawingEditor editor;

  /** Creates a new view. */
  public PertView() {
    initComponents();
    scrollPane.setLayout(new PlacardScrollPaneLayout());
    scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
    setEditor(new DefaultDrawingEditor());
    undo = new UndoRedoManager();
    view.setDrawing(createDrawing());
    view.getDrawing().addUndoableEditListener(undo);
    initActions();
    undo.addPropertyChangeListener(
        new PropertyChangeListener() {
          @Override
          public void propertyChange(PropertyChangeEvent evt) {
            setHasUnsavedChanges(undo.hasSignificantEdits());
          }
        });
    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
    JPanel placardPanel = new JPanel(new BorderLayout());
    javax.swing.AbstractButton pButton;
    pButton = ButtonFactory.createZoomButton(view);
    pButton.putClientProperty("Quaqua.Button.style", "placard");
    pButton.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, 0, 0, 0));
    pButton.setFont(UIManager.getFont("SmallSystemFont"));
    placardPanel.add(pButton, BorderLayout.WEST);
    pButton = ButtonFactory.createToggleGridButton(view);
    pButton.putClientProperty("Quaqua.Button.style", "placard");
    pButton.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, 0, 0, 0));
    pButton.setFont(UIManager.getFont("SmallSystemFont"));
    labels.configureToolBarButton(pButton, "view.toggleGrid.placard");
    placardPanel.add(pButton, BorderLayout.EAST);
    scrollPane.add(placardPanel, JScrollPane.LOWER_LEFT_CORNER);
    // setGridVisible(preferences.getBoolean("view.gridVisible", false));
    // setScaleFactor(preferences.getDouble("view.scaleFactor", 1d));
  }

  /** Creates a new Drawing for this view. */
  protected Drawing createDrawing() {
    DefaultDrawing drawing = new DefaultDrawing();
    LinkedList<InputFormat> inputFormats = new LinkedList<InputFormat>();
    inputFormats.add(new DOMStorableInputFormat(new PertFactory()));
    drawing.setInputFormats(inputFormats);
    LinkedList<OutputFormat> outputFormats = new LinkedList<OutputFormat>();
    outputFormats.add(new DOMStorableOutputFormat(new PertFactory()));
    outputFormats.add(new ImageOutputFormat());
    drawing.setOutputFormats(outputFormats);
    return drawing;
  }

  /** Creates a Pageable object for printing this view. */
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

  public void setGridVisible(boolean newValue) {
    boolean oldValue = isGridVisible();
    view.setConstrainerVisible(newValue);
    firePropertyChange(GRID_VISIBLE_PROPERTY, oldValue, newValue);
    preferences.putBoolean("view.gridVisible", newValue);
  }

  public boolean isGridVisible() {
    return view.isConstrainerVisible();
  }

  public double getScaleFactor() {
    return view.getScaleFactor();
  }

  public void setScaleFactor(double newValue) {
    double oldValue = getScaleFactor();
    view.setScaleFactor(newValue);
    firePropertyChange("scaleFactor", oldValue, newValue);
    preferences.putDouble("view.scaleFactor", newValue);
  }

  /** Initializes view specific actions. */
  private void initActions() {
    getActionMap().put(UndoAction.ID, undo.getUndoAction());
    getActionMap().put(RedoAction.ID, undo.getRedoAction());
  }

  @Override
  protected void setHasUnsavedChanges(boolean newValue) {
    super.setHasUnsavedChanges(newValue);
    undo.setHasSignificantEdits(newValue);
  }

  /** Writes the view to the specified uri. */
  @Override
  public void write(URI f, URIChooser chooser) throws IOException {
    Drawing drawing = view.getDrawing();
    OutputFormat outputFormat = drawing.getOutputFormats().get(0);
    outputFormat.write(f, drawing);
  }

  /** Reads the view from the specified uri. */
  @Override
  public void read(URI f, URIChooser chooser) throws IOException {
    try {
      final Drawing drawing = createDrawing();
      InputFormat inputFormat = drawing.getInputFormats().get(0);
      inputFormat.read(f, drawing, true);
      SwingUtilities.invokeAndWait(
          new Runnable() {
            @Override
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
      e.initCause(e);
      throw error;
    }
  }

  /** Clears the view. */
  @Override
  public void clear() {
    final Drawing newDrawing = createDrawing();
    try {
      SwingUtilities.invokeAndWait(
          new Runnable() {
            @Override
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

  @Override
  public boolean canSaveTo(URI uri) {
    return uri.getPath().endsWith(".xml");
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT
   * modify this code. The content of this method is always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    scrollPane = new javax.swing.JScrollPane();
    view = new org.jhotdraw.draw.DefaultDrawingView();
    setLayout(new java.awt.BorderLayout());
    scrollPane.setHorizontalScrollBarPolicy(
        javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    scrollPane.setVerticalScrollBarPolicy(
        javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    scrollPane.setViewportView(view);
    add(scrollPane, java.awt.BorderLayout.CENTER);
  } // </editor-fold>//GEN-END:initComponents

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JScrollPane scrollPane;
  private org.jhotdraw.draw.DefaultDrawingView view;
  // End of variables declaration//GEN-END:variables
}
