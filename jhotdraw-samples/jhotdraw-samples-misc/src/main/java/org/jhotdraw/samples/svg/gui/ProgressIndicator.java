/*
 * @(#)ProgressIndicator.java
 *
 * Copyright (c) 2007-2008 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.svg.gui;

import java.awt.*;
import javax.swing.*;

/** ProgressIndicator. */
public class ProgressIndicator extends javax.swing.JPanel {

  private static final long serialVersionUID = 1L;
  ProgressMonitor m;
  private BoundedRangeModel progressModel;

  /** Creates new instance. */
  public ProgressIndicator() {
    this(null, null);
  }

  /** Creates new instance. */
  public ProgressIndicator(String message, String note) {
    this(message, note, 0, 100, true);
  }

  /** Creates new instance. */
  public ProgressIndicator(String message, String note, int min, int max, boolean indeterminate) {
    initComponents();
    messageLabel.setFont(messageLabel.getFont().deriveFont(Font.BOLD));
    progressModel = new DefaultBoundedRangeModel(0, 0, min, max);
    progressBar.setModel(progressModel);
    setMessage(message);
    setNote(note);
    setIndeterminate(indeterminate);
  }

  public void setMessage(String message) {
    messageLabel.setText(message);
  }

  public String getMessage() {
    return messageLabel.getText();
  }

  public void setNote(final String newValue) {
    String oldValue = noteLabel.getText();
    noteLabel.setText(newValue);
    if (oldValue == null || newValue == null && oldValue != newValue) {
      Runnable r = new Runnable() {
        @Override
        public void run() {
          noteLabel.setVisible(newValue != null);
          validate();
        }
      };
      SwingUtilities.invokeLater(r);
    }
  }

  public String getNote() {
    return noteLabel.getText();
  }

  public void setProgressModel(BoundedRangeModel m) {
    // BoundedRangeModel oldValue = progressModel;
    progressModel = m;
    progressBar.setModel(m);
  }

  /**
   * Returns the minimum value -- the lower end of the progress value.
   *
   * @return an int representing the minimum value
   * @see #setMinimum
   */
  public int getMinimum() {
    return progressModel.getMinimum();
  }

  /**
   * Specifies the minimum value.
   *
   * @param m an int specifying the minimum value
   * @see #getMinimum
   */
  public void setMinimum(int m) {
    progressModel.setMinimum(m);
  }

  /**
   * Indicate the progress of the operation being monitored.
   *
   * @param nv an int specifying the current value, between the maximum and minimum specified for
   *     this component
   */
  public void setProgress(int nv) {
    progressModel.setValue(nv);
  }

  /**
   * Returns the maximum value -- the higher end of the progress value.
   *
   * @return an int representing the maximum value
   * @see #setMaximum
   */
  public int getMaximum() {
    return progressModel.getMaximum();
  }

  /**
   * Specifies the maximum value.
   *
   * @param m an int specifying the maximum value
   * @see #getMaximum
   */
  public void setMaximum(int m) {
    progressModel.setMaximum(m);
  }

  public void setIndeterminate(boolean b) {
    progressBar.setIndeterminate(b);
  }

  public boolean isIndeterminate() {
    return progressBar.isIndeterminate();
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT
   * modify this code. The content of this method is always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;
    messageLabel = new javax.swing.JLabel();
    noteLabel = new javax.swing.JLabel();
    progressBar = new javax.swing.JProgressBar();
    setBackground(new java.awt.Color(255, 255, 255));
    setLayout(new java.awt.GridBagLayout());
    messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    add(messageLabel, gridBagConstraints);
    noteLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
    add(noteLabel, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
    add(progressBar, gridBagConstraints);
  } // </editor-fold>//GEN-END:initComponents

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JLabel messageLabel;
  private javax.swing.JLabel noteLabel;
  private javax.swing.JProgressBar progressBar;
  // End of variables declaration//GEN-END:variables
}
