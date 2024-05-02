/*
 * @(#)JActivityView.java  2.1 2011-05-01
 *
 * Copyright (c) 2011 The authors and contributors of JHotDraw.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with the copyright holders. For details
 * see accompanying license terms.
 */
package org.jhotdraw.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import javax.swing.event.*;
import org.jhotdraw.api.gui.ActivityModel;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * A class to view an {@code ActivityModel}.
 *
 * <p>This view is used by {@code JActivityWindow}. <hr> <b>Design Patterns</b>
 *
 * <p><em>Framework</em><br>
 * The interfaces and classes listed below define a framework for progress management.<br>
 * Contract: {@link ActivityManager}, {@link ActivityModel}, {@link JActivityWindow}, {@link
 * JActivityIndicator}.
 */
public class JActivityView extends javax.swing.JPanel {

  private static final long serialVersionUID = 1L;
  public static final String REQUEST_REMOVE_PROPERTY = "requestRemove";
  private ActivityModel model;

  private class Handler implements PropertyChangeListener, ChangeListener {

    @Override
    public void stateChanged(ChangeEvent e) {
      /*
      if (model != null && model.getValue() >= model.getMaximum()) {
      close();
      }*/
    }

    /** Thread safe. */
    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
      ActivityManager.invokeAndWait(new Runnable() {
        @Override
        public void run() {
          updateProperties(evt);
        }
      });
    }
  }
  ;

  private Handler handler = new Handler();
  private ResourceBundleUtil labels;

  /** Creates a new JActivityView. */
  public JActivityView() {
    this(null);
  }

  /** Creates a new JActivityView. */
  public JActivityView(ActivityModel model) {
    labels = ResourceBundleUtil.getBundle("org.jhotdraw.gui.Labels");
    initComponents();
    closeButton.setVisible(false);
    setModel(model);
  }

  public void setModel(ActivityModel newValue) {
    if (model != null) {
      model.removeChangeListener(handler);
      model.removePropertyChangeListener(handler);
      progressBar.setModel(new DefaultBoundedRangeModel());
    }
    model = newValue;
    progressBar.setModel(newValue);
    if (model != null) {
      model.addChangeListener(handler);
      model.addPropertyChangeListener(handler);
      updateTitle();
      updateProperties(null);
      progressBar.setModel(model);
    }
  }

  public ActivityModel getModel() {
    return model;
  }

  private void updateProperties(PropertyChangeEvent evt) {
    if (evt == null || evt.getPropertyName() == null) {
      updateNote();
      updateWarning();
      updateError();
      updateCancelable();
      updateCanceled();
      updateClosed();
      updateIndeterminate();
      return;
    }
    String name = evt.getPropertyName();
    if ((name == null && ActivityModel.NOTE_PROPERTY == null)
        || (name != null && name.equals(ActivityModel.NOTE_PROPERTY))) {
      updateNote();
    } else if ((name == null && ActivityModel.WARNING_PROPERTY == null)
        || (name != null && name.equals(ActivityModel.WARNING_PROPERTY))) {
      updateWarning();
    } else if ((name == null && ActivityModel.ERROR_PROPERTY == null)
        || (name != null && name.equals(ActivityModel.ERROR_PROPERTY))) {
      updateError();
    } else if ((name == null && ActivityModel.CANCELABLE_PROPERTY == null)
        || (name != null && name.equals(ActivityModel.CANCELABLE_PROPERTY))) {
      updateCancelable();
    } else if ((name == null && ActivityModel.CANCELED_PROPERTY == null)
        || (name != null && name.equals(ActivityModel.CANCELED_PROPERTY))) {
      updateCanceled();
      updateCancelable();
    } else if ((name == null && ActivityModel.INDETERMINATE_PROPERTY == null)
        || (name != null && name.equals(ActivityModel.INDETERMINATE_PROPERTY))) {
      updateIndeterminate();
    } else if ((name == null && ActivityModel.CLOSED_PROPERTY == null)
        || (name != null && name.equals(ActivityModel.CLOSED_PROPERTY))) {
      updateCancelable();
      updateCanceled();
      updateClosed();
    }
  }

  private void updateTitle() {
    titleLabel.setText(model.getTitle());
  }

  private void updateNote() {
    String txt = model.getNote();
    noteLabel.setText(model.getNote());
  }

  private void updateIndeterminate() {
    progressBar.setIndeterminate(model.isIndeterminate());
  }

  private void updateWarning() {
    String txt = model.getWarning();
    warningLabel.setText(txt);
    updateLabelVisibility();
  }

  private void updateError() {
    String txt = model.getError();
    errorLabel.setText(txt);
    updateLabelVisibility();
  }

  private void updateLabelVisibility() {
    boolean isError = model.getError() != null;
    boolean isWarning = model.getWarning() != null;
    errorLabel.setVisible(isError);
    warningLabel.setVisible(!isError && isWarning);
    noteLabel.setVisible(!isError && !isWarning);
    revalidate();
  }

  private void updateCancelable() {
    boolean b = model.isCancelable() && !model.isClosed();
    if (cancelButton.isVisible() != b) {
      cancelButton.setVisible(b);
      revalidate();
    }
  }

  private void updateCanceled() {
    boolean b = model.isCancelable() && !model.isCanceled() && !model.isClosed();
    if (cancelButton.isEnabled() != b) {
      cancelButton.setEnabled(b);
      revalidate();
    }
  }

  private void updateClosed() {
    boolean b = model.isClosed();
    if (progressBar.isEnabled() == b) {
      closeButton.setVisible(model.getError() != null);
      progressBar.setEnabled(!b);
      revalidate();
    }
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT
   * modify this code. The content of this method is always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;
    titleLabel = new javax.swing.JLabel();
    noteLabel = new javax.swing.JLabel();
    warningLabel = new javax.swing.JLabel();
    errorLabel = new javax.swing.JLabel();
    progressBar = new javax.swing.JProgressBar();
    cancelButton = new javax.swing.JButton();
    closeButton = new javax.swing.JButton();
    separator = new javax.swing.JSeparator();
    setLayout(new java.awt.GridBagLayout());
    titleLabel.setFont(
        titleLabel.getFont().deriveFont(titleLabel.getFont().getStyle() | java.awt.Font.BOLD));
    titleLabel.setText("title");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 12);
    add(titleLabel, gridBagConstraints);
    noteLabel.setFont(noteLabel.getFont().deriveFont(noteLabel.getFont().getSize() - 2f));
    noteLabel.setText("note");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 12, 0, 12);
    add(noteLabel, gridBagConstraints);
    warningLabel.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    warningLabel.setIcon(new javax.swing.ImageIcon(
        getClass().getResource("/org/jhotdraw/gui/images/ProgressView.warningIcon.png"))); // NOI18N
    warningLabel.setText("warning");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 12, 0, 12);
    add(warningLabel, gridBagConstraints);
    errorLabel.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    errorLabel.setIcon(new javax.swing.ImageIcon(
        getClass().getResource("/org/jhotdraw/gui/images/ProgressView.errorIcon.png"))); // NOI18N
    errorLabel.setText("error");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 12, 0, 12);
    add(errorLabel, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(12, 12, 12, 12);
    add(progressBar, gridBagConstraints);
    cancelButton.setText(labels.getString("ActivityView.cancel.text")); // NOI18N
    cancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cancel(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(12, 0, 12, 12);
    add(cancelButton, gridBagConstraints);
    closeButton.setText(labels.getString("ActivityView.close.text")); // NOI18N
    closeButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        close(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(12, 0, 12, 12);
    add(closeButton, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
    gridBagConstraints.weighty = 1.0;
    add(separator, gridBagConstraints);
  } // </editor-fold>//GEN-END:initComponents

  private void close(java.awt.event.ActionEvent evt) { // GEN-FIRST:event_close
    closeButton.setEnabled(false);
    putClientProperty(REQUEST_REMOVE_PROPERTY, true);
  } // GEN-LAST:event_close

  private void cancel(java.awt.event.ActionEvent evt) { // GEN-FIRST:event_cancel
    model.cancel();
    model.setNote("Cancelling...");
  } // GEN-LAST:event_cancel

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton cancelButton;
  private javax.swing.JButton closeButton;
  private javax.swing.JLabel errorLabel;
  private javax.swing.JLabel noteLabel;
  private javax.swing.JProgressBar progressBar;
  private javax.swing.JSeparator separator;
  private javax.swing.JLabel titleLabel;
  private javax.swing.JLabel warningLabel;
  // End of variables declaration//GEN-END:variables
}
