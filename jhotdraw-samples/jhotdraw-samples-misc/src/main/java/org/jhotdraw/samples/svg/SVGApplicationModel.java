/*
 * @(#)SVGApplicationModel.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.svg;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import org.jhotdraw.action.edit.ClearSelectionAction;
import org.jhotdraw.action.edit.RedoAction;
import org.jhotdraw.action.edit.UndoAction;
import org.jhotdraw.api.app.Application;
import org.jhotdraw.api.app.ApplicationModel;
import org.jhotdraw.api.app.MenuBuilder;
import org.jhotdraw.api.app.View;
import org.jhotdraw.api.gui.URIChooser;
import org.jhotdraw.app.DefaultApplicationModel;
import org.jhotdraw.app.DefaultMenuBuilder;
import org.jhotdraw.app.action.file.ExportFileAction;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.action.*;
import org.jhotdraw.draw.constrainer.GridConstrainer;
import org.jhotdraw.draw.io.InputFormat;
import org.jhotdraw.draw.io.OutputFormat;
import org.jhotdraw.editor.DefaultDrawingEditor;
import org.jhotdraw.gui.JFileURIChooser;
import org.jhotdraw.samples.svg.action.CombineAction;
import org.jhotdraw.samples.svg.action.SplitAction;
import org.jhotdraw.samples.svg.action.ViewSourceAction;
import org.jhotdraw.samples.svg.figures.SVGGroupFigure;

/**
 * Provides meta-data and factory methods for an application.
 *
 * <p>See {@link ApplicationModel} on how this class interacts with an application.
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class SVGApplicationModel extends DefaultApplicationModel {

  private static final long serialVersionUID = 1L;

  /** Client property on the URIFileChooser. */
  public static final String INPUT_FORMAT_MAP_CLIENT_PROPERTY = "InputFormatMap";

  /** Client property on the URIFileChooser. */
  public static final String OUTPUT_FORMAT_MAP_CLIENT_PROPERTY = "OutputFormatMap";

  private static final double[] SCALE_FACTORS = {5, 4, 3, 2, 1.5, 1.25, 1, 0.75, 0.5, 0.25, 0.10};
  private GridConstrainer gridConstrainer;

  /** This editor is shared by all views. */
  private DefaultDrawingEditor sharedEditor;

  public SVGApplicationModel() {
    gridConstrainer = new GridConstrainer(12, 12);
  }

  public DefaultDrawingEditor getSharedEditor() {
    if (sharedEditor == null) {
      sharedEditor = new DefaultDrawingEditor();
    }
    return sharedEditor;
  }

  @Override
  public void initView(Application a, View view) {
    SVGView v = (SVGView) view;
    DrawingEditor editor;
    if (a.isSharingToolsAmongViews()) {
      v.setEditor(editor = getSharedEditor());
    } else {
      v.setEditor(editor = new DefaultDrawingEditor());
    }
    AbstractSelectedAction action;
    ActionMap m = view.getActionMap();
    m.put(SelectSameAction.ID, new SelectSameAction(editor));
    m.put(GroupAction.ID, new GroupAction(editor, new SVGGroupFigure()));
    m.put(UngroupAction.ID, new UngroupAction(editor, new SVGGroupFigure()));
    m.put(CombineAction.ID, new CombineAction(editor));
    m.put(SplitAction.ID, new SplitAction(editor));
    m.put(BringToFrontAction.ID, new BringToFrontAction(editor));
    m.put(SendToBackAction.ID, new SendToBackAction(editor));
    // view.addDisposable(action);
  }

  @Override
  public ActionMap createActionMap(Application a, View view) {
    SVGView v = (SVGView) view;
    ActionMap m = super.createActionMap(a, v);
    AbstractAction aa;
    m.put(ClearSelectionAction.ID, new ClearSelectionAction());
    m.put(ViewSourceAction.ID, new ViewSourceAction(a, v));
    m.put(ExportFileAction.ID, new ExportFileAction(a, v));
    if (v instanceof SVGView) {
      SVGView svgView = v;
      m.put(UndoAction.ID, svgView.getUndoManager().getUndoAction());
      m.put(RedoAction.ID, svgView.getUndoManager().getRedoAction());
    }
    DrawingEditor editor;
    if (a.isSharingToolsAmongViews()) {
      editor = getSharedEditor();
    } else {
      editor = (v == null) ? null : v.getEditor();
    }
    m.put(SelectSameAction.ID, new SelectSameAction(editor));
    m.put(GroupAction.ID, new GroupAction(editor, new SVGGroupFigure()));
    m.put(UngroupAction.ID, new UngroupAction(editor, new SVGGroupFigure()));
    m.put(CombineAction.ID, new CombineAction(editor));
    m.put(SplitAction.ID, new SplitAction(editor));
    m.put(BringToFrontAction.ID, new BringToFrontAction(editor));
    m.put(SendToBackAction.ID, new SendToBackAction(editor));
    return m;
  }

  /** Creates the MenuBuilder. */
  @Override
  protected MenuBuilder createMenuBuilder() {
    return new DefaultMenuBuilder() {
      @Override
      public void addSelectionItems(JMenu m, Application app, View v) {
        ActionMap am = app.getActionMap(v);
        super.addSelectionItems(m, app, v);
        m.add(am.get(SelectSameAction.ID));
      }

      @Override
      public void addOtherEditItems(JMenu m, Application app, View v) {
        ActionMap am = app.getActionMap(v);
        m.add(am.get(GroupAction.ID));
        m.add(am.get(UngroupAction.ID));
        m.add(am.get(CombineAction.ID));
        m.add(am.get(SplitAction.ID));
        m.addSeparator();
        m.add(am.get(BringToFrontAction.ID));
        m.add(am.get(SendToBackAction.ID));
      }

      @Override
      public void addOtherViewItems(JMenu m, Application app, View v) {
        ActionMap am = app.getActionMap(v);
        m.add(am.get(ViewSourceAction.ID));
      }
    };
  }

  /**
   * Overriden to create no toolbars.
   *
   * @param app
   * @param p
   * @return An empty list.
   */
  @Override
  public List<JToolBar> createToolBars(Application app, View p) {
    LinkedList<JToolBar> list = new LinkedList<JToolBar>();
    return list;
  }

  @Override
  public URIChooser createOpenChooser(Application a, View v) {
    final JFileURIChooser c = new JFileURIChooser();
    final HashMap<FileFilter, InputFormat> fileFilterInputFormatMap =
        new HashMap<FileFilter, InputFormat>();
    c.putClientProperty(INPUT_FORMAT_MAP_CLIENT_PROPERTY, fileFilterInputFormatMap);
    javax.swing.filechooser.FileFilter firstFF = null;
    if (v == null) {
      v = new SVGView();
    }
    Drawing d = ((SVGView) v).getDrawing();
    if (d == null) {
      d = ((SVGView) v).createDrawing();
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
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if ("fileFilterChanged".equals(evt.getPropertyName())) {
          InputFormat inputFormat = fileFilterInputFormatMap.get(evt.getNewValue());
          c.setAccessory(null);
        }
      }
    });
    return c;
  }

  @Override
  public URIChooser createSaveChooser(Application a, View v) {
    JFileURIChooser c = new JFileURIChooser();
    final HashMap<FileFilter, OutputFormat> fileFilterOutputFormatMap =
        new HashMap<FileFilter, OutputFormat>();
    c.putClientProperty(OUTPUT_FORMAT_MAP_CLIENT_PROPERTY, fileFilterOutputFormatMap);
    if (v == null) {
      v = new SVGView();
    }
    Drawing d = ((SVGView) v).getDrawing();
    for (OutputFormat format : d.getOutputFormats()) {
      javax.swing.filechooser.FileFilter ff = format.getFileFilter();
      fileFilterOutputFormatMap.put(ff, format);
      c.addChoosableFileFilter(ff);
      break; // only add the first uri filter
    }
    return c;
  }

  @Override
  public URIChooser createExportChooser(Application a, View v) {
    JFileURIChooser c = new JFileURIChooser();
    final HashMap<FileFilter, OutputFormat> fileFilterOutputFormatMap =
        new HashMap<FileFilter, OutputFormat>();
    c.putClientProperty("ffOutputFormatMap", fileFilterOutputFormatMap);
    if (v == null) {
      v = new SVGView();
    }
    Drawing d = ((SVGView) v).getDrawing();
    javax.swing.filechooser.FileFilter currentFilter = null;
    for (OutputFormat format : d.getOutputFormats()) {
      javax.swing.filechooser.FileFilter ff = format.getFileFilter();
      fileFilterOutputFormatMap.put(ff, format);
      c.addChoosableFileFilter(ff);
      // FIXME use preferences
      /*if (ff.getDescription().equals(preferences.get("viewExportFormat", ""))) {
      currentFilter = ff;
      }*/
    }
    if (currentFilter != null) {
      c.setFileFilter(currentFilter);
    }
    return c;
  }
}
