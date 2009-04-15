/*
 * @(#)AlignToolBar.java  1.2  2008-05-23
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

import java.beans.*;
import java.util.prefs.*;
import javax.swing.border.*;
import org.jhotdraw.gui.*;
import org.jhotdraw.samples.svg.*;
import org.jhotdraw.undo.*;
import org.jhotdraw.util.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import org.jhotdraw.app.action.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.action.*;
import org.jhotdraw.gui.plaf.palette.*;
import org.jhotdraw.samples.svg.action.*;
import org.jhotdraw.samples.svg.figures.*;
import static org.jhotdraw.samples.svg.SVGAttributeKeys.*;

/**
 * AlignToolBar.
 *
 * @author Werner Randelshofer
 * @version 1.2 2008-05-23 Hide the toolbar if nothing is selected, and no
 * creation tool is active. 
 * <br>1.1 2008-03-26 Don't draw button borders. 
 * <br>1.0 May 1, 2007 Created.
 */
public class AlignToolBar extends AbstractToolBar {

    private SelectionComponentDisplayer displayer;

    /** Creates new instance. */
    public AlignToolBar() {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.samples.svg.Labels");
        setName(labels.getString(getID() + ".toolbar"));
    }

    @Override
    public void setEditor(DrawingEditor newValue) {
        DrawingEditor oldValue = getEditor();
        if (displayer != null) {
            displayer.dispose();
            displayer = null;
        }
        super.setEditor(newValue);
        if (newValue != null) {
            displayer = new SelectionComponentDisplayer(editor, this);
            displayer.setMinSelectionCount(2);
            displayer.setVisibleIfCreationTool(false);
        }
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

                    gbc = new GridBagConstraints();
                    gbc.gridy = 0;
                    btn = new JButton(new AlignAction.West(editor, labels));
                    btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
                    btn.setText(null);
                    p.add(btn, gbc);

                    gbc.insets = new Insets(0, 3, 0, 0);
                    btn = new JButton(new AlignAction.East(editor, labels));
                    btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
                    btn.putClientProperty("hideActionText", Boolean.TRUE);
                    btn.setText(null);
                    p.add(btn, gbc);

                    gbc.gridy = 1;
                    gbc.insets = new Insets(3, 0, 0, 0);
                    btn = new JButton(new AlignAction.North(editor, labels));
                    btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
                    btn.putClientProperty("hideActionText", Boolean.TRUE);
                    btn.setText(null);
                    p.add(btn, gbc);

                    gbc.insets = new Insets(3, 3, 0, 0);
                    btn = new JButton(new AlignAction.South(editor, labels));
                    btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
                    btn.putClientProperty("hideActionText", Boolean.TRUE);
                    btn.setText(null);
                    p.add(btn, gbc);

                    gbc.gridx = 0;
                    gbc.gridy = 2;
                    gbc.insets = new Insets(3, 0, 0, 0);
                    btn = new JButton(new AlignAction.Horizontal(editor, labels));
                    btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
                    btn.putClientProperty("hideActionText", Boolean.TRUE);
                    btn.setText(null);
                    p.add(btn, gbc);

                    gbc.gridx = 1;
                    gbc.insets = new Insets(3, 3, 0, 0);
                    btn = new JButton(new AlignAction.Vertical(editor, labels));
                    btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
                    btn.putClientProperty("hideActionText", Boolean.TRUE);
                    btn.setText(null);
                    p.add(btn, gbc);
                }
                break;
        }
        return p;
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

    @Override
    protected String getID() {
        return "align";
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
