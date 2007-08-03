/*
 * @(#)ODGApplicationModel.java  1.0  January 15, 2007
 *
 * Copyright (c) 2006 Werner Randelshofer
 * Staldenmattweg 2, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Werner Randelshofer. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Werner Randelshofer.
 */

package org.jhotdraw.samples.odg;

import java.util.*;
import javax.swing.*;
import org.jhotdraw.app.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.*;
import org.jhotdraw.app.action.*;
import org.jhotdraw.samples.odg.action.*;
import org.jhotdraw.samples.odg.figures.*;
import org.jhotdraw.util.*;
import java.util.*;
import javax.swing.*;
import org.jhotdraw.app.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.action.*;
import static org.jhotdraw.samples.odg.ODGAttributeKeys.*;
/**
 * ODGApplicationModel.
 *
 * @author Werner Randelshofer
 * @version 1.0 January 15, 2007 Created.
 */
public class ODGApplicationModel extends DefaultApplicationModel {
    private final static double[] scaleFactors = {5, 4, 3, 2, 1.5, 1.25, 1, 0.75, 0.5, 0.25, 0.10};
   /**
     * This editor is shared by all projects.
     */
    private DefaultDrawingEditor sharedEditor;
    
    
    /** Creates a new instance. */
    public ODGApplicationModel() {
        setProjectClass(ODGProject.class);
    }
    public DefaultDrawingEditor getSharedEditor() {
        if (sharedEditor == null) {
            sharedEditor = new DefaultDrawingEditor();
        }
        return sharedEditor;
    }
     
    public static Collection<Action> createDrawingActions(DrawingEditor editor) {
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
        a.add(new GroupAction(editor, new ODGGroupFigure()));
        a.add(new UngroupAction(editor, new ODGGroupFigure()));
        a.add(new CombineAction(editor));
        a.add(new SplitAction(editor));
        
        a.add(null); // separator
        a.add(new MoveToFrontAction(editor));
        a.add(new MoveToBackAction(editor));
        
        return a;
    }
    private void addCreationButtonsTo(JToolBar tb, final DrawingEditor editor) {
        // AttributeKeys for the entitie sets
        HashMap<AttributeKey,Object> attributes;
        
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.samples.odg.Labels");
        ResourceBundleUtil drawLabels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        
        ButtonFactory.addSelectionToolTo(tb, editor, createDrawingActions(editor), createSelectionActions(editor));
        tb.addSeparator();
        
        attributes = new HashMap<AttributeKey,Object>();
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new ODGRectFigure(), attributes), "createRectangle", drawLabels);
        //ButtonFactory.addToolTo(tb, editor, new CreationTool(new ODGEllipseFigure(), attributes), "createEllipse", drawLabels);
        //ButtonFactory.addToolTo(tb, editor, new PathTool(new ODGPathFigure(), new ODGBezierFigure(true), attributes), "createPolygon", drawLabels);
        attributes = new HashMap<AttributeKey,Object>();
        attributes.put(AttributeKeys.FILL_COLOR, null);
        attributes.put(AttributeKeys.STROKE_COLOR, Color.black);
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new ODGPathFigure(), attributes), "createLine", drawLabels);
        //ButtonFactory.addToolTo(tb, editor, new PathTool(new ODGPathFigure(), new ODGBezierFigure(false), attributes), "createScribble", drawLabels);
        attributes = new HashMap<AttributeKey,Object>();
        attributes.put(AttributeKeys.FILL_COLOR, Color.black);
        attributes.put(AttributeKeys.STROKE_COLOR, null);
        //ButtonFactory.addToolTo(tb, editor, new TextTool(new ODGTextFigure(), attributes), "createText", drawLabels);
        //TextAreaTool tat = new TextAreaTool(new ODGTextAreaFigure(), attributes);
        //tat.setRubberbandColor(Color.BLACK);
        //ButtonFactory.addToolTo(tb, editor, tat, "createTextArea", drawLabels);
        attributes = new HashMap<AttributeKey,Object>();
        attributes.put(AttributeKeys.FILL_COLOR, null);
        attributes.put(AttributeKeys.STROKE_COLOR, null);
        //ButtonFactory.addToolTo(tb, editor, new ImageTool(new ODGImageFigure(), attributes), "createImage", drawLabels);
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
                "attributeStrokeColor", labels, 
                defaultAttributes
                )
                );
        defaultAttributes = new HashMap<AttributeKey,Object>();
        FILL_GRADIENT.set(defaultAttributes, (Gradient) null);
        bar.add(
                ButtonFactory.createEditorColorButton(editor,
                FILL_COLOR, ButtonFactory.WEBSAVE_COLORS, ButtonFactory.WEBSAVE_COLORS_COLUMN_COUNT,
                "attributeFillColor", labels, 
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
    /**
     * Creates toolbars for the application.
     */
    public java.util.List<JToolBar> createToolBars(Application a, Project pr) {
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        ODGProject p = (ODGProject) pr;
        
        DrawingEditor editor;
        if (p == null) {
            editor = getSharedEditor();
        } else {
            editor = p.getEditor();
        }
        
        LinkedList<JToolBar> list = new LinkedList<JToolBar>();
        JToolBar tb;
        tb = new JToolBar();
        addCreationButtonsTo(tb, editor);
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
    public void initProject(Application a, Project p) {
        if (a.isSharingToolsAmongProjects()) {
            ((ODGProject) p).setEditor(getSharedEditor());
        }
    }
    
    public void initApplication(Application a) {
        ResourceBundleUtil drawLabels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.samples.svg.Labels");
        AbstractAction aa;
        
        putAction(ExportAction.ID, new ExportAction(a));
        putAction("toggleGrid", aa = new ToggleProjectPropertyAction(a, "gridVisible"));
        drawLabels.configureAction(aa, "alignGrid");
        for (double sf : scaleFactors) {
            putAction((int) (sf*100)+"%",
                    aa = new ProjectPropertyAction(a, "scaleFactor", Double.TYPE, new Double(sf))
                    );
            aa.putValue(Action.NAME, (int) (sf*100)+" %");
            
        }
        putAction("togglePropertiesPanel", new TogglePropertiesPanelAction(a));
    }
}
