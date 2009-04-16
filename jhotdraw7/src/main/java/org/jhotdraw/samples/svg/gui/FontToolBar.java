/*
 * @(#)StrokeToolBar.java  1.2  2008-05-23
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
import org.jhotdraw.util.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.action.*;
import org.jhotdraw.gui.JFontChooser;
import org.jhotdraw.gui.plaf.palette.*;

/**
 * StrokeToolBar.
 * 
 * @author Werner Randelshofer
 * @version 1.2 2008-05-23 Hide the toolbar if nothing is selected, and no
 * creation tool is active. 
 * <br>1.1 2008-03-26 Don't draw button borders. 
 * <br>1.0 May 1, 2007 Created.
 */
public class FontToolBar extends AbstractToolBar {

    private SelectionComponentDisplayer displayer;

    /** Creates new instance. */
    public FontToolBar() {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.samples.svg.Labels");
        setName(labels.getString("font.toolbar"));
        JFontChooser.loadAllFonts();
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
            displayer = new SelectionComponentDisplayer(editor, this) {

                @Override
                public void updateVisibility() {
                    boolean newValue = editor != null &&
                            editor.getActiveView() != null &&
                            (isVisibleIfCreationTool && ((editor.getTool() instanceof TextCreationTool) || editor.getTool() instanceof TextAreaCreationTool) ||
                            containsTextHolderFigure(editor.getActiveView().getSelectedFigures())
                            );
                    component.setVisible(newValue);

                    // The following is needed to trick BoxLayout
                    if (newValue) {
                        component.setPreferredSize(null);
                    } else {
                        component.setPreferredSize(new Dimension(0, 0));
                    }

                    component.revalidate();
                }
                
                private boolean containsTextHolderFigure(Collection<Figure> figures) {
                        for (Figure f : figures) {
                            if (f instanceof TextHolderFigure) {
                                return true;
                            } else if (f instanceof CompositeFigure) {
                                if (containsTextHolderFigure(((CompositeFigure) f).getChildren())) {
                                    return true;
                                }
                            }
                        }
                        return false;
                    
                }
            };
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

        btn = ButtonFactory.createFontButton(editor, labels);
        btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
        gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.WEST;
        p.add(btn, gbc);
        btn = ButtonFactory.createFontStyleBoldButton(editor, labels);
        btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
        btn.putClientProperty("Palette.Component.segmentPosition", "first");
        gbc = new GridBagConstraints();
        gbc.gridy = 1;
        gbc.insets = new Insets(3, 0, 0, 0);
        p.add(btn, gbc);
        btn = ButtonFactory.createFontStyleItalicButton(editor, labels);
        btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
        btn.putClientProperty("Palette.Component.segmentPosition", "middle");
        gbc = new GridBagConstraints();
        gbc.gridy = 1;
        gbc.insets = new Insets(3, 0, 0, 0);
        p.add(btn, gbc);
        btn = ButtonFactory.createFontStyleUnderlineButton(editor, labels);
        btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
        btn.putClientProperty("Palette.Component.segmentPosition", "last");
        gbc = new GridBagConstraints();
        gbc.gridy = 1;
        gbc.insets = new Insets(3, 0, 0, 0);
        gbc.anchor = GridBagConstraints.WEST;
        p.add(btn, gbc);
                }
                break;
        }
        return p;
    }

    @Override
    protected String getID() {
        return "font";
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
