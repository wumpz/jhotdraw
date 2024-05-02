/*
 * @(#)DrawView.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 *
 */
package org.jhotdraw.samples.draw;

import java.awt.*;
import java.awt.geom.*;
import java.awt.print.Pageable;
import java.beans.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.URI;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.*;
import org.jhotdraw.action.edit.RedoAction;
import org.jhotdraw.action.edit.UndoAction;
import org.jhotdraw.api.gui.URIChooser;
import org.jhotdraw.app.AbstractView;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.QuadTreeDrawing;
import org.jhotdraw.draw.figure.ImageFigure;
import org.jhotdraw.draw.figure.TextAreaFigure;
import org.jhotdraw.draw.figure.TextFigure;
import org.jhotdraw.draw.io.InputFormat;
import org.jhotdraw.draw.io.OutputFormat;
import org.jhotdraw.draw.print.DrawingPageable;
import org.jhotdraw.editor.DefaultDrawingEditor;
import org.jhotdraw.gui.PlacardScrollPaneLayout;
import org.jhotdraw.gui.action.ButtonFactory;
import org.jhotdraw.io.DOMDefaultDrawFigureFactory;
import org.jhotdraw.io.DOMStorableInputFormat;
import org.jhotdraw.io.DOMStorableOutputFormat;
import org.jhotdraw.io.ImageInputFormat;
import org.jhotdraw.io.ImageOutputFormat;
import org.jhotdraw.io.TextInputFormat;
import org.jhotdraw.net.URIUtil;
import org.jhotdraw.undo.UndoRedoManager;
import org.jhotdraw.util.*;

/**
 * Provides a view on a drawing.
 *
 * <p>
 *
 * <p>
 *
 * <p>
 *
 * <p>See {@link org.jhotdraw.api.app.View} interface on how this view interacts with an
 * application.
 */
public class DrawView extends AbstractView {

  private static final long serialVersionUID = 1L;

  /**
   * Each DrawView uses its own undo redo manager. This allows for undoing and redoing actions per
   * view.
   */
  private UndoRedoManager undo;

  /**
   * Depending on the type of an application, there may be one editor per view, or a single shared
   * editor for all views.
   */
  private DrawingEditor editor;

  /** Creates a new view. */
  public DrawView() {
    initComponents();
    scrollPane.setLayout(new PlacardScrollPaneLayout());
    scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
    setEditor(new DefaultDrawingEditor());
    undo = new UndoRedoManager();
    view.setDrawing(createDrawing());
    view.getDrawing().addUndoableEditListener(undo);
    initActions();
    undo.addPropertyChangeListener(new PropertyChangeListener() {
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
  }

  /** Creates a new Drawing for this view. */
  protected Drawing createDrawing() {
    Drawing drawing = new QuadTreeDrawing();
    final DOMDefaultDrawFigureFactory domDefaultDrawFigureFactory =
        new DOMDefaultDrawFigureFactory();
    drawing.addInputFormat(new DOMStorableInputFormat(domDefaultDrawFigureFactory));
    ImageFigure prototype = new ImageFigure();
    drawing.addInputFormat(new ImageInputFormat(prototype));
    drawing.addInputFormat(new TextInputFormat(new TextFigure()));
    TextAreaFigure taf = new TextAreaFigure();
    taf.setBounds(new Point2D.Double(10, 10), new Point2D.Double(60, 40));
    drawing.addInputFormat(new TextInputFormat(taf));
    drawing.addOutputFormat(new DOMStorableOutputFormat(domDefaultDrawFigureFactory));
    drawing.addOutputFormat(new ImageOutputFormat());
    return drawing;
  }

  /** Creates a Pageable object for printing the view. */
  public Pageable createPageable() {
    return new DrawingPageable(view.getDrawing());
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
  public void write(URI f, URIChooser fc) throws IOException {
    Drawing drawing = view.getDrawing();
    OutputFormat outputFormat = drawing.getOutputFormats().get(0);
    outputFormat.write(f, drawing);
  }

  /** Reads the view from the specified uri. */
  @Override
  public void read(URI f, URIChooser fc) throws IOException {
    try {
      final Drawing drawing = createDrawing();
      boolean success = false;
      for (InputFormat sfi : drawing.getInputFormats()) {
        try {
          sfi.read(f, drawing, true);
          success = true;
          break;
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      if (!success) {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
        throw new IOException(
            labels.getFormatted("file.open.unsupportedFileFormat.message", URIUtil.getName(f)));
      }
      SwingUtilities.invokeAndWait(new Runnable() {
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
      error.initCause(e);
      throw error;
    }
  }

  private static final Logger LOG = Logger.getLogger(DrawView.class.getName());

  /** Sets a drawing editor for the view. */
  public void setEditor(DrawingEditor newValue) {
    if (editor != null) {
      editor.remove(view);
    }
    editor = newValue;
    if (editor != null) {
      editor.add(view);
    }
  }

  /** Gets the drawing editor of the view. */
  public DrawingEditor getEditor() {
    return editor;
  }

  /** Clears the view. */
  @Override
  public void clear() {
    final Drawing newDrawing = createDrawing();
    try {
      SwingUtilities.invokeAndWait(new Runnable() {
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
  public boolean canSaveTo(URI file) {
    return new File(file).getName().endsWith(".xml");
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
