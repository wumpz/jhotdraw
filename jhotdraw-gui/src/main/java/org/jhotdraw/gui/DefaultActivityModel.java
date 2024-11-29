/*
 * @(#)DefaultActivityModel.java
 *
 * Copyright (c) 2011 The authors and contributors of JHotDraw.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with the copyright holders. For details
 * see accompanying license terms.
 */
package org.jhotdraw.gui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Formatter;
import java.util.Locale;
import javax.swing.DefaultBoundedRangeModel;
import org.jhotdraw.api.gui.ActivityModel;
import org.jhotdraw.utils.beans.WeakPropertyChangeListener;

/** Default implementation of {@link ActivityModel}. */
public class DefaultActivityModel extends DefaultBoundedRangeModel implements ActivityModel {

  private static final long serialVersionUID = 1L;
  private boolean canceled, closed, cancelable = true;
  private Runnable doCancel;
  private final String title;
  private String note;
  private boolean isIndeterminate;
  private String warning;
  private String error;
  private Formatter formatter;
  private final Object owner;
  protected PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);

  /** Creates a new DefaultActivityModel. */
  public DefaultActivityModel(Object owner, String title, String note, boolean isIndeterminate) {
    this(owner, title, note, 0, 100, isIndeterminate);
  }

  /** Creates a new DefaultActivityModel. */
  public DefaultActivityModel(Object owner, String title, String note, int min, int max) {
    this(owner, title, note, min, max, false);
  }

  /** Creates a new DefaultActivityModel. */
  public DefaultActivityModel(
      Object owner,
      String title,
      String note,
      int min,
      final int max,
      final boolean isIndeterminate) {
    super(min, 0, min, max);
    this.owner = owner;
    this.title = title;
    this.note = note;
    this.isIndeterminate = isIndeterminate;
    ActivityManager.getInstance().add(this);
  }

  /** Creates a new indeterminate DefaultActivityModel. */
  public DefaultActivityModel(Object owner, String title) {
    this(owner, title, null, 0, 100, true);
  }

  /** Set cancelable to false if the operation can not be canceled. */
  @Override
  public void setCancelable(boolean newValue) {
    boolean oldValue = cancelable;
    cancelable = newValue;
    firePropertyChange(CANCELABLE_PROPERTY, oldValue, newValue);
  }

  /** The specified Runnable is executed when the user presses the cancel button. */
  @Override
  public void setDoCancel(Runnable doCancel) {
    this.doCancel = doCancel;
  }

  /** Indicate that the operation is closed. */
  @Override
  public void close() {
    if (!closed) {
      closed = true;
      firePropertyChange(CLOSED_PROPERTY, false, true);
      ActivityManager.getInstance().remove(this);
    }
  }

  /** Closes the progress view. */
  /** Returns true if the user has hit the Cancel button in the progress dialog. */
  @Override
  public boolean isCanceled() {
    return canceled;
  }

  /** Returns true if the operation is completed. */
  @Override
  public boolean isClosed() {
    return closed;
  }

  /** Cancels the operation. This method must be invoked from the user event dispatch thread. */
  @Override
  public void cancel() {
    if (cancelable && !canceled) {
      canceled = true;
      firePropertyChange(CANCELED_PROPERTY, false, true);
      if (doCancel != null) {
        doCancel.run();
      }
    }
  }

  /**
   * Specifies the additional note that is displayed along with the progress message. Used, for
   * example, to show which file the is currently being copied during a multiple-file copy.
   *
   * @param newValue a String specifying the note to display
   * @see #getNote
   */
  @Override
  public void setNote(String newValue) {
    String oldValue = note;
    this.note = newValue;
    firePropertyChange(NOTE_PROPERTY, oldValue, newValue);
  }

  /**
   * Specifies the additional note that is displayed along with the progress message.
   *
   * @return a String specifying the note to display
   * @see #setNote
   */
  @Override
  public String getNote() {
    return note;
  }

  @Override
  public void setWarning(String newValue) {
    String oldValue = warning;
    this.warning = newValue;
    firePropertyChange(WARNING_PROPERTY, oldValue, newValue);
  }

  @Override
  public String getWarning() {
    return warning;
  }

  @Override
  public void setError(String newValue) {
    String oldValue = error;
    this.error = newValue;
    firePropertyChange(ERROR_PROPERTY, oldValue, newValue);
  }

  @Override
  public String getError() {
    return error;
  }

  @Override
  public void setIndeterminate(boolean newValue) {
    boolean oldValue = isIndeterminate;
    isIndeterminate = newValue;
    firePropertyChange(INDETERMINATE_PROPERTY, oldValue, newValue);
  }

  @Override
  public boolean isIndeterminate() {
    return isIndeterminate;
  }

  @Override
  public void printf(String format, Object... args) {
    if ((formatter == null) || (formatter.locale() != Locale.getDefault())) {
      formatter = new Formatter();
    }
    formatter.format(Locale.getDefault(), format, args);
    StringBuilder buf = (StringBuilder) formatter.out();
    setNote(buf.toString());
  }

  @Override
  public Object getOwner() {
    return owner;
  }

  @Override
  public String getTitle() {
    return title;
  }

  /**
   * Adds a {@code PropertyChangeListener} which can optionally be wrapped into a {@code
   * WeakPropertyChangeListener}.
   *
   * @param listener
   */
  @Override
  public void addPropertyChangeListener(PropertyChangeListener listener) {
    propertySupport.addPropertyChangeListener(listener);
  }

  /**
   * Adds a {@code PropertyChangeListener} which can optionally be wrapped into a {@code
   * WeakPropertyChangeListener}.
   *
   * @param listener
   */
  public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    propertySupport.addPropertyChangeListener(propertyName, listener);
  }

  /**
   * Removes a {@code PropertyChangeListener}. If the listener was added wrapped into a {@code
   * WeakPropertyChangeListener}, the {@code WeakPropertyChangeListener} is removed.
   *
   * @param listener
   */
  @Override
  public void removePropertyChangeListener(PropertyChangeListener listener) {
    // Removes a property change listener from our list.
    // We need a somewhat complex procedure here in case a listener
    // has been registered using addPropertyChangeListener(new
    // WeakPropertyChangeListener(listener));
    for (PropertyChangeListener l : propertySupport.getPropertyChangeListeners()) {
      if (l == listener) {
        propertySupport.removePropertyChangeListener(l);
        break;
      }
      if (l instanceof WeakPropertyChangeListener) {
        WeakPropertyChangeListener wl = (WeakPropertyChangeListener) l;
        PropertyChangeListener target = wl.getTarget();
        if (target == listener) {
          propertySupport.removePropertyChangeListener(l);
          break;
        }
      }
    }
  }

  protected void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
    propertySupport.firePropertyChange(propertyName, oldValue, newValue);
  }

  protected void firePropertyChange(String propertyName, int oldValue, int newValue) {
    propertySupport.firePropertyChange(propertyName, oldValue, newValue);
  }

  protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    propertySupport.firePropertyChange(propertyName, oldValue, newValue);
  }

  @Override
  public boolean isCancelable() {
    return cancelable;
  }
}
