/*
 * @(#)DrawToolsPane.java
 *
 * Copyright (c) 2007-2008 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.svg.gui;

import static org.jhotdraw.draw.AttributeKeys.PATH_CLOSED;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import org.jhotdraw.action.edit.DuplicateAction;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.action.*;
import org.jhotdraw.draw.tool.CreationTool;
import org.jhotdraw.draw.tool.TextAreaCreationTool;
import org.jhotdraw.draw.tool.TextCreationTool;
import org.jhotdraw.gui.action.ButtonFactory;
import org.jhotdraw.gui.plaf.palette.PaletteButtonUI;
import org.jhotdraw.samples.svg.PathTool;
import org.jhotdraw.samples.svg.SVGCreateFromFileTool;
import org.jhotdraw.samples.svg.action.CombineAction;
import org.jhotdraw.samples.svg.action.SplitAction;
import org.jhotdraw.samples.svg.figures.SVGBezierFigure;
import org.jhotdraw.samples.svg.figures.SVGEllipseFigure;
import org.jhotdraw.samples.svg.figures.SVGGroupFigure;
import org.jhotdraw.samples.svg.figures.SVGImageFigure;
import org.jhotdraw.samples.svg.figures.SVGPathFigure;
import org.jhotdraw.samples.svg.figures.SVGRectFigure;
import org.jhotdraw.samples.svg.figures.SVGTextAreaFigure;
import org.jhotdraw.samples.svg.figures.SVGTextFigure;
import org.jhotdraw.util.*;

/** DrawToolsPane. */
public class ToolsToolBar extends AbstractToolBar {

  private static final long serialVersionUID = 1L;

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
        p = new JPanel();
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(5, 5, 5, 8));
        // Abort if no editor is set
        if (editor == null) {
          break;
        }
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
        HashMap<AttributeKey<?>, Object> attributes;
        btn = ButtonFactory.addSelectionToolTo(
            this,
            editor,
            ButtonFactory.createDrawingActions(editor, disposables),
            createSelectionActions(editor));
        btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
        btn.addMouseListener(new SelectionToolButtonHandler(editor));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        p.add(btn, gbc);
        labels.configureToolBarButton(btn, "selectionTool");
        attributes = new HashMap<AttributeKey<?>, Object>();
        btn = ButtonFactory.addToolTo(
            this,
            editor,
            creationTool = new CreationTool(new SVGRectFigure(), attributes),
            "createRectangle",
            labels);
        creationTool.setToolDoneAfterCreation(false);
        btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(3, 0, 0, 0);
        p.add(btn, gbc);
        btn = ButtonFactory.addToolTo(
            this,
            editor,
            creationTool = new CreationTool(new SVGEllipseFigure(), attributes),
            "createEllipse",
            labels);
        creationTool.setToolDoneAfterCreation(false);
        btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(3, 3, 0, 0);
        p.add(btn, gbc);
        btn = ButtonFactory.addToolTo(
            this,
            editor,
            pathTool = new PathTool(new SVGPathFigure(), new SVGBezierFigure(true), attributes),
            "createPolygon",
            labels);
        pathTool.setToolDoneAfterCreation(false);
        btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.insets = new Insets(3, 3, 0, 0);
        p.add(btn, gbc);
        attributes = new HashMap<AttributeKey<?>, Object>();
        attributes.put(AttributeKeys.FILL_COLOR, null);
        attributes.put(PATH_CLOSED, false);
        btn = ButtonFactory.addToolTo(
            this,
            editor,
            creationTool = new CreationTool(new SVGPathFigure(), attributes),
            "createLine",
            labels);
        creationTool.setToolDoneAfterCreation(false);
        btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 3, 0, 0);
        p.add(btn, gbc);
        btn = ButtonFactory.addToolTo(
            this,
            editor,
            pathTool = new PathTool(new SVGPathFigure(), new SVGBezierFigure(false), attributes),
            "createScribble",
            labels);
        pathTool.setToolDoneAfterCreation(false);
        btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 3, 0, 0);
        p.add(btn, gbc);
        attributes = new HashMap<AttributeKey<?>, Object>();
        attributes.put(AttributeKeys.FILL_COLOR, Color.black);
        attributes.put(AttributeKeys.STROKE_COLOR, null);
        btn = ButtonFactory.addToolTo(
            this,
            editor,
            textTool = new TextCreationTool(new SVGTextFigure(), attributes),
            "createText",
            labels);
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
        attributes = new HashMap<AttributeKey<?>, Object>();
        attributes.put(AttributeKeys.FILL_COLOR, null);
        attributes.put(AttributeKeys.STROKE_COLOR, null);
        btn = ButtonFactory.addToolTo(
            this,
            editor,
            imageTool =
                new SVGCreateFromFileTool(new SVGImageFigure(), new SVGGroupFigure(), attributes),
            "createImage",
            labels);
        imageTool.setToolDoneAfterCreation(true);
        imageTool.setUseFileDialog(true);
        btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.insets = new Insets(3, 3, 0, 0);
        p.add(btn, gbc);
        break;
    }
    return p;
  }

  public Collection<Action> createSelectionActions(DrawingEditor editor) {
    LinkedList<Action> list = new LinkedList<Action>();
    AbstractSelectedAction a;
    list.add(new DuplicateAction());
    list.add(null); // separator
    list.add(a = new GroupAction(editor, new SVGGroupFigure()));
    disposables.add(a);
    list.add(a = new UngroupAction(editor, new SVGGroupFigure()));
    disposables.add(a);
    list.add(a = new CombineAction(editor));
    disposables.add(a);
    list.add(a = new SplitAction(editor));
    disposables.add(a);
    list.add(null); // separator
    list.add(a = new BringToFrontAction(editor));
    disposables.add(a);
    list.add(a = new SendToBackAction(editor));
    disposables.add(a);
    return list;
  }

  @Override
  protected String getID() {
    return "tools";
  }

  @Override
  protected int getDefaultDisclosureState() {
    return 1;
  }

  private static class SelectionToolButtonHandler extends MouseAdapter {

    private DrawingEditor editor;
    private boolean wasSelectedOnPressed = false;

    public SelectionToolButtonHandler(DrawingEditor editor) {
      this.editor = editor;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
      if (wasSelectedOnPressed) {
        DrawingView view = editor.getActiveView();
        if (view != null) {
          view.setHandleDetailLevel(view.getHandleDetailLevel() + 1);
        }
      }
    }

    @Override
    public void mousePressed(MouseEvent e) {
      // Note: we blindly assume here that selection changes occur on mouse release!!
      wasSelectedOnPressed = ((AbstractButton) e.getSource()).isSelected();
    }
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT
   * modify this code. The content of this method is always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    setOpaque(false);
  } // </editor-fold>//GEN-END:initComponents
  // Variables declaration - do not modify//GEN-BEGIN:variables
  // End of variables declaration//GEN-END:variables
}
