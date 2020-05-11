/*
 * @(#)DrawApplicationModel.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.draw;

import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jhotdraw.api.app.Application;
import org.jhotdraw.api.app.ApplicationModel;
import org.jhotdraw.api.app.View;
import org.jhotdraw.api.gui.URIChooser;
import org.jhotdraw.app.DefaultApplicationModel;
import org.jhotdraw.draw.figure.AbstractAttributedFigure;
import static org.jhotdraw.draw.AttributeKeys.*;
import org.jhotdraw.draw.figure.BezierFigure;
import org.jhotdraw.draw.figure.ConnectionFigure;
import org.jhotdraw.draw.DefaultDrawingEditor;
import org.jhotdraw.draw.figure.DiamondFigure;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.figure.EllipseFigure;
import org.jhotdraw.draw.figure.ImageFigure;
import org.jhotdraw.draw.figure.LineConnectionFigure;
import org.jhotdraw.draw.figure.LineFigure;
import org.jhotdraw.draw.figure.RectangleFigure;
import org.jhotdraw.draw.figure.RoundRectangleFigure;
import org.jhotdraw.draw.figure.TextAreaFigure;
import org.jhotdraw.draw.figure.TextFigure;
import org.jhotdraw.draw.figure.TriangleFigure;
import org.jhotdraw.draw.decoration.ArrowTip;
import org.jhotdraw.draw.liner.CurvedLiner;
import org.jhotdraw.draw.liner.ElbowLiner;
import org.jhotdraw.draw.tool.BezierTool;
import org.jhotdraw.draw.tool.ConnectionTool;
import org.jhotdraw.draw.tool.CreationTool;
import org.jhotdraw.draw.tool.ImageTool;
import org.jhotdraw.draw.tool.TextAreaCreationTool;
import org.jhotdraw.draw.tool.TextCreationTool;
import org.jhotdraw.gui.JFileURIChooser;
import org.jhotdraw.gui.action.ButtonFactory;
import org.jhotdraw.util.*;

/**
 * Provides factory methods for creating views, menu bars and toolbars.
 * <p>
 * See {@link ApplicationModel} on how this class interacts with an application.
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class DrawApplicationModel extends DefaultApplicationModel {

    private static final long serialVersionUID = 1L;
    /**
     * This editor is shared by all views.
     */
    private DefaultDrawingEditor sharedEditor;

    /**
     * Creates a new instance.
     */
    public DrawApplicationModel() {
    }

    public DefaultDrawingEditor getSharedEditor() {
        if (sharedEditor == null) {
            sharedEditor = new DefaultDrawingEditor();
        }
        return sharedEditor;
    }

    @Override
    public void initView(Application a, View p) {
        if (a.isSharingToolsAmongViews()) {
            ((DrawView) p).setEditor(getSharedEditor());
        }
    }

    /**
     * Creates toolbars for the application.
     * This class always returns an empty list. Subclasses may return other
     * values.
     */
    @Override
    public List<JToolBar> createToolBars(Application a, View pr) {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
        DrawView p = (DrawView) pr;
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
        tb.setName(labels.getString("window.drawToolBar.title"));
        list.add(tb);
        tb = new JToolBar();
        ButtonFactory.addAttributesButtonsTo(tb, editor);
        tb.setName(labels.getString("window.attributesToolBar.title"));
        list.add(tb);
        tb = new JToolBar();
        ButtonFactory.addAlignmentButtonsTo(tb, editor);
        tb.setName(labels.getString("window.alignmentToolBar.title"));
        list.add(tb);
        return list;
    }

    private void addCreationButtonsTo(JToolBar tb, DrawingEditor editor) {
        addDefaultCreationButtonsTo(tb, editor,
                ButtonFactory.createDrawingActions(editor),
                ButtonFactory.createSelectionActions(editor));
    }

    public void addDefaultCreationButtonsTo(JToolBar tb, final DrawingEditor editor,
            Collection<Action> drawingActions, Collection<Action> selectionActions) {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
        ButtonFactory.addSelectionToolTo(tb, editor, drawingActions, selectionActions);
        tb.addSeparator();
        AbstractAttributedFigure af;
        CreationTool ct;
        ConnectionTool cnt;
        ConnectionFigure lc;
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new RectangleFigure()), "edit.createRectangle", labels);
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new RoundRectangleFigure()), "edit.createRoundRectangle", labels);
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new EllipseFigure()), "edit.createEllipse", labels);
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new DiamondFigure()), "edit.createDiamond", labels);
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new TriangleFigure()), "edit.createTriangle", labels);
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new LineFigure()), "edit.createLine", labels);
        ButtonFactory.addToolTo(tb, editor, ct = new CreationTool(new LineFigure()), "edit.createArrow", labels);
        af = (AbstractAttributedFigure) ct.getPrototype();
        af.set(END_DECORATION, new ArrowTip(0.35, 12, 11.3));
        ButtonFactory.addToolTo(tb, editor, new ConnectionTool(new LineConnectionFigure()), "edit.createLineConnection", labels);
        ButtonFactory.addToolTo(tb, editor, cnt = new ConnectionTool(new LineConnectionFigure()), "edit.createElbowConnection", labels);
        lc = cnt.getPrototype();
        lc.setLiner(new ElbowLiner());
        ButtonFactory.addToolTo(tb, editor, cnt = new ConnectionTool(new LineConnectionFigure()), "edit.createCurvedConnection", labels);
        lc = cnt.getPrototype();
        lc.setLiner(new CurvedLiner());
        ButtonFactory.addToolTo(tb, editor, new BezierTool(new BezierFigure()), "edit.createScribble", labels);
        ButtonFactory.addToolTo(tb, editor, new BezierTool(new BezierFigure(true)), "edit.createPolygon", labels);
        ButtonFactory.addToolTo(tb, editor, new TextCreationTool(new TextFigure()), "edit.createText", labels);
        ButtonFactory.addToolTo(tb, editor, new TextAreaCreationTool(new TextAreaFigure()), "edit.createTextArea", labels);
        ButtonFactory.addToolTo(tb, editor, new ImageTool(new ImageFigure()), "edit.createImage", labels);
    }

    @Override
    public URIChooser createOpenChooser(Application a, View v) {
        JFileURIChooser c = new JFileURIChooser();
        c.addChoosableFileFilter(new FileNameExtensionFilter("Drawing .xml", "xml"));
        return c;
    }

    @Override
    public URIChooser createSaveChooser(Application a, View v) {
        JFileURIChooser c = new JFileURIChooser();
        c.addChoosableFileFilter(new FileNameExtensionFilter("Drawing .xml", "xml"));
        return c;
    }
}
