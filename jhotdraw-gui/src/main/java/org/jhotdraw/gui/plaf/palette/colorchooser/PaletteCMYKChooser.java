/*
 * @(#)PaletteCMYKChooser.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.gui.plaf.palette.colorchooser;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.colorchooser.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import org.jhotdraw.color.CMYKGenericColorSpace;
import org.jhotdraw.color.CMYKNominalColorSpace;
import org.jhotdraw.color.ColorSliderModel;
import org.jhotdraw.gui.plaf.palette.PaletteLabelUI;
import org.jhotdraw.gui.plaf.palette.PaletteLookAndFeel;
import org.jhotdraw.gui.plaf.palette.PalettePanelUI;
import org.jhotdraw.gui.plaf.palette.PaletteTextFieldUI;

/** A color chooser with CMYK color sliders. */
public class PaletteCMYKChooser extends AbstractColorChooserPanel implements UIResource {

  private static final long serialVersionUID = 1L;
  private ColorSliderModel ccModel;
  private int updateRecursion = 0;
  private PaletteLookAndFeel labels;

  /** Creates new form. */
  public PaletteCMYKChooser() {}

  @Override
  protected void buildChooser() {
    PaletteLookAndFeel plaf = labels = PaletteLookAndFeel.getInstance();
    initComponents();
    setUI(PalettePanelUI.createUI(this));
    cyanFieldPanel.setUI((PanelUI) PalettePanelUI.createUI(cyanFieldPanel));
    magentaFieldPanel.setUI((PanelUI) PalettePanelUI.createUI(magentaFieldPanel));
    yellowFieldPanel.setUI((PanelUI) PalettePanelUI.createUI(yellowFieldPanel));
    blackFieldPanel.setUI((PanelUI) PalettePanelUI.createUI(blackFieldPanel));
    cyanLabel.setUI((LabelUI) PaletteLabelUI.createUI(cyanLabel));
    magentaLabel.setUI((LabelUI) PaletteLabelUI.createUI(magentaLabel));
    yellowLabel.setUI((LabelUI) PaletteLabelUI.createUI(yellowLabel));
    blackLabel.setUI((LabelUI) PaletteLabelUI.createUI(blackLabel));
    cyanFieldLabel.setUI((LabelUI) PaletteLabelUI.createUI(cyanFieldLabel));
    magentaFieldLabel.setUI((LabelUI) PaletteLabelUI.createUI(magentaFieldLabel));
    yellowFieldLabel.setUI((LabelUI) PaletteLabelUI.createUI(yellowFieldLabel));
    blackFieldLabel.setUI((LabelUI) PaletteLabelUI.createUI(blackFieldLabel));
    cyanField.setUI((TextUI) PaletteTextFieldUI.createUI(cyanField));
    magentaField.setUI((TextUI) PaletteTextFieldUI.createUI(magentaField));
    yellowField.setUI((TextUI) PaletteTextFieldUI.createUI(yellowField));
    blackField.setUI((TextUI) PaletteTextFieldUI.createUI(blackField));
    Font font = plaf.getFont("ColorChooser.font");
    cyanLabel.setFont(font);
    cyanSlider.setFont(font);
    cyanField.setFont(font);
    cyanFieldLabel.setFont(font);
    magentaLabel.setFont(font);
    magentaSlider.setFont(font);
    magentaField.setFont(font);
    magentaFieldLabel.setFont(font);
    yellowLabel.setFont(font);
    yellowSlider.setFont(font);
    yellowField.setFont(font);
    yellowFieldLabel.setFont(font);
    blackLabel.setFont(font);
    blackSlider.setFont(font);
    blackField.setFont(font);
    blackFieldLabel.setFont(font);
    int textSliderGap = plaf.getInt("ColorChooser.textSliderGap");
    if (textSliderGap != 0) {
      Border fieldBorder = new EmptyBorder(0, textSliderGap, 0, 0);
      cyanFieldPanel.setBorder(fieldBorder);
      magentaFieldPanel.setBorder(fieldBorder);
      yellowFieldPanel.setBorder(fieldBorder);
      blackFieldPanel.setBorder(fieldBorder);
    }
    // The NominalCMYKColorSliderModel works fine:
    // ccModel = new NominalCMYKColorSliderModel();
    /* Unfortunately the following does not work due to Java bug #4760025 as
     * described at http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4760025*/
    try {
      ccModel = new PaletteColorSliderModel(new CMYKGenericColorSpace());
    } catch (IOException e) {
      System.err.println("Warning: " + getClass() + " couldn't load \"Generic CMYK Profile.icc\".");
      // e.printStackTrace();
      ccModel = new PaletteColorSliderModel(new CMYKNominalColorSpace());
    }
    ccModel.configureSlider(0, cyanSlider);
    ccModel.configureSlider(1, magentaSlider);
    ccModel.configureSlider(2, yellowSlider);
    ccModel.configureSlider(3, blackSlider);
    cyanField.setText(Integer.toString(cyanSlider.getValue()));
    magentaField.setText(Integer.toString(magentaSlider.getValue()));
    yellowField.setText(Integer.toString(yellowSlider.getValue()));
    blackField.setText(Integer.toString(blackSlider.getValue()));
    Insets borderMargin = UIManager.getInsets("Component.visualMargin");
    if (borderMargin != null) {
      borderMargin = (Insets) borderMargin.clone();
      borderMargin.left = 3 - borderMargin.left;
      cyanFieldLabel.putClientProperty("Quaqua.Component.visualMargin", borderMargin);
      magentaFieldLabel.putClientProperty("Quaqua.Component.visualMargin", borderMargin);
      yellowFieldLabel.putClientProperty("Quaqua.Component.visualMargin", borderMargin);
      blackFieldLabel.putClientProperty("Quaqua.Component.visualMargin", borderMargin);
    }
    new ColorSliderTextFieldHandler(cyanField, ccModel, 0);
    new ColorSliderTextFieldHandler(magentaField, ccModel, 1);
    new ColorSliderTextFieldHandler(yellowField, ccModel, 2);
    new ColorSliderTextFieldHandler(blackField, ccModel, 3);
    ccModel.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent evt) {
        if (updateRecursion++ == 0) {
          setColorToModel(ccModel.getColor());
        }
        updateRecursion--;
      }
    });
    cyanField.setMinimumSize(cyanField.getPreferredSize());
    magentaField.setMinimumSize(magentaField.getPreferredSize());
    yellowField.setMinimumSize(yellowField.getPreferredSize());
    blackField.setMinimumSize(blackField.getPreferredSize());
    EmptyBorder bm = new EmptyBorder(0, 0, 0, 0);
    cyanLabel.setBorder(bm);
    magentaLabel.setBorder(bm);
    yellowLabel.setBorder(bm);
    blackLabel.setBorder(bm);
  }

  @Override
  public String getDisplayName() {
    return PaletteLookAndFeel.getInstance().getString("ColorChooser.cmykSliders");
  }

  @Override
  public Icon getLargeDisplayIcon() {
    return PaletteLookAndFeel.getInstance().getIcon("ColorChooser.colorSlidersIcon");
  }

  @Override
  public Icon getSmallDisplayIcon() {
    return getLargeDisplayIcon();
  }

  @Override
  public void updateChooser() {
    if (updateRecursion++ == 0) {
      ccModel.setColor(getColorFromModel());
    }
    updateRecursion--;
  }

  public void setColorToModel(Color color) {
    getColorSelectionModel().setSelectedColor(color);
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT
   * modify this code. The content of this method is always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;
    cyanLabel = new javax.swing.JLabel();
    cyanSlider = new javax.swing.JSlider();
    cyanFieldPanel = new javax.swing.JPanel();
    cyanField = new javax.swing.JTextField();
    cyanFieldLabel = new javax.swing.JLabel();
    magentaLabel = new javax.swing.JLabel();
    magentaSlider = new javax.swing.JSlider();
    magentaFieldPanel = new javax.swing.JPanel();
    magentaField = new javax.swing.JTextField();
    magentaFieldLabel = new javax.swing.JLabel();
    yellowLabel = new javax.swing.JLabel();
    yellowSlider = new javax.swing.JSlider();
    yellowFieldPanel = new javax.swing.JPanel();
    yellowField = new javax.swing.JTextField();
    yellowFieldLabel = new javax.swing.JLabel();
    blackLabel = new javax.swing.JLabel();
    blackSlider = new javax.swing.JSlider();
    blackFieldPanel = new javax.swing.JPanel();
    blackField = new javax.swing.JTextField();
    blackFieldLabel = new javax.swing.JLabel();
    springPanel = new javax.swing.JPanel();
    setLayout(new java.awt.GridBagLayout());
    cyanLabel.setText(labels.getString("ColorChooser.cmykCyanText")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(3, 0, -4, 0);
    add(cyanLabel, gridBagConstraints);
    cyanSlider.setMajorTickSpacing(100);
    cyanSlider.setMinorTickSpacing(50);
    cyanSlider.setPaintTicks(true);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    add(cyanSlider, gridBagConstraints);
    cyanFieldPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));
    cyanField.setColumns(3);
    cyanField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
    cyanField.setText("0");
    cyanField.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent evt) {
        fieldFocusGained(evt);
      }

      public void focusLost(java.awt.event.FocusEvent evt) {
        cyanFieldFocusLost(evt);
      }
    });
    cyanFieldPanel.add(cyanField);
    cyanFieldLabel.setText("%");
    cyanFieldPanel.add(cyanFieldLabel);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridheight = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
    add(cyanFieldPanel, gridBagConstraints);
    magentaLabel.setText(labels.getString("ColorChooser.cmykMagentaText")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(3, 0, -4, 0);
    add(magentaLabel, gridBagConstraints);
    magentaSlider.setMajorTickSpacing(100);
    magentaSlider.setMinorTickSpacing(50);
    magentaSlider.setPaintTicks(true);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    add(magentaSlider, gridBagConstraints);
    magentaFieldPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));
    magentaField.setColumns(3);
    magentaField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
    magentaField.setText("0");
    magentaField.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent evt) {
        fieldFocusGained(evt);
      }

      public void focusLost(java.awt.event.FocusEvent evt) {
        magentaFieldFocusLost(evt);
      }
    });
    magentaFieldPanel.add(magentaField);
    magentaFieldLabel.setText("%");
    magentaFieldPanel.add(magentaFieldLabel);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridheight = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
    add(magentaFieldPanel, gridBagConstraints);
    yellowLabel.setText(labels.getString("ColorChooser.cmykYellowText")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(3, 0, -4, 0);
    add(yellowLabel, gridBagConstraints);
    yellowSlider.setMajorTickSpacing(100);
    yellowSlider.setMinorTickSpacing(50);
    yellowSlider.setPaintTicks(true);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    add(yellowSlider, gridBagConstraints);
    yellowFieldPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));
    yellowField.setColumns(3);
    yellowField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
    yellowField.setText("0");
    yellowField.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent evt) {
        fieldFocusGained(evt);
      }

      public void focusLost(java.awt.event.FocusEvent evt) {
        yellowFieldFocusLost(evt);
      }
    });
    yellowFieldPanel.add(yellowField);
    yellowFieldLabel.setText("%");
    yellowFieldPanel.add(yellowFieldLabel);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.gridheight = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
    add(yellowFieldPanel, gridBagConstraints);
    blackLabel.setText(labels.getString("ColorChooser.cmykBlackText")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(3, 0, -4, 0);
    add(blackLabel, gridBagConstraints);
    blackSlider.setMajorTickSpacing(100);
    blackSlider.setMinorTickSpacing(50);
    blackSlider.setPaintTicks(true);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    add(blackSlider, gridBagConstraints);
    blackFieldPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));
    blackField.setColumns(3);
    blackField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
    blackField.setText("0");
    blackField.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent evt) {
        fieldFocusGained(evt);
      }

      public void focusLost(java.awt.event.FocusEvent evt) {
        blackFieldFocusLost(evt);
      }
    });
    blackFieldPanel.add(blackField);
    blackFieldLabel.setText("%");
    blackFieldPanel.add(blackFieldLabel);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.gridheight = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
    add(blackFieldPanel, gridBagConstraints);
    springPanel.setLayout(new java.awt.BorderLayout());
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 100;
    gridBagConstraints.weighty = 1.0;
    add(springPanel, gridBagConstraints);
  } // </editor-fold>//GEN-END:initComponents

  private void fieldFocusGained(java.awt.event.FocusEvent evt) { // GEN-FIRST:event_fieldFocusGained
    ((JTextField) evt.getSource()).selectAll();
  } // GEN-LAST:event_fieldFocusGained

  private void blackFieldFocusLost(
      java.awt.event.FocusEvent evt) { // GEN-FIRST:event_blackFieldFocusLost
    blackField.setText(Integer.toString(ccModel.getBoundedRangeModel(3).getValue()));
  } // GEN-LAST:event_blackFieldFocusLost

  private void yellowFieldFocusLost(
      java.awt.event.FocusEvent evt) { // GEN-FIRST:event_yellowFieldFocusLost
    yellowField.setText(Integer.toString(ccModel.getBoundedRangeModel(2).getValue()));
  } // GEN-LAST:event_yellowFieldFocusLost

  private void magentaFieldFocusLost(
      java.awt.event.FocusEvent evt) { // GEN-FIRST:event_magentaFieldFocusLost
    magentaField.setText(Integer.toString(ccModel.getBoundedRangeModel(1).getValue()));
  } // GEN-LAST:event_magentaFieldFocusLost

  private void cyanFieldFocusLost(
      java.awt.event.FocusEvent evt) { // GEN-FIRST:event_cyanFieldFocusLost
    cyanField.setText(Integer.toString(ccModel.getBoundedRangeModel(0).getValue()));
  } // GEN-LAST:event_cyanFieldFocusLost

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JTextField blackField;
  private javax.swing.JLabel blackFieldLabel;
  private javax.swing.JPanel blackFieldPanel;
  private javax.swing.JLabel blackLabel;
  private javax.swing.JSlider blackSlider;
  private javax.swing.JTextField cyanField;
  private javax.swing.JLabel cyanFieldLabel;
  private javax.swing.JPanel cyanFieldPanel;
  private javax.swing.JLabel cyanLabel;
  private javax.swing.JSlider cyanSlider;
  private javax.swing.JTextField magentaField;
  private javax.swing.JLabel magentaFieldLabel;
  private javax.swing.JPanel magentaFieldPanel;
  private javax.swing.JLabel magentaLabel;
  private javax.swing.JSlider magentaSlider;
  private javax.swing.JPanel springPanel;
  private javax.swing.JTextField yellowField;
  private javax.swing.JLabel yellowFieldLabel;
  private javax.swing.JPanel yellowFieldPanel;
  private javax.swing.JLabel yellowLabel;
  private javax.swing.JSlider yellowSlider;
  // End of variables declaration//GEN-END:variables
}
