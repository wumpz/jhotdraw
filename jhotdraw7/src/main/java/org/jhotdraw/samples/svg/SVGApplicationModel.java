/*
 * @(#)SVGApplicationModel.java  1.0  June 10, 2006
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
 */

package org.jhotdraw.samples.svg;

import java.awt.*;
import org.jhotdraw.app.action.*;
import org.jhotdraw.samples.svg.action.*;
import org.jhotdraw.samples.svg.figures.*;
import org.jhotdraw.util.*;
import java.util.*;
import javax.swing.*;
import org.jhotdraw.app.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.action.*;
import static org.jhotdraw.draw.AttributeKeys.*;
/**
 * SVGApplicationModel.
 * 
 * @author Werner Randelshofer.
 * @version 1.0 June 10, 2006 Created.
 */
public class SVGApplicationModel extends DefaultApplicationModel {
    /**
     * This editor is shared by all projects.
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
    
    public void initProject(Application a, Project p) {
        if (a.isSharingToolsAmongProjects()) {
            ((SVGProject) p).setEditor(getSharedEditor());
        }
    }
    /**
     * Creates toolbars for the application.
     * This class always returns an empty list. Subclasses may return other
     * values.
     */
    public java.util.List<JToolBar> createToolBars(Application a, Project pr) {
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        SVGProject p = (SVGProject) pr;
        
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
        ToolBarButtonFactory.addAlignmentButtonsTo(tb, editor);
        tb.setName(labels.getString("alignmentToolBarTitle"));
        list.add(tb);
        return list;
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
        a.add(new GroupAction(editor, new SVGGroup()));
        a.add(new UngroupAction(editor, new SVGGroup()));
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
        
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.samples.svg.Labels");
        ResourceBundleUtil drawLabels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        
        ToolBarButtonFactory.addSelectionToolTo(tb, editor, createDrawingActions(editor), createSelectionActions(editor));
        tb.addSeparator();
        
        attributes = new HashMap<AttributeKey,Object>();
        attributes.put(AttributeKeys.FILL_COLOR, Color.white);
        attributes.put(AttributeKeys.STROKE_COLOR, Color.black);
        ToolBarButtonFactory.addToolTo(tb, editor, new CreationTool(new SVGRect(), attributes), "createRectangle", drawLabels);
        ToolBarButtonFactory.addToolTo(tb, editor, new CreationTool(new SVGEllipse(), attributes), "createEllipse", drawLabels);
        ToolBarButtonFactory.addToolTo(tb, editor, new PathTool(new SVGPath(), new BezierFigure(true), attributes), "createPolygon", drawLabels);
        attributes = new HashMap<AttributeKey,Object>();
        attributes.put(AttributeKeys.FILL_COLOR, null);
        attributes.put(AttributeKeys.STROKE_COLOR, Color.black);
        ToolBarButtonFactory.addToolTo(tb, editor, new CreationTool(new SVGLine(), attributes), "createLine", drawLabels);
        ToolBarButtonFactory.addToolTo(tb, editor, new PathTool(new SVGPath(), new BezierFigure(false), attributes), "createScribble", drawLabels);
        attributes = new HashMap<AttributeKey,Object>();
        attributes.put(AttributeKeys.FILL_COLOR, Color.black);
        attributes.put(AttributeKeys.STROKE_COLOR, null);
        ToolBarButtonFactory.addToolTo(tb, editor, new CreationTool(new SVGText(), attributes), "createText", drawLabels);
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
        ToolBarButtonFactory.addFontButtonsTo(bar, editor);
    }
    private void addColorButtonsTo(JToolBar bar, DrawingEditor editor) {
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        ToolBarButtonFactory.addColorButtonTo(bar, editor, STROKE_COLOR, ToolBarButtonFactory.DEFAULT_COLORS, 8, "attributeStrokeColor", labels);
        ToolBarButtonFactory.addColorButtonTo(bar, editor, FILL_COLOR, ToolBarButtonFactory.DEFAULT_COLORS, 8, "attributeFillColor", labels);
    }
    private void addStrokeButtonsTo(JToolBar bar, DrawingEditor editor) {
        ToolBarButtonFactory.addStrokeWidthButtonTo(bar, editor);
        ToolBarButtonFactory.addStrokeDashesButtonTo(bar, editor);
    }

}
