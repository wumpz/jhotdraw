/*
 * @(#)CanvasToolBar.java  1.0  2008-05-18
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
import javax.swing.plaf.SliderUI;
import javax.swing.plaf.SliderUI;
import org.jhotdraw.app.action.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.action.*;
import org.jhotdraw.gui.plaf.palette.*;
import org.jhotdraw.samples.svg.action.*;
import org.jhotdraw.samples.svg.figures.*;
import static org.jhotdraw.samples.svg.SVGAttributeKeys.*;

/**
 * CanvasToolBar.
 *
 * @author Werner Randelshofer
 * @version 1.0 2008-05-18 Created.
 */
public class CanvasToolBar extends AbstractToolBar {

    /** Creates new instance. */
    public CanvasToolBar() {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.samples.svg.Labels");
        setName(labels.getString(getID() + ".toolbar"));
        setDisclosureStateCount(3);
    }

    @Override
    protected JComponent createDisclosedComponent(int state) {
        JPanel p = null;

        switch (state) {
            case 1:
                 {
                    p = new JPanel();
                    p.setOpaque(false);

                    p.removeAll();
                    p.setBorder(new EmptyBorder(5, 5, 5, 8));
                    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.samples.svg.Labels");
                    GridBagLayout layout = new GridBagLayout();
                    p.setLayout(layout);
                    GridBagConstraints gbc;
                    AbstractButton btn;

                    // Fill color
                    btn = ButtonFactory.createDrawingColorButton(editor,
                            CANVAS_FILL_COLOR, ButtonFactory.WEBSAVE_COLORS, ButtonFactory.WEBSAVE_COLORS_COLUMN_COUNT,
                            "attribute.canvasFillColor", labels, null, new Rectangle(3, 3, 10, 10));
                    btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
                    new DrawingComponentRepainter(editor, btn);
                    ((JPopupButton) btn).setAction(null, null);
                    gbc = new GridBagConstraints();
                    gbc.gridx = 0;
                    gbc.gridwidth = 2;
                    gbc.anchor = GridBagConstraints.FIRST_LINE_START;
                    p.add(btn, gbc);

                    // Opacity slider
                    JPopupButton opacityPopupButton = new JPopupButton();
                    JDoubleDrawingAttributeSlider opacitySlider = new JDoubleDrawingAttributeSlider(JSlider.VERTICAL, 0, 100, 100);
                    opacitySlider.setUI((SliderUI) PaletteSliderUI.createUI(opacitySlider));
                    opacitySlider.setScaleFactor(100d);
                    opacitySlider.setAttributeKey(CANVAS_FILL_OPACITY);
                    opacitySlider.setEditor(editor);
                    opacityPopupButton.add(opacitySlider);
                    labels.configureToolBarButton(opacityPopupButton, "attribute.canvasFillOpacity");
                    opacityPopupButton.setUI((PaletteButtonUI) PaletteButtonUI.createUI(opacityPopupButton));
                    opacityPopupButton.setIcon(
                            new CanvasOpacityIcon(editor, CANVAS_FILL_OPACITY, CANVAS_FILL_COLOR, null, getClass().getResource(labels.getString("attribute.canvasFillOpacity.icon")),
                            new Rectangle(5, 5, 6, 6), new Rectangle(4, 4, 7, 7)));
                    new DrawingComponentRepainter(editor, opacityPopupButton);
                    gbc = new GridBagConstraints();
                    gbc.gridx = 0;
                    gbc.anchor = GridBagConstraints.FIRST_LINE_START;
                    gbc.insets = new Insets(3, 0, 0, 0);
                    p.add(opacityPopupButton, gbc);

                    // Toggle Grid Button
                    btn = ButtonFactory.createToggleGridButton(editor.getActiveView());
                    btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
                    labels.configureToolBarButton(btn, "alignGrid");
                    gbc = new GridBagConstraints();
                    gbc.gridx = 0;
                    gbc.anchor = GridBagConstraints.FIRST_LINE_START;
                    gbc.weighty = 1f;
                    gbc.insets = new Insets(3, 0, 0, 0);
                    p.add(btn, gbc);

                    // Zoom button
                    btn = ButtonFactory.createZoomButton(editor.getActiveView());
                    btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
                    labels.configureToolBarButton(btn, "view.zoomFactor");
                    btn.setText("100 %");
                    gbc = new GridBagConstraints();
                    gbc.gridx = 1;
                    gbc.gridy = 2;
                    gbc.anchor = GridBagConstraints.FIRST_LINE_START;
                    gbc.fill = GridBagConstraints.BOTH;
                    gbc.insets = new Insets(3, 3, 0, 0);
                    p.add(btn, gbc);
                }
                break;
            case 2:
                 {
                    // Panel 2 -- FIXME implement panel 2
                    p = new JPanel();
                    p.setOpaque(false);
                    p.setBorder(new EmptyBorder(5, 5, 5, 8));
                    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.samples.svg.Labels");
                    GridBagLayout layout = new GridBagLayout();
                    p.setLayout(layout);
                    GridBagConstraints gbc;
                    AbstractButton btn;

                    // Fill color
                    btn = ButtonFactory.createDrawingColorButton(editor,
                            CANVAS_FILL_COLOR, ButtonFactory.WEBSAVE_COLORS, ButtonFactory.WEBSAVE_COLORS_COLUMN_COUNT,
                            "attribute.canvasFillColor", labels, null, new Rectangle(3, 3, 10, 10));
                    btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
                    new DrawingComponentRepainter(editor, btn);
                    ((JPopupButton) btn).setAction(null, null);
                    gbc = new GridBagConstraints();
                    gbc.gridx = 0;
                    gbc.gridwidth = 2;
                    gbc.anchor = GridBagConstraints.FIRST_LINE_START;
                    p.add(btn, gbc);

                    // Opacity field with slider
                    JDoubleAttributeField opacityField = new JDoubleAttributeField();
                    opacityField.setColumns(2);
                    opacityField.setToolTipText(labels.getString("attribute.canvasFillOpacity.tip"));
                    opacityField.setHorizontalAlignment(JDoubleAttributeField.RIGHT);
                    opacityField.putClientProperty("Palette.Component.segmentPosition", "first");
                    opacityField.setUI((PaletteTextFieldUI) PaletteTextFieldUI.createUI(opacityField));
                    opacityField.setScaleFactor(100d);
                    opacityField.setMinimum(0d);
                    opacityField.setMaximum(100d);
                    opacityField.setAttributeKey(CANVAS_FILL_OPACITY);
                    opacityField.setEditor(editor);
                    gbc = new GridBagConstraints();
                    gbc.gridx = 0;
                    gbc.insets = new Insets(3, 0, 0, 0);
                    gbc.fill = GridBagConstraints.HORIZONTAL;
                    gbc.anchor = GridBagConstraints.FIRST_LINE_START;
                    p.add(opacityField, gbc);
                    JPopupButton opacityPopupButton = new JPopupButton();
                    JDoubleDrawingAttributeSlider opacitySlider = new JDoubleDrawingAttributeSlider(JSlider.VERTICAL, 0, 100, 100);
                    opacityPopupButton.add(opacitySlider);
                    labels.configureToolBarButton(opacityPopupButton, "attribute.canvasOpacity");
                    opacityPopupButton.setUI((PaletteButtonUI) PaletteButtonUI.createUI(opacityPopupButton));
                    opacityPopupButton.setIcon(
                            new CanvasOpacityIcon(editor, CANVAS_FILL_OPACITY, CANVAS_FILL_COLOR, null, getClass().getResource(labels.getString("attribute.canvasFillOpacity.icon")),
                            new Rectangle(5, 5, 6, 6), new Rectangle(4, 4, 7, 7)));
                    new DrawingComponentRepainter(editor, opacityPopupButton);
                    gbc = new GridBagConstraints();
                    gbc.gridx = 1;
                    gbc.anchor = GridBagConstraints.FIRST_LINE_START;
                    gbc.weighty = 1f;
                    gbc.insets = new Insets(3, 0, 0, 0);
                    p.add(opacityPopupButton, gbc);
                    opacitySlider.setUI((SliderUI) PaletteSliderUI.createUI(opacitySlider));
                    opacitySlider.setScaleFactor(100d);
                    opacitySlider.setAttributeKey(CANVAS_FILL_OPACITY);
                    opacitySlider.setEditor(editor);
                    
                    // Toggle Grid Button
                    btn = ButtonFactory.createToggleGridButton(editor.getActiveView());
                    btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
                    labels.configureToolBarButton(btn, "alignGrid");
                    gbc = new GridBagConstraints();
                    gbc.gridx = 0;
                    gbc.anchor = GridBagConstraints.FIRST_LINE_START;
                    gbc.weighty = 1f;
                    gbc.insets = new Insets(3, 0, 0, 0);
                    p.add(btn, gbc);

                    // Zoom button
                    btn = ButtonFactory.createZoomButton(editor.getActiveView());
                    btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
                    labels.configureToolBarButton(btn, "view.zoomFactor");
                    btn.setText("100 %");
                    gbc = new GridBagConstraints();
                    gbc.gridx = 1;
                    gbc.gridy = 2;
                    gbc.anchor = GridBagConstraints.FIRST_LINE_START;
                    gbc.fill = GridBagConstraints.BOTH;
                    gbc.insets = new Insets(3, 3, 0, 0);
                    p.add(btn, gbc);
                }
                break;
        }
        return p;
    }

    @Override
    protected String getID() {
        return "canvas";
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
