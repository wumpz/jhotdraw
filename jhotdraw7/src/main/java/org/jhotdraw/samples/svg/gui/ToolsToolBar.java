/*
 * @(#)DrawToolsPane.java  2.0  2008-04-06
 *
 * Copyright (c) 2007-2008 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */
package org.jhotdraw.samples.svg.gui;

import javax.swing.border.*;
import org.jhotdraw.gui.plaf.palette.*;
import org.jhotdraw.samples.svg.*;
import org.jhotdraw.util.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import org.jhotdraw.app.action.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.action.*;
import org.jhotdraw.samples.svg.action.*;
import org.jhotdraw.samples.svg.figures.*;
import static org.jhotdraw.samples.svg.SVGAttributeKeys.*;

/**
 * DrawToolsPane.
 *
 * @author Werner Randelshofer
 * @version 2.0 2008-04-06 Reworked. 
 * <br>1.0 May 1, 2007 Created.
 */
public class ToolsToolBar extends AbstractToolBar {

    /** Creates new instance. */
    public ToolsToolBar() {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.samples.svg.Labels");
        setName(labels.getString("tools.toolbar"));
    }

    @Override
    protected JComponent createDisclosedComponent(int state) {
        JPanel p = null;

        switch (state) {
            case 1:
               {
                  p = new JPanel();
                    p.setOpaque(false);
        p.setBorder(new EmptyBorder(5, 5, 5, 8));

        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.samples.svg.Labels");

        GridBagLayout layout = new GridBagLayout();
        p.setLayout(layout);
        GridBagConstraints gbc;
        AbstractButton btn;
        CreationTool creationTool;
        PathTool pathTool;
        TextCreationTool textTool;
        TextAreaCreationTool textAreaTool;
        SVGCreateFromFileTool imageTool;

        HashMap<AttributeKey, Object> attributes;
        btn = ButtonFactory.addSelectionToolTo(this, editor,
                ButtonFactory.createDrawingActions(editor),
                createSelectionActions(editor));
        btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        p.add(btn, gbc);
        labels.configureToolBarButton(btn, "selectionTool");

        attributes = new HashMap<AttributeKey, Object>();
        btn = ButtonFactory.addToolTo(this, editor, creationTool = new CreationTool(new SVGRectFigure(), attributes), "createRectangle", labels);
        creationTool.setToolDoneAfterCreation(false);
        btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(3, 0, 0, 0);
        p.add(btn, gbc);

        btn = ButtonFactory.addToolTo(this, editor, creationTool = new CreationTool(new SVGEllipseFigure(), attributes), "createEllipse", labels);
        creationTool.setToolDoneAfterCreation(false);
        btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(3, 3, 0, 0);
        p.add(btn, gbc);

        btn = ButtonFactory.addToolTo(this, editor, pathTool = new PathTool(new SVGPathFigure(), new SVGBezierFigure(true), attributes), "createPolygon", labels);
        pathTool.setToolDoneAfterCreation(false);
        btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.insets = new Insets(3, 3, 0, 0);
        p.add(btn, gbc);

        attributes = new HashMap<AttributeKey, Object>();
        attributes.put(AttributeKeys.FILL_COLOR, null);
        attributes.put(CLOSED, false);
        btn = ButtonFactory.addToolTo(this, editor, creationTool = new CreationTool(new SVGPathFigure(), attributes), "createLine", labels);
        creationTool.setToolDoneAfterCreation(false);
        btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 3, 0, 0);
        p.add(btn, gbc);

        btn = ButtonFactory.addToolTo(this, editor, pathTool = new PathTool(new SVGPathFigure(), new SVGBezierFigure(false), attributes), "createScribble", labels);
        pathTool.setToolDoneAfterCreation(false);
        btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 3, 0, 0);
        p.add(btn, gbc);

        attributes = new HashMap<AttributeKey, Object>();
        attributes.put(AttributeKeys.FILL_COLOR, Color.black);
        attributes.put(AttributeKeys.STROKE_COLOR, null);
        btn = ButtonFactory.addToolTo(this, editor, textTool = new TextCreationTool(new SVGTextFigure(), attributes), "createText", labels);
        textTool.setToolDoneAfterCreation(true);
        btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(3, 0, 0, 0);
        p.add(btn, gbc);

        textAreaTool = new TextAreaCreationTool(new SVGTextAreaFigure(), attributes);
        textAreaTool.setRubberbandColor(Color.BLACK);
        textAreaTool.setToolDoneAfterCreation(true);
        btn = ButtonFactory.addToolTo(this, editor, textAreaTool, "createTextArea", labels);
        btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.insets = new Insets(3, 3, 0, 0);
        p.add(btn, gbc);

        attributes = new HashMap<AttributeKey, Object>();
        attributes.put(AttributeKeys.FILL_COLOR, null);
        attributes.put(AttributeKeys.STROKE_COLOR, null);
        btn = ButtonFactory.addToolTo(this, editor, imageTool = new SVGCreateFromFileTool(new SVGImageFigure(), new SVGGroupFigure(), attributes), "createImage", labels);
        imageTool.setToolDoneAfterCreation(true);
        imageTool.setUseFileDialog(true);
        btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.insets = new Insets(3, 3, 0, 0);
        p.add(btn, gbc);
                }
                break;
        }
        return p;
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
    protected String getID() {
        return "tools";
    }
    protected int getDefaultDisclosureState() {
        return 1;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setOpaque(false);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
