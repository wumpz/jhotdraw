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
import org.jhotdraw.gui.*;
import org.jhotdraw.gui.plaf.palette.*;
import org.jhotdraw.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.SliderUI;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.action.*;
import static org.jhotdraw.samples.svg.SVGAttributeKeys.*;

/**
 * StrokeToolBar.
 * 
 * @author Werner Randelshofer
 * @version 1.2 2008-05-23 Hide the toolbar if nothing is selected, and no
 * creation tool is active. 
 * <br>1.1 2008-03-26 Don't draw button borders. 
 * <br>1.0 May 1, 2007 Created.
 */
public class StrokeToolBar extends AbstractToolBar {

    private SelectionComponentDisplayer displayer;

    /** Creates new instance. */
    public StrokeToolBar() {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.samples.svg.Labels");
        setName(labels.getString("stroke.toolbar"));
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
                    p.setBorder(new EmptyBorder(5, 5, 5, 8));
                    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.samples.svg.Labels");

                    GridBagLayout layout = new GridBagLayout();
                    p.setLayout(layout);
                    GridBagConstraints gbc;
                    AbstractButton btn;

                    // Stroke color
                    btn = ButtonFactory.createSelectionColorButton(editor,
                            STROKE_COLOR, ButtonFactory.WEBSAVE_COLORS, ButtonFactory.WEBSAVE_COLORS_COLUMN_COUNT,
                            "attribute.strokeColor", labels, null, new Rectangle(3, 3, 10, 10));
                    btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
                    ((JPopupButton) btn).setAction(null, null);
                    gbc = new GridBagConstraints();
                    gbc.gridx = 0;
                    gbc.anchor = GridBagConstraints.FIRST_LINE_START;
                    p.add(btn, gbc);

                    // Opacity slider
                    JPopupButton opacityPopupButton = new JPopupButton();
                    JAttributeSlider opacitySlider = new JAttributeSlider(JSlider.VERTICAL, 0, 100, 100);
                    opacityPopupButton.add(opacitySlider);
                    labels.configureToolBarButton(opacityPopupButton, "attribute.strokeOpacity");
                    opacityPopupButton.setUI((PaletteButtonUI) PaletteButtonUI.createUI(opacityPopupButton));
                    opacityPopupButton.setIcon(
                            new SelectionOpacityIcon(editor, STROKE_OPACITY, null, STROKE_COLOR, getClass().getResource(labels.getString("attribute.strokeOpacity.icon")),
                            new Rectangle(5, 5, 6, 6), new Rectangle(4, 4, 7, 7)));
                    opacityPopupButton.setPopupAnchor(SOUTH_EAST);
                    new SelectionComponentRepainter(editor, opacityPopupButton);
                    gbc = new GridBagConstraints();
                    gbc.gridx = 0;
                    gbc.anchor = GridBagConstraints.FIRST_LINE_START;
                    gbc.insets = new Insets(3, 0, 0, 0);
                    p.add(opacityPopupButton, gbc);
                    opacitySlider.setUI((SliderUI) PaletteSliderUI.createUI(opacitySlider));
                    opacitySlider.setScaleFactor(100d);
                    new FigureAttributeEditorHandler(STROKE_OPACITY, opacitySlider, editor);

                    // Create stroke width popup slider
                    JPopupButton strokeWidthPopupButton = new JPopupButton();
                    JAttributeSlider strokeWidthSlider = new JAttributeSlider(
                            JSlider.VERTICAL, 0, 50, 1);
                    strokeWidthSlider.setUI((SliderUI) PaletteSliderUI.createUI(strokeWidthSlider));
                    strokeWidthPopupButton.add(strokeWidthSlider);
                    labels.configureToolBarButton(strokeWidthPopupButton, "attribute.strokeWidth");
                    strokeWidthPopupButton.setUI((PaletteButtonUI) PaletteButtonUI.createUI(strokeWidthPopupButton));
                    gbc = new GridBagConstraints();
                    gbc.anchor = GridBagConstraints.FIRST_LINE_START;
                    gbc.gridx = 0;
                    gbc.insets = new Insets(3, 0, 0, 0);
                    p.add(strokeWidthPopupButton, gbc);
                    new FigureAttributeEditorHandler(STROKE_WIDTH, strokeWidthSlider, editor);

                    // Create stroke dashes buttons
                    btn = ButtonFactory.createStrokeDashesButton(editor, labels);
                    btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
                    gbc = new GridBagConstraints();
                    gbc.gridwidth = GridBagConstraints.REMAINDER;
                    gbc.anchor = GridBagConstraints.FIRST_LINE_START;
                    gbc.gridy = 0;
                    gbc.insets = new Insets(0, 3, 0, 0);
                    p.add(btn, gbc);
                    btn = ButtonFactory.createStrokeCapButton(editor, labels);
                    btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
                    gbc = new GridBagConstraints();
                    gbc.anchor = GridBagConstraints.FIRST_LINE_START;
                    gbc.gridy = 1;
                    gbc.insets = new Insets(3, 3, 0, 0);
                    p.add(btn, gbc);
                    btn = ButtonFactory.createStrokeJoinButton(editor, labels);
                    btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
                    gbc = new GridBagConstraints();
                    gbc.anchor = GridBagConstraints.FIRST_LINE_START;
                    gbc.gridy = 2;
                    gbc.insets = new Insets(3, 3, 0, 0);
                    p.add(btn, gbc);
                }
                break;

            case 2:
                 {
                    p = new JPanel();
                   p.setOpaque(false);
                    p.setBorder(new EmptyBorder(5, 5, 5, 8));
                    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.samples.svg.Labels");

                    GridBagLayout layout = new GridBagLayout();
                    p.setLayout(layout);
                    GridBagConstraints gbc;
                    AbstractButton btn;

                    // Stroke color
                    btn = ButtonFactory.createSelectionColorButton(editor,
                            STROKE_COLOR, ButtonFactory.WEBSAVE_COLORS, ButtonFactory.WEBSAVE_COLORS_COLUMN_COUNT,
                            "attribute.strokeColor", labels, null, new Rectangle(3, 3, 10, 10));
                    btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
                    ((JPopupButton) btn).setAction(null, null);
                    gbc = new GridBagConstraints();
                    gbc.gridx = 0;
                    gbc.gridwidth = 2;
                    gbc.anchor = GridBagConstraints.FIRST_LINE_START;
                    p.add(btn, gbc);

                    // Opacity field with slider
                    JAttributeTextField opacityField = new JAttributeTextField();
                    opacityField.setColumns(3);
                    opacityField.setToolTipText(labels.getString("attribute.strokeOpacity.toolTipText"));
                    opacityField.setHorizontalAlignment(JAttributeTextField.RIGHT);
                    opacityField.putClientProperty("Palette.Component.segmentPosition", "first");
                    opacityField.setUI((PaletteFormattedTextFieldUI) PaletteFormattedTextFieldUI.createUI(opacityField));
                    opacityField.setFormatterFactory(ScalableNumberFormatter.createFormatterFactory(0d, 100d, 100d));
                    opacityField.setHorizontalAlignment(JTextField.LEFT);
                    new FigureAttributeEditorHandler(STROKE_OPACITY, opacityField, editor);
                    gbc = new GridBagConstraints();
                    gbc.gridx = 0;
                    gbc.insets = new Insets(3, 0, 0, 0);
                    gbc.fill = GridBagConstraints.HORIZONTAL;
                    gbc.anchor = GridBagConstraints.FIRST_LINE_START;
                    p.add(opacityField, gbc);
                    JPopupButton opacityPopupButton = new JPopupButton();
                    JAttributeSlider opacitySlider = new JAttributeSlider(JSlider.VERTICAL, 0, 100, 100);
                    opacityPopupButton.add(opacitySlider);
                    labels.configureToolBarButton(opacityPopupButton, "attribute.strokeOpacity");
                    opacityPopupButton.setUI((PaletteButtonUI) PaletteButtonUI.createUI(opacityPopupButton));
                    opacityPopupButton.setIcon(
                            new SelectionOpacityIcon(editor, STROKE_OPACITY, null, STROKE_COLOR, getClass().getResource(labels.getString("attribute.strokeOpacity.icon")),
                            new Rectangle(5, 5, 6, 6), new Rectangle(4, 4, 7, 7)));
                    opacityPopupButton.setPopupAnchor(SOUTH_EAST);
                    new SelectionComponentRepainter(editor, opacityPopupButton);
                    gbc = new GridBagConstraints();
                    gbc.gridx = 1;
                    gbc.anchor = GridBagConstraints.FIRST_LINE_START;
                    gbc.weighty = 1f;
                    gbc.insets = new Insets(3, 0, 0, 0);
                    p.add(opacityPopupButton, gbc);
                    opacitySlider.setUI((SliderUI) PaletteSliderUI.createUI(opacitySlider));
                    opacitySlider.setScaleFactor(100d);
                    new FigureAttributeEditorHandler(STROKE_OPACITY, opacitySlider, editor);

                    // Create stroke width field with popup slider
                    JAttributeTextField strokeWidthField = new JAttributeTextField();
                    //strokeWidthField.setFont(PaletteLookAndFeel.getInstance().getFont("SmallSystemFont"));
                    strokeWidthField.setColumns(2);
                    strokeWidthField.setToolTipText(labels.getString("attribute.strokeWidth.toolTipText"));
                    strokeWidthField.setHorizontalAlignment(JAttributeTextField.LEFT);
                    strokeWidthField.putClientProperty("Palette.Component.segmentPosition", "first");
                    strokeWidthField.setUI((PaletteFormattedTextFieldUI) PaletteFormattedTextFieldUI.createUI(strokeWidthField));
                    strokeWidthField.setFormatterFactory(ScalableNumberFormatter.createFormatterFactory(0d, 1000d, 1d));
                    new FigureAttributeEditorHandler(STROKE_WIDTH, strokeWidthField, editor);
                    gbc = new GridBagConstraints();
                    gbc.gridx = 0;
                    gbc.gridy = 2;
                    gbc.insets = new Insets(3, 0, 0, 0);
                    gbc.fill = GridBagConstraints.BOTH;
                    p.add(strokeWidthField, gbc);
                    JPopupButton strokeWidthPopupButton = new JPopupButton();
                    JAttributeSlider strokeWidthSlider = new JAttributeSlider(
                            JSlider.VERTICAL, 0, 50, 1);
                    strokeWidthSlider.setUI((SliderUI) PaletteSliderUI.createUI(strokeWidthSlider));
                    strokeWidthPopupButton.add(strokeWidthSlider);
                    labels.configureToolBarButton(strokeWidthPopupButton, "attribute.strokeWidth");
                    strokeWidthPopupButton.setUI((PaletteButtonUI) PaletteButtonUI.createUI(strokeWidthPopupButton));
                    gbc = new GridBagConstraints();
                    gbc.anchor = GridBagConstraints.FIRST_LINE_START;
                    gbc.gridx = 1;
                    gbc.insets = new Insets(3, 0, 0, 0);
                    p.add(strokeWidthPopupButton, gbc);
                    new FigureAttributeEditorHandler(STROKE_WIDTH, strokeWidthSlider, editor);

                    // Create stroke dashes button
                    btn = ButtonFactory.createStrokeDashesButton(editor, labels);
                    btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
                    gbc = new GridBagConstraints();
                    gbc.gridwidth = GridBagConstraints.REMAINDER;
                    gbc.anchor = GridBagConstraints.FIRST_LINE_START;
                    gbc.gridy = 0;
                    gbc.insets = new Insets(0, 3, 0, 0);
                    p.add(btn, gbc);
                    btn = ButtonFactory.createStrokeCapButton(editor, labels);
                    btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
                    gbc = new GridBagConstraints();
                    gbc.anchor = GridBagConstraints.FIRST_LINE_START;
                    gbc.gridy = 1;
                    gbc.insets = new Insets(3, 3, 0, 0);
                    p.add(btn, gbc);
                    btn = ButtonFactory.createStrokeJoinButton(editor, labels);
                    btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
                    gbc = new GridBagConstraints();
                    gbc.anchor = GridBagConstraints.FIRST_LINE_START;
                    gbc.gridy = 2;
                    gbc.gridwidth = 2;
                    gbc.insets = new Insets(3, 3, 0, 0);
                    p.add(btn, gbc);
                }
                break;
        }
        return p;
    }

    @Override
    protected String getID() {
        return "stroke";
    }

    @Override
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
