/*
 * @(#)SVGApplicationModel.java
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
 */
package org.jhotdraw.samples.svg;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.jhotdraw.app.action.file.ExportFileAction;
import org.jhotdraw.app.action.edit.PasteAction;
import org.jhotdraw.app.action.edit.CutAction;
import org.jhotdraw.app.action.edit.CopyAction;
import org.jhotdraw.app.action.edit.DuplicateAction;
import org.jhotdraw.app.action.edit.ClearSelectionAction;
import org.jhotdraw.app.action.edit.SelectAllAction;
import org.jhotdraw.samples.svg.action.*;
import org.jhotdraw.samples.svg.figures.*;
import org.jhotdraw.util.*;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import org.jhotdraw.app.*;
import org.jhotdraw.app.action.edit.RedoAction;
import org.jhotdraw.app.action.edit.UndoAction;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.action.*;
import org.jhotdraw.draw.io.InputFormat;
import org.jhotdraw.draw.io.OutputFormat;
import org.jhotdraw.gui.JFileURIChooser;
import org.jhotdraw.gui.URIChooser;

/**
 * SVGApplicationModel.
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class SVGApplicationModel extends DefaultApplicationModel {

    /** Client property on the URIFileChooser. */
    public final static String INPUT_FORMAT_MAP_CLIENT_PROPERTY = "InputFormatMap";
    /** Client property on the URIFileChooser. */
    public final static String OUTPUT_FORMAT_MAP_CLIENT_PROPERTY = "OutputFormatMap";
    private final static double[] scaleFactors = {5, 4, 3, 2, 1.5, 1.25, 1, 0.75, 0.5, 0.25, 0.10};
    private GridConstrainer gridConstrainer;
    /**
     * This editor is shared by all views.
     */
    private DefaultDrawingEditor sharedEditor;

    /** Creates a new instance. */
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
        if (a.isSharingToolsAmongViews()) {
            v.setEditor(getSharedEditor());
        } else {
            v.setEditor(new DefaultDrawingEditor());
        }

        AbstractSelectedAction action;
        view.getActionMap().put(SelectSameAction.ID, action = new SelectSameAction(v.getEditor()));
        view.addDisposable(action);
    }

    @Override
    public ActionMap createActionMap(Application a, View v) {
        ActionMap m = super.createActionMap(a, v);
        ResourceBundleUtil drawLabels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.samples.svg.Labels");
        AbstractAction aa;

        m.put(ClearSelectionAction.ID, new ClearSelectionAction());
        m.put(ViewSourceAction.ID, new ViewSourceAction(a, v));
        m.put(ExportFileAction.ID, new ExportFileAction(a, v));
        if (v instanceof SVGView) {
            SVGView svgView=(SVGView)v;
            m.put(UndoAction.ID, svgView.getUndoManager().getUndoAction());
            m.put(RedoAction.ID, svgView.getUndoManager().getRedoAction());
        }
        return m;
    }

    public Collection<Action> createDrawingActions(Application app, DrawingEditor editor) {
        LinkedList<Action> a = new LinkedList<Action>();
        a.add(new CutAction());
        a.add(new CopyAction());
        a.add(new PasteAction());
        a.add(new SelectAllAction());
        a.add(new ClearSelectionAction());
        a.add(new SelectSameAction(editor));
        return a;
    }

    public static Collection<Action> createSelectionActions(DrawingEditor editor) {
        LinkedList<Action> a = new LinkedList<Action>();
        a.add(new DuplicateAction());

        a.add(null); // separator
        a.add(new GroupAction(editor, new SVGGroupFigure()));
        a.add(new UngroupAction(editor, new SVGGroupFigure()));
        a.add(new CombineAction(editor));
        a.add(new SplitAction(editor));

        a.add(null); // separator
        a.add(new BringToFrontAction(editor));
        a.add(new SendToBackAction(editor));

        return a;
    }

    @Override
    public java.util.List<JMenu> createMenus(Application a, View pr) {
        LinkedList<JMenu> mb = new LinkedList<JMenu>();
        mb.add(createEditMenu(a, pr));
        mb.add(createViewMenu(a, pr));
        return mb;
    }

    @Override
    protected JMenu createViewMenu(Application a, View v) {
        JMenu m, m2;
        JMenuItem mi;
        JRadioButtonMenuItem rbmi;
        JCheckBoxMenuItem cbmi;
        ButtonGroup group;
        Action action;

        ResourceBundleUtil appLabels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
        ResourceBundleUtil drawLabels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
        ResourceBundleUtil svgLabels = ResourceBundleUtil.getBundle("org.jhotdraw.samples.svg.Labels");

        m = new JMenu();
        appLabels.configureMenu(m, "view");
        ActionMap am = a.getActionMap(v);
        m.add(am.get(ViewSourceAction.ID));

        return m;
    }

    @Override
    protected JMenu createEditMenu(Application a, View v) {
        ResourceBundleUtil appLabels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
        ResourceBundleUtil drawLabels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");

        JMenu m = a.createEditMenu(v);
        if (m == null) {
            m = new JMenu();
            appLabels.configureMenu(m, "edit");
        }
        JMenuItem mi;

        ActionMap am = a.getActionMap(v);
        mi = m.add(am.get(SelectSameAction.ID));
        mi.setIcon(null);
        return m;
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
                if (evt.getPropertyName().equals("fileFilterChanged")) {
                    InputFormat inputFormat = fileFilterInputFormatMap.get(evt.getNewValue());
                    c.setAccessory((inputFormat == null) ? null : inputFormat.getInputFormatAccessory());
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
