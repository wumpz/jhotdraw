/*
 * @(#)DrawApplicationModel.java  1.0  June 10, 2006
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

package org.jhotdraw.samples.draw;

import org.jhotdraw.util.*;
import java.util.*;
import javax.swing.*;
import org.jhotdraw.app.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.action.*;
import static org.jhotdraw.draw.AttributeKeys.*;
/**
 * DrawApplicationModel.
 * 
 * 
 * 
 * @author Werner Randelshofer.
 * @version 1.0 June 10, 2006 Created.
 */
public class DrawApplicationModel extends DefaultApplicationModel {
    /**
     * This editor is shared by all projects.
     */
    private DefaultDrawingEditor sharedEditor;
    
    /** Creates a new instance. */
    public DrawApplicationModel() {
    }
    
    public DefaultDrawingEditor getSharedEditor() {
        if (sharedEditor == null) {
            sharedEditor = new DefaultDrawingEditor();
        }
        return sharedEditor;
    }
    
    public void initProject(Application a, Project p) {
        if (a.isSharingToolsAmongProjects()) {
            ((DrawProject) p).setEditor(getSharedEditor());
        }
    }
    /**
     * Creates toolbars for the application.
     * This class always returns an empty list. Subclasses may return other
     * values.
     */
    public List<JToolBar> createToolBars(Application a, Project pr) {
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        DrawProject p = (DrawProject) pr;
        
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
        ToolBarButtonFactory.addAttributesButtonsTo(tb, editor);
        tb.setName(labels.getString("attributesToolBarTitle"));
        list.add(tb);
        tb = new JToolBar();
        ToolBarButtonFactory.addAlignmentButtonsTo(tb, editor);
        tb.setName(labels.getString("alignmentToolBarTitle"));
        list.add(tb);
        return list;
    }
    private void addCreationButtonsTo(JToolBar tb, DrawingEditor editor) {
        addDefaultCreationButtonsTo(tb, editor, 
                ToolBarButtonFactory.createDrawingActions(editor), 
                ToolBarButtonFactory.createSelectionActions(editor)
                );
    }
    public void addDefaultCreationButtonsTo(JToolBar tb, final DrawingEditor editor,
            Collection<Action> drawingActions, Collection<Action> selectionActions) {
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        
        ToolBarButtonFactory.addSelectionToolTo(tb, editor, drawingActions, selectionActions);
        tb.addSeparator();
        
        AttributedFigure af;
        CreationTool ct;
        ConnectionTool cnt;
        ConnectionFigure lc;
        
        ToolBarButtonFactory.addToolTo(tb, editor, new CreationTool(new RectangleFigure()), "createRectangle", labels);
        ToolBarButtonFactory.addToolTo(tb, editor, new CreationTool(new RoundRectangleFigure()), "createRoundRectangle", labels);
        ToolBarButtonFactory.addToolTo(tb, editor, new CreationTool(new EllipseFigure()), "createEllipse", labels);
        ToolBarButtonFactory.addToolTo(tb, editor, new CreationTool(new DiamondFigure()), "createDiamond", labels);
        ToolBarButtonFactory.addToolTo(tb, editor, new CreationTool(new TriangleFigure()), "createTriangle", labels);
        ToolBarButtonFactory.addToolTo(tb, editor, new CreationTool(new LineFigure()), "createLine", labels);
        ToolBarButtonFactory.addToolTo(tb, editor, ct = new CreationTool(new LineFigure()), "createArrow", labels);
       af = (AttributedFigure) ct.getPrototype();
        af.setAttribute(END_DECORATION, new ArrowTip(0.35, 12, 11.3));
        ToolBarButtonFactory.addToolTo(tb, editor, new ConnectionTool(new LineConnectionFigure()), "createLineConnection", labels);
        ToolBarButtonFactory.addToolTo(tb, editor, cnt = new ConnectionTool(new LineConnectionFigure()), "createElbowConnection", labels);
     lc =  cnt.getPrototype();
        lc.setLiner(new ElbowLiner());
        ToolBarButtonFactory.addToolTo(tb, editor, new BezierTool(new BezierFigure()), "createScribble", labels);
        ToolBarButtonFactory.addToolTo(tb, editor, new BezierTool(new BezierFigure(true)), "createPolygon", labels);
        ToolBarButtonFactory.addToolTo(tb, editor, new TextTool(new TextFigure()), "createText", labels);
        ToolBarButtonFactory.addToolTo(tb, editor, new TextAreaTool(new TextAreaFigure()), "createTextArea", labels);
    }    
}
