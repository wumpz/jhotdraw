/*
 * @(#)FigureToolBar.java  1.2  2008-05-23
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
import org.jhotdraw.gui.*;
import org.jhotdraw.util.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.SliderUI;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.action.*;
import org.jhotdraw.gui.plaf.palette.*;
import static org.jhotdraw.samples.svg.SVGAttributeKeys.*;

/**
 * FigureToolBar.
 * 
 * @author Werner Randelshofer
 * @version 1.2 2008-05-23 Hide the toolbar if nothing is selected, and no
 * creation tool is active. 
 * <br>1.1 2008-03-26 Don't draw border.
 * <br>1.0 May 1, 2007 Created.
 */
public class FigureToolBar extends AbstractToolBar {

    private SelectionComponentDisplayer displayer;
    private ResourceBundleUtil labels;

    /** Creates new instance. */
    public FigureToolBar() {
        labels = ResourceBundleUtil.getBundle("org.jhotdraw.samples.svg.Labels");
        setName(labels.getString(getID() + ".toolbar"));
        setDisclosureStateCount(3);
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
                    p.setLayout(new GridBagLayout());
                    GridBagConstraints gbc;
                    AbstractButton btn;
                    p.setBorder(new EmptyBorder(5, 5, 5, 8));

                    // Link field
                    JLabel linkLabel;
                    JScrollPane scrollPane;
                    JStringAttributeArea linkField;

                    linkLabel = new javax.swing.JLabel();
                    linkLabel.setToolTipText(labels.getString("attribute.figureLink.toolTipText"));
                    linkLabel.setText(labels.getString("attribute.figureLink.text")); // NOI18N
                    linkLabel.setFont(new java.awt.Font("Dialog", 0, 11));

                    scrollPane = new javax.swing.JScrollPane();
                    linkField = new org.jhotdraw.gui.JStringAttributeArea();
                    linkField.setToolTipText(labels.getString("attribute.figureLink.toolTipText"));

                    linkLabel.setLabelFor(linkField);

                    gbc = new GridBagConstraints();
                    gbc.gridx = 0;
                    gbc.insets = new Insets(3, 0, 0, 0);
                    gbc.anchor = GridBagConstraints.SOUTHWEST;
                    p.add(linkLabel, gbc);

                    scrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                    scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                    scrollPane.putClientProperty("JComponent.sizeVariant","small");

                    //linkField.setUI((TextUI) PaletteTextFieldUI.createUI(linkField));
                    linkField.setColumns(8);
                    linkField.setLineWrap(true);
                    linkField.setRows(3);
                    linkField.setWrapStyleWord(true);
                    linkField.setFont(new java.awt.Font("DialogInput", 0, 11));
                    linkField.setAttributeKey(LINK);
                    linkField.setEditor(editor);
                    scrollPane.setViewportView(linkField);
                    gbc = new GridBagConstraints();
                    gbc.gridx = 0;
                    gbc.gridy = 1;
                    gbc.insets = new Insets(3, 0, 0, 0);
                    gbc.fill = GridBagConstraints.BOTH;
                    gbc.gridwidth = GridBagConstraints.REMAINDER;
                    gbc.weightx = 1d;
                    gbc.weighty = 1d;
                    p.add(scrollPane, gbc);

                    // Opacity slider
                    JPopupButton opacityPopupButton = new JPopupButton();
                    JDoubleAttributeSlider opacitySlider = new JDoubleAttributeSlider(JSlider.VERTICAL, 0, 100, 100);
                    opacityPopupButton.add(opacitySlider);
                    labels.configureToolBarButton(opacityPopupButton, "attribute.figureOpacity");
                    opacityPopupButton.setUI((PaletteButtonUI) PaletteButtonUI.createUI(opacityPopupButton));
                    opacityPopupButton.setIcon(
                            new SelectionOpacityIcon(editor, OPACITY, FILL_COLOR, STROKE_COLOR, getClass().getResource(labels.getString("attribute.figureOpacity.icon")),
                            new Rectangle(5, 5, 6, 6), new Rectangle(4, 4, 7, 7)));
                    opacityPopupButton.setPopupAnchor(SOUTH_EAST);
                    new SelectionComponentRepainter(editor, opacityPopupButton);
                    gbc = new GridBagConstraints();
                    gbc.gridx = 2;
                    gbc.gridy = 0;
                    gbc.insets = new Insets(0, 0, 0, 0);
                    gbc.anchor = GridBagConstraints.EAST;
                    p.add(opacityPopupButton, gbc);
                    opacitySlider.setUI((SliderUI) PaletteSliderUI.createUI(opacitySlider));
                    opacitySlider.setScaleFactor(100d);
                    opacitySlider.setAttributeKey(OPACITY);
                    opacitySlider.setEditor(editor);
                }
                break;
                
            case 2:
                 {
                    p = new JPanel();
                    p.setOpaque(false);
                    p.setLayout(new GridBagLayout());
                    GridBagConstraints gbc;
                    AbstractButton btn;
                    p.setBorder(new EmptyBorder(5, 5, 5, 8));

                    // Link field
                    JLabel linkLabel;
                    JScrollPane scrollPane;
                    JStringAttributeArea linkField;

                    linkLabel = new javax.swing.JLabel();
                    scrollPane = new javax.swing.JScrollPane();
                    linkField = new org.jhotdraw.gui.JStringAttributeArea();
                    linkField.setToolTipText(labels.getString("attribute.figureLink.toolTipText"));

                    linkLabel.setFont(new java.awt.Font("Dialog", 0, 11));
                    linkLabel.setLabelFor(linkField);
                    linkLabel.setToolTipText(labels.getString("attribute.figureLink.toolTipText"));
                    linkLabel.setText(labels.getString("attribute.figureLink.text")); // NOI18N

                    gbc = new GridBagConstraints();
                    gbc.gridx = 0;
                    gbc.insets = new Insets(3, 0, 0, 0);
                    gbc.anchor = GridBagConstraints.SOUTHWEST;
                    p.add(linkLabel, gbc);

                    scrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                    scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                    scrollPane.putClientProperty("JComponent.sizeVariant","small");

                    //linkField.setUI((TextUI) PaletteTextFieldUI.createUI(linkField));
                    linkField.setColumns(12);
                    linkField.setLineWrap(true);
                    linkField.setRows(3);
                    linkField.setWrapStyleWord(true);
                    linkField.setFont(new java.awt.Font("DialogInput", 0, 11));
                    linkField.setAttributeKey(LINK);
                    linkField.setEditor(editor);
                    scrollPane.setViewportView(linkField);
                    gbc = new GridBagConstraints();
                    gbc.gridx = 0;
                    gbc.gridy = 1;
                    gbc.insets = new Insets(3, 0, 0, 0);
                    gbc.fill = GridBagConstraints.BOTH;
                    gbc.gridwidth = GridBagConstraints.REMAINDER;
                    gbc.weightx = 1d;
                    gbc.weighty = 1d;
                    p.add(scrollPane, gbc);

                    // Opacity field with slider
                    JDoubleAttributeField opacityField = new JDoubleAttributeField();
                    opacityField.setColumns(2);
                    opacityField.setToolTipText(labels.getString("attribute.figureOpacity.toolTipText"));
                    opacityField.setHorizontalAlignment(JDoubleAttributeField.RIGHT);
                    opacityField.putClientProperty("Palette.Component.segmentPosition", "first");
                    opacityField.setUI((PaletteTextFieldUI) PaletteTextFieldUI.createUI(opacityField));
                    opacityField.setScaleFactor(100d);
                    opacityField.setMinimum(0d);
                    opacityField.setMaximum(100d);
                    opacityField.setAttributeKey(OPACITY);
                    opacityField.setEditor(editor);
                    gbc = new GridBagConstraints();
                    gbc.gridx = 1;
                    gbc.gridy = 0;
                    gbc.insets = new Insets(0, 0, 0, 0);
                    gbc.anchor = GridBagConstraints.EAST;
                    gbc.weightx = 1d;
                    p.add(opacityField, gbc);
                    JPopupButton opacityPopupButton = new JPopupButton();
                    JDoubleAttributeSlider opacitySlider = new JDoubleAttributeSlider(JSlider.VERTICAL, 0, 100, 100);
                    opacityPopupButton.add(opacitySlider);
                    labels.configureToolBarButton(opacityPopupButton, "attribute.figureOpacity");
                    opacityPopupButton.setUI((PaletteButtonUI) PaletteButtonUI.createUI(opacityPopupButton));
                    opacityPopupButton.setIcon(
                            new SelectionOpacityIcon(editor, OPACITY, FILL_COLOR, STROKE_COLOR, getClass().getResource(labels.getString("attribute.figureOpacity.icon")),
                            new Rectangle(5, 5, 6, 6), new Rectangle(4, 4, 7, 7)));
                    opacityPopupButton.setPopupAnchor(SOUTH_EAST);
                    new SelectionComponentRepainter(editor, opacityPopupButton);
                    gbc = new GridBagConstraints();
                    gbc.gridx = 2;
                    gbc.gridy = 0;
                    gbc.insets = new Insets(0, 0, 0, 0);
                    p.add(opacityPopupButton, gbc);
                    opacitySlider.setUI((SliderUI) PaletteSliderUI.createUI(opacitySlider));
                    opacitySlider.setScaleFactor(100d);
                    opacitySlider.setAttributeKey(OPACITY);
                    opacitySlider.setEditor(editor);
                }
                break;
        }
        return p;
    }

    @Override
    protected String getID() {
        return "figure";
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
