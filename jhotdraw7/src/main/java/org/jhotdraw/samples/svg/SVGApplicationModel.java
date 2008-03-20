/*
 * @(#)SVGApplicationModel.java  1.0  June 10, 2006
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.*;
import org.jhotdraw.app.action.*;
import org.jhotdraw.samples.svg.action.*;
import org.jhotdraw.samples.svg.figures.*;
import org.jhotdraw.util.*;
import java.util.*;
import javax.swing.*;
import org.jhotdraw.app.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.action.*;
import static org.jhotdraw.samples.svg.SVGAttributeKeys.*;
/**
 * SVGApplicationModel.
 *
 * @author Werner Randelshofer.
 * @version 1.0 June 10, 2006 Created.
 */
public class SVGApplicationModel extends DefaultApplicationModel {
    private final static double[] scaleFactors = {5, 4, 3, 2, 1.5, 1.25, 1, 0.75, 0.5, 0.25, 0.10};
    
    private GridConstrainer gridConstrainer;
    
    /**
     * This editor is shared by all views.
     */
    private DefaultDrawingEditor sharedEditor;
    
    /** Creates a new instance. */
    public SVGApplicationModel() {
    }
    
    public DefaultDrawingEditor getSharedEditor() {
        if (sharedEditor == null) {
            sharedEditor = new DefaultDrawingEditor();
        }
        return sharedEditor;
    }
    
    public void initView(Application a, View p) {
        if (a.isSharingToolsAmongViews()) {
            ((SVGView) p).setEditor(getSharedEditor());
        }
        p.putAction(EditGridAction.ID, getAction(EditGridAction.ID));
        p.putAction(SelectSameAction.ID, new SelectSameAction(((SVGView) p).getEditor()));
    }
    
    @Override public void initApplication(Application a) {
        ResourceBundleUtil drawLabels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.samples.svg.Labels");
        AbstractAction aa;
        
        gridConstrainer = new GridConstrainer(12, 12);
        
        putAction(ViewSourceAction.ID, new ViewSourceAction(a));
        putAction(ExportAction.ID, new ExportAction(a));
        putAction(ToggleGridAction.ID, new ToggleGridAction(getSharedEditor()));
        putAction(EditGridAction.ID, new EditGridAction(a, getSharedEditor()));
        putAction(EditDrawingAction.ID, new EditDrawingAction(a, getSharedEditor()));
        for (double sf : scaleFactors) {
            putAction((int) (sf*100)+"%",
                    aa = new ViewPropertyAction(a, "scaleFactor", Double.TYPE, new Double(sf))
                    );
            aa.putValue(Action.NAME, (int) (sf*100)+" %");
            
        }
        
        putAction("togglePropertiesPanel", new TogglePropertiesPanelAction(a));
    }
    /**
     * Creates toolbars for the application.
     */
    public java.util.List<JToolBar> createToolBars(Application a, View pr) {
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        SVGView p = (SVGView) pr;
        
        DrawingEditor editor;
        if (p == null) {
            editor = getSharedEditor();
        } else {
            editor = p.getEditor();
        }
        
        LinkedList<JToolBar> list = new LinkedList<JToolBar>();
        JToolBar tb;
        tb = new JToolBar();
        addCreationButtonsTo(a, tb, editor);
        tb.setName(labels.getString("drawToolBarTitle"));
        list.add(tb);
        tb = new JToolBar();
        addAttributesButtonsTo(tb, editor);
        tb.setName(labels.getString("attributesToolBarTitle"));
        list.add(tb);
        tb = new JToolBar();
        ButtonFactory.addAlignmentButtonsTo(tb, editor);
        tb.setName(labels.getString("alignmentToolBarTitle"));
        list.add(tb);
        return list;
    }
    
    public  Collection<Action> createDrawingActions(Application app, DrawingEditor editor) {
        LinkedList<Action> a = new LinkedList<Action>();
        a.add(new CutAction());
        a.add(new CopyAction());
        a.add(new PasteAction());
        a.add(new SelectAllAction());
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
        a.add(new MoveToFrontAction(editor));
        a.add(new MoveToBackAction(editor));
        
        return a;
    }
    private void addCreationButtonsTo(Application a, JToolBar tb, final DrawingEditor editor) {
        // AttributeKeys for the entitie sets
        HashMap<AttributeKey,Object> attributes;
        
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.samples.svg.Labels");
        ResourceBundleUtil drawLabels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        
        ButtonFactory.addSelectionToolTo(tb, editor, createDrawingActions(a, editor), createSelectionActions(editor));
        tb.addSeparator();
        
        attributes = new HashMap<AttributeKey,Object>();
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new SVGRectFigure(), attributes), "createRectangle", drawLabels);
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new SVGEllipseFigure(), attributes), "createEllipse", drawLabels);
        ButtonFactory.addToolTo(tb, editor, new PathTool(new SVGPathFigure(), new SVGBezierFigure(true), attributes), "createPolygon", drawLabels);
        attributes = new HashMap<AttributeKey,Object>();
        attributes.put(AttributeKeys.FILL_COLOR, null);
        attributes.put(AttributeKeys.STROKE_COLOR, Color.black);
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new SVGPathFigure(), attributes), "createLine", drawLabels);
        ButtonFactory.addToolTo(tb, editor, new PathTool(new SVGPathFigure(), new SVGBezierFigure(false), attributes), "createScribble", drawLabels);
        attributes = new HashMap<AttributeKey,Object>();
        attributes.put(AttributeKeys.FILL_COLOR, Color.black);
        attributes.put(AttributeKeys.STROKE_COLOR, null);
        ButtonFactory.addToolTo(tb, editor, new TextTool(new SVGTextFigure(), attributes), "createText", drawLabels);
        TextAreaTool tat = new TextAreaTool(new SVGTextAreaFigure(), attributes);
        tat.setRubberbandColor(Color.BLACK);
        ButtonFactory.addToolTo(tb, editor, tat, "createTextArea", drawLabels);
        attributes = new HashMap<AttributeKey,Object>();
        attributes.put(AttributeKeys.FILL_COLOR, null);
        attributes.put(AttributeKeys.STROKE_COLOR, null);
        ButtonFactory.addToolTo(tb, editor, new ImageTool(new SVGImageFigure(), attributes), "createImage", drawLabels);
    }
    /**
     * Creates toolbar buttons and adds them to the specified JToolBar
     */
    private void addAttributesButtonsTo(JToolBar bar, DrawingEditor editor) {
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        JButton b;
        
        b = bar.add(new PickAttributesAction(editor));
        b.setFocusable(false);
        b = bar.add(new ApplyAttributesAction(editor));
        b.setFocusable(false);
        bar.addSeparator();
        
        addColorButtonsTo(bar, editor);
        bar.addSeparator();
        addStrokeButtonsTo(bar, editor);
        bar.addSeparator();
        ButtonFactory.addFontButtonsTo(bar, editor);
    }
    private void addColorButtonsTo(JToolBar bar, DrawingEditor editor) {
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        HashMap<AttributeKey,Object> defaultAttributes = new HashMap<AttributeKey,Object>();
        STROKE_GRADIENT.set(defaultAttributes, (Gradient) null);
        bar.add(
                ButtonFactory.createEditorColorButton(editor,
                STROKE_COLOR, ButtonFactory.WEBSAVE_COLORS, ButtonFactory.WEBSAVE_COLORS_COLUMN_COUNT,
                "attribute.strokeColor", labels,
                defaultAttributes
                )
                );
        defaultAttributes = new HashMap<AttributeKey,Object>();
        FILL_GRADIENT.set(defaultAttributes, (Gradient) null);
        bar.add(
                ButtonFactory.createEditorColorButton(editor,
                FILL_COLOR, ButtonFactory.WEBSAVE_COLORS, ButtonFactory.WEBSAVE_COLORS_COLUMN_COUNT,
                "attribute.fillColor", labels,
                defaultAttributes
                )
                );
    }
    private void addStrokeButtonsTo(JToolBar bar, DrawingEditor editor) {
        bar.add(ButtonFactory.createStrokeWidthButton(editor));
        bar.add(ButtonFactory.createStrokeDashesButton(editor));
        bar.add(ButtonFactory.createStrokeCapButton(editor));
        bar.add(ButtonFactory.createStrokeJoinButton(editor));
    }
    
     @Override public java.util.List<JMenu> createMenus(Application a, View pr) {
        // FIXME - Add code for unconfiguring the menus!! We leak memory!
        SVGView p = (SVGView) pr;
        ResourceBundleUtil appLabels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.app.Labels");
        ResourceBundleUtil drawLabels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        
        //  JMenuBar mb = new JMenuBar();
        LinkedList<JMenu> mb =  new LinkedList<JMenu>();
        JMenu m, m2;
        JMenuItem mi;
        JRadioButtonMenuItem rbmi;
        JCheckBoxMenuItem cbmi;
        ButtonGroup group;
        
        mb.add(createEditMenu(a, pr));
        
        m = new JMenu();
        appLabels.configureMenu(m, "view");
        m.add(getAction(EditDrawingAction.ID));
        m2 = new JMenu();
        drawLabels.configureMenu(m2, "grid");
        cbmi = new JCheckBoxMenuItem(getAction(ToggleGridAction.ID));
        Actions.configureJCheckBoxMenuItem(cbmi, getAction(ToggleGridAction.ID));
        m2.add(cbmi);
        cbmi.setIcon(null);
        m2.add(getAction(EditGridAction.ID));
        m.add(m2);
        m2 = new JMenu();
        drawLabels.configureMenu(m2, "zoom");
        for (double sf : scaleFactors) {
            String id = (int) (sf*100)+"%";
            cbmi = new JCheckBoxMenuItem(getAction(id));
            Actions.configureJCheckBoxMenuItem(cbmi, getAction(id));
            m2.add(cbmi);
        }
        m.add(m2);
        cbmi = new JCheckBoxMenuItem(getAction("togglePropertiesPanel"));
        m.add(getAction(ViewSourceAction.ID));
        m.add(cbmi);
        
        mb.add(m);
        
        return mb;
    }
    @Override protected JMenu createEditMenu(Application a, View p) {
        ResourceBundleUtil drawLabels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        
        JMenu m = super.createEditMenu(a, p);
        JMenuItem mi;
        if (p != null) {
            mi = m.add(p.getAction(SelectSameAction.ID));
        } else {
            mi = new JMenuItem();
            drawLabels.configureMenu(mi, SelectSameAction.ID);
            mi.setEnabled(false);
            m.add(mi);
        }
        mi.setIcon(null);
        return m;
    }
}
